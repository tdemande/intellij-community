/*
 * Copyright 2000-2010 JetBrains s.r.o.
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
package git4idea.ui.branch;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.util.Consumer;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

/**
 * Status bar widget which displays the current branch for the file currently open in the editor.
 * @author Kirill Likhodedov
 */
public class GitBranchWidget extends EditorBasedWidget implements StatusBarWidget.MultipleTextValuesPresentation,
                                                                  StatusBarWidget.Multiframe,
                                                                  GitRepositoryChangeListener {
  private final GitRepositoryManager myRepositoryManager;
  private volatile String myText = "";
  private volatile String myTooltip = "";

  public GitBranchWidget(Project project) {
    super(project);
    myRepositoryManager = GitRepositoryManager.getInstance(project);
    myRepositoryManager.addListenerToAllRepositories(this);
  }

  @Override
  public StatusBarWidget copy() {
    return new GitBranchWidget(getProject());
  }

  @NotNull
  @Override
  public String ID() {
    return GitBranchWidget.class.getName();
  }

  @Override
  public WidgetPresentation getPresentation(@NotNull PlatformType type) {
    return this;
  }

  @Override
  public void selectionChanged(FileEditorManagerEvent event) {
    update();
  }

  @Override
  public void fileOpened(FileEditorManager source, VirtualFile file) {
    update();
  }

  @Override
  public void fileClosed(FileEditorManager source, VirtualFile file) {
    update();
  }

  @Override
  public void repositoryChanged() {
    update();
  }

  @Override
  public ListPopup getPopupStep() {
    Project project = getProject();
    if (project == null) {
      return null;
    }
    VirtualFile root = GitBranchUiUtil.guessGitRoot(project);
    GitRepository repo = myRepositoryManager.getRepositoryForRoot(root);
    if (repo == null) {
      return null;
    }
    return GitBranchPopup.getInstance(getProject(), repo).asListPopup();
  }

  @Override
  public String getSelectedValue() {
    final String text = myText;
    return StringUtil.isEmpty(text) ? "" : "Git: " + text;
  }

  @NotNull
  @Override
  public String getMaxValue() {
    return "Git: Rebasing abcdefghij";
  }

  @Override
  public String getTooltipText() {
    return myTooltip;
  }

  @Override
  // Updates branch information on click
  public Consumer<MouseEvent> getClickConsumer() {
    return new Consumer<MouseEvent>() {
      public void consume(MouseEvent mouseEvent) {
          update();
      }
    };
  }

  private void update() {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      @Override
      public void run() {
        Project project = getProject();
        if (project == null) {
          emptyTextAndTooltip();
          return;
        }

        VirtualFile root = GitBranchUiUtil.guessGitRoot(project);
        GitRepository repo = myRepositoryManager.getRepositoryForRoot(root);
        if (repo == null) { // the file is not under version control => display nothing
          emptyTextAndTooltip();
          return;
        }

        myText = GitBranchUiUtil.getDisplayableBranchText(repo);
        myTooltip = getDisplayableBranchTooltip(repo);
        myStatusBar.updateWidget(ID());
      }
    });
  }

  private void emptyTextAndTooltip() {
    myText = "";
    myTooltip = "";
  }

  @NotNull
  private static String getDisplayableBranchTooltip(GitRepository repo) {
    String text = GitBranchUiUtil.getDisplayableBranchText(repo);
    if (GitRepositoryManager.getInstance(repo.getProject()).getRepositories().size() > 1) {
      return text + "\n" + "Root: " + repo.getRoot().getName();
    }
    return text;
  }

}
