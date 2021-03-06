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
package com.intellij.openapi.roots.impl;

import com.intellij.internal.statistic.AbstractApplicationUsagesCollector;
import com.intellij.internal.statistic.beans.GroupDescriptor;
import com.intellij.internal.statistic.beans.UsageDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class LanguageLevelUsagesCollector extends AbstractApplicationUsagesCollector {
  public static final String GROUP_ID = "language-level";

  @NotNull
  @Override
  public GroupDescriptor getGroupId() {
    return GroupDescriptor.create(GROUP_ID, GroupDescriptor.HIGHER_PRIORITY);
  }


  @NotNull
  public Set<UsageDescriptor> getProjectUsages(@NotNull Project project) {

    final Set<String> languageLevels = new HashSet<String>();
    for (Module module : ModuleManager.getInstance(project).getModules()) {
      final LanguageLevelModuleExtension instance = LanguageLevelModuleExtension.getInstance(module);
      final LanguageLevel languageLevel = instance.getLanguageLevel();
      if (languageLevel != null) {
        languageLevels.add(languageLevel.getPresentableText());
      }
    }
    languageLevels.add(LanguageLevelProjectExtension.getInstance(project).getLanguageLevel().getPresentableText());

    return ContainerUtil.map2Set(languageLevels, new Function<String, UsageDescriptor>() {
      @Override
      public UsageDescriptor fun(String languageLevel) {
        return new UsageDescriptor(languageLevel, 1);
      }
    });
  }
}
