package org.jetbrains.plugins.gradle.importing.model.impl;

import com.intellij.pom.java.LanguageLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.importing.model.GradleModule;
import org.jetbrains.plugins.gradle.importing.model.GradleProject;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Not thread-safe.
 * 
 * @author Denis Zhdanov
 * @since 8/1/11 1:30 PM
 */
public class GradleProjectImpl implements Serializable, GradleProject {

  private static final long serialVersionUID = 1L;
  
  private static final LanguageLevel DEFAULT_LANGUAGE_LEVEL = LanguageLevel.JDK_1_6;
  private static final String        DEFAULT_JDK            = "1.6";

  private final Set<GradleModuleImpl> myModules = new HashSet<GradleModuleImpl>();

  private final String myCompileOutputPath;
  
  private String        myName          = "unnamed";
  private String        myJdk           = DEFAULT_JDK;
  private LanguageLevel myLanguageLevel = DEFAULT_LANGUAGE_LEVEL;

  public GradleProjectImpl(@NotNull String compileOutputPath) {
    myCompileOutputPath = new File(compileOutputPath).getAbsolutePath();
  }

  @NotNull
  @Override
  public String getName() {
    return myName;
  }

  public void setName(@NotNull String name) {
    myName = name;
  }

  @NotNull
  @Override
  public String getCompileOutputPath() {
    return myCompileOutputPath;
  }

  @NotNull
  @Override
  public String getJdkName() {
    return myJdk;
  }

  public void setJdk(@Nullable String jdk) {
    if (jdk != null) {
      myJdk = jdk;
    }
  }

  @NotNull
  @Override
  public LanguageLevel getLanguageLevel() {
    return myLanguageLevel;
  }

  public void setLanguageLevel(@Nullable String languageLevel) {
    LanguageLevel level = LanguageLevel.parse(languageLevel);
    if (level != null) {
      myLanguageLevel = level;
    } 
  }

  public void addModule(@NotNull GradleModuleImpl module) {
    myModules.add(module);
  }
  
  @NotNull
  @Override
  public Set<? extends GradleModule> getModules() {
    return myModules;
  }

  @Override
  public String toString() {
    return String.format("project '%s'. Jdk: '%s', language level: '%s', modules: %s",
                         getName(), getJdkName(), getLanguageLevel(), getModules());
  }
}