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
package com.intellij.ui;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Konstantin Bulenkov
 */
@SuppressWarnings("UnusedDeclaration")
public class ToolbarDecorator {
  private JTable myTable;
  private TableModel myTableModel;
  private ListModel myListModel;
  private Border myToolbarBorder;
  private boolean myAddActionEnabled;
  private boolean myRemoveActionEnabled;
  private boolean myUpActionEnabled;
  private boolean myDownActionEnabled;
  private Border myBorder;
  private List<AnActionButton> myExtraActions = new ArrayList<AnActionButton>();
  private ActionToolbarPosition myToolbarPosition;
  private Runnable myAddAction;
  private Runnable myRemoveAction;
  private Runnable myUpAction;
  private Runnable myDownAction;
  private AddRemoveUpDownPanel myPanel;
  private JList myList;


  private ToolbarDecorator(JTable table) {
    myTable = table;
    myTable.setBorder(IdeBorderFactory.createEmptyBorder(0));
    myTableModel = table.getModel();
    initPositionAndBorder();
    myAddActionEnabled = myRemoveActionEnabled = myUpActionEnabled = myDownActionEnabled = myTableModel instanceof EditableModel;
    if (myTableModel instanceof EditableModel) {
      createDefaultTableActions();
    }
  }

  private ToolbarDecorator(JList list) {
    myList = list;
    myListModel = list.getModel();
    myAddActionEnabled = myRemoveActionEnabled = myUpActionEnabled = myDownActionEnabled = true;
    initPositionAndBorder();
    createDefaultListActions();
  }

  private void createDefaultListActions() {
    myRemoveAction = new Runnable() {
      @Override
      public void run() {
        ListUtil.removeSelectedItems(myList);
        updateListButtons(myList, myPanel);
      }
    };
    myUpAction = new Runnable() {
      @Override
      public void run() {
        ListUtil.moveSelectedItemsUp(myList);
        updateListButtons(myList, myPanel);
      }
    };
    myDownAction = new Runnable() {
      @Override
      public void run() {
        ListUtil.moveSelectedItemsDown(myList);
        updateListButtons(myList, myPanel);
      }
    };
  }

  private void initPositionAndBorder() {
    myToolbarPosition = SystemInfo.isMac ? ActionToolbarPosition.BOTTOM : ActionToolbarPosition.RIGHT;
    myBorder = SystemInfo.isMac ? new CustomLineBorder(0,1,1,1) : new CustomLineBorder(0, 1, 0, 0);
  }

  private void createDefaultTableActions() {
    final JTable table = myTable;
    final EditableModel tableModel = (EditableModel)myTableModel;

    myAddAction = new Runnable() {
      public void run() {
        TableUtil.stopEditing(table);
        tableModel.addRow();
        final int index = myTableModel.getRowCount() - 1;
        table.editCellAt(index, 0);
        table.setRowSelectionInterval(index, index);
        table.setColumnSelectionInterval(0, 0);
        table.getParent().repaint();
        final Component editorComponent = table.getEditorComponent();
        if (editorComponent != null) {
          final Rectangle bounds = editorComponent.getBounds();
          table.scrollRectToVisible(bounds);
          editorComponent.requestFocus();
        }
      }
    };

    myRemoveAction = new Runnable() {
      public void run() {
        TableUtil.stopEditing(table);
        int index = table.getSelectedRow();
        if (0 <= index && index < myTableModel.getRowCount()) {
          tableModel.removeRow(index);
          if (index < myTableModel.getRowCount()) {
            table.setRowSelectionInterval(index, index);
          }
          else {
            if (index > 0) {
              table.setRowSelectionInterval(index - 1, index - 1);
            }
          }
          updateTableButtons(table, tableModel, myPanel);
        }

        table.getParent().repaint();
        table.requestFocus();
      }
    };

    myUpAction = new Runnable() {
      public void run() {
        TableUtil.stopEditing(table);
        int index = table.getSelectedRow();
        if (0 < index && index < myTableModel.getRowCount()) {
          tableModel.exchangeRows(index, index - 1);
          table.setRowSelectionInterval(index - 1, index - 1);
        }
        table.requestFocus();
      }
    };

    myDownAction = new Runnable() {
      public void run() {
        TableUtil.stopEditing(table);
        int index = table.getSelectedRow();
        if (0 <= index && index < myTableModel.getRowCount() - 1) {
          tableModel.exchangeRows(index, index + 1);
          table.setRowSelectionInterval(index + 1, index + 1);
        }
        table.requestFocus();
      }
    };
   }

  private static void updateListButtons(final JList list, final AddRemoveUpDownPanel p) {
    if (list.isEnabled() && p != null) {
      final int index = list.getSelectedIndex();
      if (0 <= index && index < list.getModel().getSize()) {
        final boolean downEnable = list.getMaxSelectionIndex() < list.getModel().getSize() - 1;
        final boolean upEnable = list.getMinSelectionIndex() > 0;
        p.setEnabled(AddRemoveUpDownPanel.Buttons.REMOVE, true);
        p.setEnabled(AddRemoveUpDownPanel.Buttons.UP, upEnable);
        p.setEnabled(AddRemoveUpDownPanel.Buttons.DOWN, downEnable);
      } else {
        p.setEnabled(AddRemoveUpDownPanel.Buttons.REMOVE, false);
        p.setEnabled(AddRemoveUpDownPanel.Buttons.UP, false);
        p.setEnabled(AddRemoveUpDownPanel.Buttons.DOWN, false);
      }
      p.setEnabled(AddRemoveUpDownPanel.Buttons.ADD, true);
    }
  }

  private static void updateTableButtons(final JTable table,
                                         final EditableModel tableModel,
                                         final AddRemoveUpDownPanel p) {
    if (table.isEnabled() && p != null) {
      final int index = table.getSelectedRow();
      if (0 <= index && index < ((TableModel)tableModel).getRowCount()) {
        final boolean downEnable = index < ((TableModel)tableModel).getRowCount() - 1;
        final boolean upEnable = index > 0;
        p.setEnabled(AddRemoveUpDownPanel.Buttons.REMOVE, true);
        p.setEnabled(AddRemoveUpDownPanel.Buttons.UP, upEnable);
        p.setEnabled(AddRemoveUpDownPanel.Buttons.DOWN, downEnable);
      } else {
        p.setEnabled(AddRemoveUpDownPanel.Buttons.REMOVE, false);
        p.setEnabled(AddRemoveUpDownPanel.Buttons.UP, false);
        p.setEnabled(AddRemoveUpDownPanel.Buttons.DOWN, false);
      }
      p.setEnabled(AddRemoveUpDownPanel.Buttons.ADD, true);
    }
  }


  public static ToolbarDecorator createDecorator(JTable table) {
    return new ToolbarDecorator(table);
  }

  public static ToolbarDecorator createDecorator(JList list) {
    return new ToolbarDecorator(list);
  }

  public ToolbarDecorator disableAddAction() {
    myAddActionEnabled = false;
    return this;
  }

  public ToolbarDecorator disableRemoveAction() {
    myRemoveActionEnabled = false;
    return this;
  }

  public ToolbarDecorator disableUpAction() {
    myUpActionEnabled = false;
    return this;
  }

  public ToolbarDecorator disableDownAction() {
    myDownActionEnabled = false;
    return this;
  }

  public ToolbarDecorator setToolbarBorder(Border border) {
    myBorder = border;
    return this;
  }

  public ToolbarDecorator setLineBorder(int top, int left, int bottom, int right) {
    return setToolbarBorder(new CustomLineBorder(top, left, bottom, right));
  }

  public ToolbarDecorator addExtraAction(AnActionButton action) {
    myExtraActions.add(action);
    return this;
  }

  public ToolbarDecorator setToolbarPosition(ActionToolbarPosition position) {
    myToolbarPosition = position;
    return this;
  }

  public ToolbarDecorator setAddAction(Runnable action) {
    myAddActionEnabled = action != null;
    myAddAction = action;
    return this;
  }

  public ToolbarDecorator setRemoveAction(Runnable action) {
    myRemoveActionEnabled = action != null;
    myRemoveAction = action;
    return this;
  }

  public ToolbarDecorator setUpAction(Runnable action) {
    myUpActionEnabled = action != null;
    myUpAction = action;
    return this;
  }

  public ToolbarDecorator setDownAction(Runnable action) {
    myDownActionEnabled = action != null;
    myDownAction = action;
    return this;
  }

  public JPanel createPanel() {
    final AddRemoveUpDownPanel.Buttons[] buttons = getButtons();
    myPanel = new AddRemoveUpDownPanel(createListener(),
                             myTable == null ? myList : myTable,
                             myToolbarPosition == ActionToolbarPosition.TOP || myToolbarPosition == ActionToolbarPosition.BOTTOM,
                             myExtraActions.toArray(new AnActionButton[myExtraActions.size()]),
                             buttons);
    myPanel.setBorder(myBorder);
    final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myTable == null ? myList : myTable);
    scrollPane.setBorder(IdeBorderFactory.createEmptyBorder(0));
    final JPanel panel = new JPanel(new BorderLayout()) {
      @Override
      public void addNotify() {
        super.addNotify();
        if (myList != null) {
          updateListButtons(myList, myPanel);
        }
        if (myTable != null && myTableModel instanceof EditableModel) {
          updateTableButtons(myTable, (EditableModel)myTableModel, myPanel);
        }
      }
    };
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(myPanel, getPlacement());
    if (myTableModel instanceof EditableModel && buttons.length > 0) {
      updateTableButtons(myTable, (EditableModel)myTableModel, myPanel);

      if (myUpAction != null && myUpActionEnabled && myDownAction != null && myDownActionEnabled) {
        TableRowsDnDSupport.install(myTable, (EditableModel)myTableModel);
      }
      myTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          updateTableButtons(myTable, (EditableModel)myTableModel, myPanel);
        }
      });
    }
    if (myList != null) {
      updateListButtons(myList, myPanel);
      myList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          updateListButtons(myList, myPanel);
        }
      });
    }
    panel.setBorder(new LineBorder(UIUtil.getBorderColor()));
    return panel;
  }

  private Object getPlacement() {
    switch (myToolbarPosition) {
      case TOP: return BorderLayout.NORTH;
      case LEFT: return BorderLayout.WEST;
      case BOTTOM: return BorderLayout.SOUTH;
      case RIGHT: return BorderLayout.EAST;
    }
    return BorderLayout.SOUTH;
  }

  private AddRemoveUpDownPanel.Buttons[] getButtons() {
    final ArrayList<AddRemoveUpDownPanel.Buttons> buttons = new ArrayList<AddRemoveUpDownPanel.Buttons>();
    if (myAddActionEnabled && myAddAction != null) {
      buttons.add(AddRemoveUpDownPanel.Buttons.ADD);
    }
    if (myRemoveActionEnabled && myRemoveAction != null) {
      buttons.add(AddRemoveUpDownPanel.Buttons.REMOVE);
    }
    if (myUpActionEnabled && myUpAction != null) {
      buttons.add(AddRemoveUpDownPanel.Buttons.UP);
    }
    if (myDownActionEnabled && myDownAction != null) {
      buttons.add(AddRemoveUpDownPanel.Buttons.DOWN);
    }
    return buttons.toArray(new AddRemoveUpDownPanel.Buttons[buttons.size()]);
  }

  private AddRemoveUpDownPanel.Listener createListener() {
    return new AddRemoveUpDownPanel.Listener() {
      @Override
      public void doAdd() {
        if (myAddAction != null) myAddAction.run();
      }

      @Override
      public void doRemove() {
        if (myRemoveAction != null) myRemoveAction.run();
      }

      @Override
      public void doUp() {
        if (myUpAction != null) myUpAction.run();
      }

      @Override
      public void doDown() {
        if (myDownAction != null) myDownAction.run();
      }
    };
  }
}