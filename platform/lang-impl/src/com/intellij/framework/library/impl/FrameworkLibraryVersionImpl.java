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
package com.intellij.framework.library.impl;

import com.intellij.framework.library.DownloadableFileDescription;
import com.intellij.framework.library.FrameworkLibraryVersion;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author nik
 */
public class FrameworkLibraryVersionImpl implements FrameworkLibraryVersion {
  private final List<DownloadableFileDescription> myLibraryFiles;
  private final String myVersionString;
  private final String myLibraryCategory;
  private final FrameworkVersion myFrameworkVersion;

  public FrameworkLibraryVersionImpl(String versionString,
                                     List<DownloadableFileDescription> libraryFiles,
                                     String category,
                                     @Nullable FrameworkVersion frameworkVersion) {
    myVersionString = versionString;
    myLibraryFiles = libraryFiles;
    myLibraryCategory = category;
    myFrameworkVersion = frameworkVersion;
  }

  @NotNull
  @Override
  public String getVersionString() {
    return myVersionString;
  }

  @NotNull
  @Override
  public String getDefaultLibraryName() {
    return myVersionString.length() > 0 ? myLibraryCategory + "-" + myVersionString : myLibraryCategory;
  }

  @NotNull
  @Override
  public List<DownloadableFileDescription> getLibraryFiles() {
    return myLibraryFiles;
  }

  @Override
  public boolean isCompatibleWith(@NotNull FrameworkVersion frameworkVersion) {
    return myFrameworkVersion == null || myFrameworkVersion.getVersionName().equals(frameworkVersion.getVersionName());
  }
}