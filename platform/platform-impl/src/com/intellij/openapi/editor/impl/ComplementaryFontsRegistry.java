/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.editor.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.SystemInfo;
import gnu.trove.TIntHashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author max
 */
public class ComplementaryFontsRegistry {
  private static final Object lock = new String("common lock");
  private static final List<String> ourFontNames;
  private static final Set<String> ourStyledFontNames;
  private static final LinkedHashMap<FontKey, FontInfo> ourUsedFonts;
  private static FontKey ourSharedKeyInstance = new FontKey("", 0, 0);
  private static FontInfo ourSharedDefaultFont;
  private static final TIntHashSet ourUndisplayableChars = new TIntHashSet();

  private ComplementaryFontsRegistry() {
  }

  private static class FontKey {
    public String myFamilyName;
    public int mySize;
    public int myStyle;

    public FontKey(@NotNull String familyName, final int size, final int style) {
      myFamilyName = familyName;
      mySize = size;
      myStyle = style;
    }

    public boolean equals(final Object o) {
      if (this == o) return true;
      final FontKey fontKey = (FontKey)o;

      if (mySize != fontKey.mySize) return false;
      if (myStyle != fontKey.myStyle) return false;
      return myFamilyName.equals(fontKey.myFamilyName);
    }

    public int hashCode() {
      int result = myFamilyName.hashCode();
      result = 29 * result + mySize;
      result = 29 * result + myStyle;
      return result;
    }
  }

  @NonNls private static final String BOLD_SUFFIX = ".bold";

  @NonNls private static final String ITALIC_SUFFIX = ".italic";

  static {
    ourStyledFontNames = new HashSet<String>();
    ourFontNames = new ArrayList<String>();
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      ourFontNames.add("Monospaced");
    } else {
      GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (SystemInfo.isMac) {
        Font[] allFonts = graphicsEnvironment.getAllFonts();
        for (Font font : allFonts) {
          String name = font.getName();
          if (name.endsWith("-Italic") || name.endsWith("-Bold") || name.endsWith("-BoldItalic")) {
            ourStyledFontNames.add(font.getName());
          }
        }
      }

      String[] fontNames = graphicsEnvironment.getAvailableFontFamilyNames();
      for (final String fontName : fontNames) {
        if (!fontName.endsWith(BOLD_SUFFIX) && !fontName.endsWith(ITALIC_SUFFIX)) {
          ourFontNames.add(fontName);
        }
      }
    }
    ourUsedFonts = new LinkedHashMap<FontKey, FontInfo>();
  }

  private static Pair<String, Integer> fontFamily(String familyName, int style) {
    if (!SystemInfo.isMac || style == 0) return Pair.create(familyName, style);

    StringBuilder st = new StringBuilder(familyName).append('-');
    if ((style & Font.BOLD) == Font.BOLD) {
      st.append("Bold");
    }
    
    if ((style & Font.ITALIC) == Font.ITALIC) {
      st.append("Italic");
    }
    
    String styledFamilyName = st.toString();
    boolean found = ourStyledFontNames.contains(styledFamilyName);
    return Pair.create(found ? styledFamilyName : familyName, found ? Font.PLAIN : style);
  }
  
  public static FontInfo getFontAbleToDisplay(char c, int size, int style, @NotNull String defaultFontFamily) {
    synchronized (lock) {
      Pair<String, Integer> p = fontFamily(defaultFontFamily, style);
      if (ourSharedKeyInstance.mySize == size &&
          ourSharedKeyInstance.myStyle == p.getSecond() &&
          ourSharedKeyInstance.myFamilyName != null &&
          ourSharedKeyInstance.myFamilyName.equals(p.getFirst()) &&
          ourSharedDefaultFont != null &&
          ( c < 128 ||
            ourSharedDefaultFont.canDisplay(c)
          )
         ) {
        return ourSharedDefaultFont;
      }

      ourSharedKeyInstance.myFamilyName = p.getFirst();
      ourSharedKeyInstance.mySize = size;
      ourSharedKeyInstance.myStyle = p.getSecond();

      FontInfo defaultFont = ourUsedFonts.get(ourSharedKeyInstance);
      if (defaultFont == null) {
        defaultFont = new FontInfo(p.getFirst(), size, p.getSecond());
        ourUsedFonts.put(ourSharedKeyInstance, defaultFont);
        ourSharedKeyInstance = new FontKey("", 0, 0);
      }

      ourSharedDefaultFont = defaultFont;
      if (c < 128 || defaultFont.canDisplay(c)) {
        return defaultFont;
      }

      if (ourUndisplayableChars.contains(c)) return defaultFont;

      final Collection<FontInfo> descriptors = ourUsedFonts.values();
      for (FontInfo font : descriptors) {
        if (font.getSize() == size && font.getStyle() == style && font.canDisplay(c)) {
          return font;
        }
      }

      for (int i = 0; i < ourFontNames.size(); i++) {
        String name = ourFontNames.get(i);
        FontInfo font = new FontInfo(name, size, style);
        if (font.canDisplay(c)) {
          ourUsedFonts.put(new FontKey(name, size, style), font);
          ourFontNames.remove(i);
          return font;
        }
      }

      ourUndisplayableChars.add(c);

      return defaultFont;
    }
  }
}
