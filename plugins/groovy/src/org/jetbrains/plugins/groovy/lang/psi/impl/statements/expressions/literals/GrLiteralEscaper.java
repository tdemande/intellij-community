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

package org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.literals;

import com.intellij.openapi.util.ProperTextRange;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes;
import org.jetbrains.plugins.groovy.lang.psi.util.GrStringUtil;

public class GrLiteralEscaper extends LiteralTextEscaper<GrLiteralImpl> {
  private int[] outSourceOffsets;

  public GrLiteralEscaper(final GrLiteralImpl literal) {
    super(literal);
  }

  @Override
  public boolean decode(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
    ProperTextRange.assertProperRange(rangeInsideHost);
    String subText = rangeInsideHost.substring(myHost.getText());
    outSourceOffsets = new int[subText.length() + 1];
    return GrStringUtil.parseStringCharacters(subText, outChars, outSourceOffsets, isStrictBackSlash());
  }

  @Override
  public int getOffsetInHost(int offsetInDecoded, @NotNull final TextRange rangeInsideHost) {
    int result = offsetInDecoded < outSourceOffsets.length ? outSourceOffsets[offsetInDecoded] : -1;
    if (result == -1) return -1;
    return (result <= rangeInsideHost.getLength() ? result : rangeInsideHost.getLength()) + rangeInsideHost.getStartOffset();
  }

  protected boolean isStrictBackSlash() {
    PsiElement child = myHost.getFirstChild();
    return child == null || child.getNode().getElementType() != GroovyTokenTypes.mREGEX_LITERAL;
  }

  public boolean isOneLine() {
    final Object value = myHost.getValue();
    return value instanceof String && ((String)value).indexOf('\n') < 0;
  }
}
