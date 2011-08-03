/*
 * Copyright 2000-2011 JetBrains s.r.o.
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
package com.intellij.application.options;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettings;

/**
 * @author Rustam Vishnyakov
 */
public class JavaCodeStyleMainPanel extends MultiTabCodeStyleAbstractPanel {
  protected JavaCodeStyleMainPanel(CodeStyleSettings settings) {
    super(settings);
  }

  @Override
  public Language getDefaultLanguage() {
    return JavaLanguage.INSTANCE;
  }

  @Override
  protected void initTabs(CodeStyleSettings settings) {
    super.initTabs(settings);
    addTab(new JavaDocFormattingPanel(settings));
    addTab(new CodeStyleImportsPanelWrapper(settings));
    addTab(new CodeStyleGenerationWrapper(settings));
  }
}