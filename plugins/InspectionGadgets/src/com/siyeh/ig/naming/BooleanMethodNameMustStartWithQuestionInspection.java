/*
 * Copyright 2003-2011 Dave Griffith, Bas Leijdekkers
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
package com.siyeh.ig.naming;

import com.intellij.codeInspection.ui.ListTable;
import com.intellij.codeInspection.ui.ListWrappingTableModel;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.searches.SuperMethodsSearch;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.Query;
import com.intellij.util.ui.CheckBox;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.fixes.RenameFix;
import com.siyeh.ig.psiutils.LibraryUtil;
import com.siyeh.ig.ui.UiUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BooleanMethodNameMustStartWithQuestionInspection
  extends BaseInspection {

  @SuppressWarnings({"PublicField"})
  public boolean ignoreBooleanMethods = false;

  @SuppressWarnings({"PublicField"})
  public boolean ignoreInAnnotationInterface = true;

  @SuppressWarnings({"PublicField"})
  public boolean onlyWarnOnBaseMethods = true;

  /**
   * @noinspection PublicField
   */
  @NonNls public String questionString =
    "is,can,has,should,could,will,shall,check,contains,equals,add," +
    "put,remove,startsWith,endsWith";

  List<String> questionList = new ArrayList(32);

  public BooleanMethodNameMustStartWithQuestionInspection() {
    parseString(questionString, questionList);
  }

  @Override
  @NotNull
  public String getDisplayName() {
    return InspectionGadgetsBundle.message(
      "boolean.method.name.must.start.with.question.display.name");
  }

  @Override
  @NotNull
  public String buildErrorString(Object... infos) {
    return InspectionGadgetsBundle.message(
      "boolean.method.name.must.start.with.question.problem.descriptor");
  }

  @Override
  public void readSettings(Element element) throws InvalidDataException {
    super.readSettings(element);
    parseString(questionString, questionList);
  }

  @Override
  public void writeSettings(Element element) throws WriteExternalException {
    questionString = formatString(questionList);
    super.writeSettings(element);
  }

  @Override
  public JComponent createOptionsPanel() {
    final JPanel panel = new JPanel(new GridBagLayout());
    final ListTable table =
      new ListTable(new ListWrappingTableModel(questionList,
                                               InspectionGadgetsBundle.message(
                                                 "boolean.method.name.must.start.with.question.table.column.name")));
    final JScrollPane scrollPane =
      ScrollPaneFactory.createScrollPane(table);
    UiUtils.setScrollPaneSize(scrollPane, 7, 25);
    final ActionToolbar toolbar =
      UiUtils.createAddRemoveToolbar(table);

    final GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    panel.add(toolbar.getComponent(), constraints);

    constraints.gridy = 1;
    constraints.weightx = 1.0;
    constraints.weighty = 1.0;
    constraints.fill = GridBagConstraints.BOTH;
    panel.add(scrollPane, constraints);

    final CheckBox checkBox1 =
      new CheckBox(InspectionGadgetsBundle.message(
        "ignore.methods.with.boolean.return.type.option"),
                   this, "ignoreBooleanMethods");
    constraints.gridy = 2;
    constraints.weighty = 0.0;
    panel.add(checkBox1, constraints);

    final CheckBox checkBox2 =
      new CheckBox(InspectionGadgetsBundle.message(
        "ignore.boolean.methods.in.an.interface.option"),
                   this, "ignoreInAnnotationInterface");
    constraints.gridy = 3;
    panel.add(checkBox2, constraints);

    final CheckBox checkBox3 =
      new CheckBox(InspectionGadgetsBundle.message(
        "ignore.methods.overriding.super.method"),
                   this, "onlyWarnOnBaseMethods");
    constraints.gridy = 4;
    panel.add(checkBox3, constraints);
    return panel;
  }

  @Override
  protected InspectionGadgetsFix buildFix(Object... infos) {
    return new RenameFix();
  }

  @Override
  protected boolean buildQuickFixesOnlyForOnTheFlyErrors() {
    return true;
  }

  @Override
  public BaseInspectionVisitor buildVisitor() {
    return new BooleanMethodNameMustStartWithQuestionVisitor();
  }

  private class BooleanMethodNameMustStartWithQuestionVisitor
    extends BaseInspectionVisitor {

    @Override
    public void visitMethod(@NotNull PsiMethod method) {
      final PsiType returnType = method.getReturnType();
      if (returnType == null) {
        return;
      }
      else if (!returnType.equals(PsiType.BOOLEAN)) {
        if (ignoreBooleanMethods ||
            !returnType.equalsToText(
              CommonClassNames.JAVA_LANG_BOOLEAN)) {
          return;
        }
      }
      if (ignoreInAnnotationInterface) {
        final PsiClass containingClass = method.getContainingClass();
        if (containingClass != null &&
            containingClass.isAnnotationType()) {
          return;
        }
      }
      final String name = method.getName();
      for (String question : questionList) {
        if (name.startsWith(question)) {
          return;
        }
      }
      if (onlyWarnOnBaseMethods) {
        final Query<MethodSignatureBackedByPsiMethod> superSearch =
          SuperMethodsSearch.search(method, null, true, false);
        if (superSearch.findFirst() != null) {
          return;
        }
      }
      else if (LibraryUtil.isOverrideOfLibraryMethod(method)) {
        return;
      }
      registerMethodError(method);
    }
  }
}
