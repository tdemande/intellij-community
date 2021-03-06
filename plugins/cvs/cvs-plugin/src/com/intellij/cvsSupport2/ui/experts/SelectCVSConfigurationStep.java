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
package com.intellij.cvsSupport2.ui.experts;

import com.intellij.CvsBundle;
import com.intellij.cvsSupport2.config.CvsRootConfiguration;
import com.intellij.cvsSupport2.config.ui.SelectCvsConfigurationPanel;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * author: lesya
 */
public class SelectCVSConfigurationStep extends WizardStep{
  private final SelectCvsConfigurationPanel mySelectCvsConfigurationPanel;
  private final ListSelectionListener myListSelectionListener;

  public SelectCVSConfigurationStep(Project project, CvsWizard wizard) {
    super(CvsBundle.message("dialog.title.select.cvs.configuration"), wizard);
    mySelectCvsConfigurationPanel = new SelectCvsConfigurationPanel(project);
    myListSelectionListener = new ListSelectionListener() {
      @Override public void valueChanged(ListSelectionEvent e) {
        getWizard().updateStep();
      }
    };
    mySelectCvsConfigurationPanel.addListSelectionListener(myListSelectionListener);
    init();
  }

  @Override
  protected void dispose() {
    mySelectCvsConfigurationPanel.removeListSelectionListener(myListSelectionListener);
  }

  @Override
  public boolean nextIsEnabled() {
    return mySelectCvsConfigurationPanel.getSelectedConfiguration() != null;
  }

  @Override
  public boolean setActive() {
    return true;
  }

  @Override
  protected JComponent createComponent() {
    return mySelectCvsConfigurationPanel;
  }

  public CvsRootConfiguration getSelectedConfiguration() {
    return mySelectCvsConfigurationPanel.getSelectedConfiguration();
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return mySelectCvsConfigurationPanel.getPreferredFocusedComponent();
  }
}
