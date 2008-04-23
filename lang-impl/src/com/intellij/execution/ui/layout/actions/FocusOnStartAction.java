package com.intellij.execution.ui.layout.actions;

import com.intellij.execution.ui.layout.impl.ViewContext;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.ui.content.Content;

public class FocusOnStartAction extends BaseRunnerViewAction implements Toggleable {
  protected void update(final AnActionEvent e, final ViewContext context, final Content[] content) {
    final boolean visible = content.length == 1;
    e.getPresentation().setVisible(visible);
    if (visible) {
      e.getPresentation().putClientProperty(SELECTED_PROPERTY, isToFocus(context, content));
    }
  }

  private static boolean isToFocus(final ViewContext context, final Content[] content) {
    return context.getRunnerLayoutUi().isFocusOnStartup(content[0]);
  }

  protected void actionPerformed(final AnActionEvent e, final ViewContext context, final Content[] content) {
    final boolean toFocus = isToFocus(context, content);
    context.getRunnerLayoutUi().setFocusOnStartup(toFocus ? null : content[0]);
  }
}