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

package com.intellij.codeInsight.completion.simple;

import com.intellij.codeInsight.TailType;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;

/**
 * @author peter
 */
public abstract class ParenthesesTailType extends TailType {

  protected abstract boolean isSpaceBeforeParentheses(CodeStyleSettings styleSettings, Editor editor, final int tailOffset);

  protected abstract boolean isSpaceWithinParentheses(CodeStyleSettings styleSettings, Editor editor, final int tailOffset);

  public int processTail(final Editor editor, int tailOffset) {
    CodeStyleSettings styleSettings = CodeStyleSettingsManager.getSettings(editor.getProject());
    if (isSpaceBeforeParentheses(styleSettings, editor, tailOffset)) {
      tailOffset = insertChar(editor, tailOffset, ' ');
    }
    tailOffset = insertChar(editor, tailOffset, '(');
    if (isSpaceWithinParentheses(styleSettings, editor, tailOffset)) {
      tailOffset = insertChar(editor, tailOffset, ' ');
      tailOffset = insertChar(editor, tailOffset, ' ');
      tailOffset = insertChar(editor, tailOffset, ')');
      moveCaret(editor, tailOffset, -2);
    } else {
      tailOffset = insertChar(editor, tailOffset, ')');
      moveCaret(editor, tailOffset, -1);
    }
    return tailOffset;
  }

}