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
package com.intellij.unscramble;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.configurable.VcsContentAnnotationConfigurable;

/**
* Created by IntelliJ IDEA.
* User: Irina.Chernushina
* Date: 8/4/11
* Time: 2:29 PM
* To change this template use File | Settings | File Templates.
*/
public class ShowRecentlyChanged extends DumbAwareAction {
  public ShowRecentlyChanged() {
    super("Show recently changed", "Show recently changed", IconLoader.getIcon("/general/copy.png"));
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    if (! enabled(e)) return;
    Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());
    VcsContentAnnotationConfigurable configurable = new VcsContentAnnotationConfigurable(project);
    ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
    // todo recalculate highlight
  }

  private boolean enabled(AnActionEvent e) {
    Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());
    if (project == null) return false;
    ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance(project);
    if (! vcsManager.hasActiveVcss()) return false;
    return true;
  }


  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(enabled(e));
  }
}
