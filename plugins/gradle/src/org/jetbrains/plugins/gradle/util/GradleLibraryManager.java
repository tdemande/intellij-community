package org.jetbrains.plugins.gradle.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.config.GradleSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Encapsulates algorithm of gradle libraries discovery.
 * <p/>
 * Thread-safe.
 * <p/>
 * This class is not singleton but offers single-point-of-usage field - {@link #INSTANCE}.
 * 
 * @author Denis Zhdanov
 * @since 8/4/11 11:06 AM
 */
@SuppressWarnings("MethodMayBeStatic")
public class GradleLibraryManager {

  /** Shared instance of the current (stateless) class. */
  public static final GradleLibraryManager INSTANCE = new GradleLibraryManager();

  private static final Pattern GRADLE_JAR_FILE_PATTERN;
  private static final Pattern ANY_GRADLE_JAR_FILE_PATTERN;
  private static final String[] GRADLE_START_FILE_NAMES;
  @NonNls private static final String GRADLE_ENV_PROPERTY_NAME;
  static {
    // Init static data with ability to redefine it locally.
    GRADLE_JAR_FILE_PATTERN = Pattern.compile(System.getProperty("gradle.pattern.core.jar", "gradle-(core-)?(\\d.*)\\.jar"));
    ANY_GRADLE_JAR_FILE_PATTERN = Pattern.compile(System.getProperty("gradle.pattern.core.jar", "gradle-(.*)\\.jar"));
    GRADLE_START_FILE_NAMES = System.getProperty("gradle.start.file.names", "gradle|gradle.cmd|gradle.sh").split("|");
    GRADLE_ENV_PROPERTY_NAME = System.getProperty("gradle.home.env.key", "GRADLE_HOME");
  }

  /**
   * Allows to get file handles for the gradle binaries to use.
   *
   * @param project  target project
   * @return         file handles for the gradle binaries; <code>null</code> if gradle is not discovered
   */
  @Nullable
  public Collection<File> getAllLibraries(@Nullable Project project) {

    // Manually defined gradle home
    File gradleHome = getGradleHome(project);

    if (gradleHome == null || gradleHome.isDirectory()) {
      return null;
    }

    File libs = new File(gradleHome, "lib");
    File[] files = libs.listFiles();
    if (files == null) {
      return null;
    }
    List<File> result = new ArrayList<File>();
    for (File file : files) {
      if (file.getName().endsWith(".jar")) {
        result.add(file);
      }
    }
    return result;
  }

  /**
   * Tries to return file handle that points to the gradle installation home.
   * 
   * @param project  target project (if any)
   * @return         file handle that points to the gradle installation home (if any)
   */
  @Nullable
  public File getGradleHome(@Nullable Project project) {
    File result = getManuallyDefinedGradleHome(project);
    if (result != null) {
      return result;
      
    }
    result = getGradleHomeFromPath();
    return result == null ? getGradleHomeFromEnvProperty() : result;
  }
  
  /**
   * Allows to ask for user-defined path to gradle.
   *  
   * @param project  target project to use (if any)
   * @return         path to the gradle distribution (if the one is explicitly configured)
   */
  @Nullable
  public File getManuallyDefinedGradleHome(@Nullable Project project) {
    if (project == null) {
      return null;
    }
    GradleSettings settings = GradleSettings.getInstance(project);
    String path = settings.INSTALLATION_HOME;
    if (path == null) {
      return null;
    }
    File candidate = new File(path);
    return isGradleSdkHome(candidate) ? candidate : null;
  }

  /**
   * Tries to discover gradle installation path from the configured system path
   * 
   * @return    file handle for the gradle directory if it's possible to deduce from the system path; <code>null</code> otherwise
   */
  @Nullable
  public File getGradleHomeFromPath() {
    String path = System.getenv("PATH");
    if (path == null) {
      return null;
    }
    for (String pathEntry : path.split(File.separator)) {
      File dir = new File(pathEntry);
      if (!dir.isDirectory()) {
        continue;
      }
      for (String fileName : GRADLE_START_FILE_NAMES) {
        File startFile = new File(dir, fileName);
        if (startFile.isFile()) {
          File candidate = dir.getParentFile();
          if (isGradleSdkHome(candidate)) {
            return candidate;
          } 
        } 
      }
    }
    return null;
  }

  /**
   * Tries to discover gradle installation via environment property.
   * 
   * @return    file handle for the gradle directory deduced from the system property (if any)
   */
  @Nullable
  public File getGradleHomeFromEnvProperty() {
    String path = System.getenv(GRADLE_ENV_PROPERTY_NAME);
    if (path == null) {
      return null;
    }
    File candidate = new File(path);
    return isGradleSdkHome(candidate) ? candidate : null;
  }

  /**
   * Does the same job as {@link #isGradleSdkHome(File)} for the given virtual file.
   * 
   * @param file  gradle installation home candidate
   * @return      <code>true</code> if given file points to the gradle installation; <code>false</code> otherwise
   */
  public boolean isGradleSdkHome(@Nullable VirtualFile file) {
    if (file == null) {
      return false;
    }
    return isGradleSdkHome(new File(file.getPath()));
  }
  
  /**
   * Allows to answer if given virtual file points to the gradle installation root.
   *  
   * @param file  gradle installation root candidate
   * @return      <code>true</code> if we consider that given file actually points to the gradle installation root;
   *              <code>false</code> otherwise
   */
  public boolean isGradleSdkHome(@Nullable File file) {
    if (file == null) {
      return false;
    }
    final File libs = new File(file, "lib");
    if (!libs.isDirectory()) {
      return false;
    }

    return isGradleSdk(libs.listFiles());
  }

  /**
   * Allows to answer if given files contain the one from gradle installation.
   * 
   * @param files  files to process
   * @return       <code>true</code> if one of the given files is from the gradle installation; <code>false</code> otherwise
   */
  public boolean isGradleSdk(@Nullable VirtualFile... files) {
    if (files == null) {
      return false;
    }
    File[] arg = new File[files.length];
    for (int i = 0; i < files.length; i++) {
      arg[i] = new File(files[i].getPath());
    }
    return isGradleSdk(arg);
  }
  
  private boolean isGradleSdk(@Nullable File ... files) {
    return findGradleJar(files) != null;
  }

  @Nullable
  private File findGradleJar(@Nullable File ... files) {
    if (files == null) {
      return null;
    }
    for (File file : files) {
      if (GRADLE_JAR_FILE_PATTERN.matcher(file.getName()).matches()) {
        return file;
      }
    }
    return null;
  }
}