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
package com.intellij.openapi.roots.ui.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PlatformIcons;

import javax.swing.*;

public class HttpUrlCellAppearance extends ValidFileCellAppearance {
  public HttpUrlCellAppearance(VirtualFile file) {
    super(file);
  }

  protected Icon getIcon() {
    return PlatformIcons.WEB_ICON;
  }
}
