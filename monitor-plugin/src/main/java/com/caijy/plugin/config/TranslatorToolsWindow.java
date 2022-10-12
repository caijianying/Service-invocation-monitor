//package com.caijy.plugin.config;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.wm.ToolWindow;
//import com.intellij.openapi.wm.ToolWindowFactory;
//import com.intellij.ui.components.JBScrollPane;
//import com.intellij.ui.content.Content;
//import com.intellij.ui.content.ContentFactory;
//import org.jetbrains.annotations.NotNull;
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//
//public class TranslatorToolsWindow implements ToolWindowFactory {
//
//    private static JTable table;
//
//    @Override
//    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
//        // ContentFactory 在 IntelliJ 平台 SDK 中负责 UI 界面的管理
//        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        // 创建我们的工具栏界面，TranslatorNote 是基于 Swing 实现的一个窗口视图
//        TranslatorNote note = new TranslatorNote();
//        table = note.getTable();
//        // 在界面工厂中创建翻译插件的界面
//        Content content = contentFactory.createContent(note.getNotePanel(), "", false);
//        // 将被界面工厂代理后创建的content，添加到工具栏窗口管理器中
//        toolWindow.getContentManager().addContent(content);
//    }
//
//    // 基于 Swing 实现的窗口类
//    static class TranslatorNote {
//        // Swing 中的滑动窗口视图
//        private final JScrollPane notePanel;
//        // Swing 中的表格视图，该表格视图用于展示翻译结果
//        private final JTable table;
//
//        public TranslatorNote() {
//            // 设置表格的表头
//            String[] header = {"原文", "译文"};
//            DefaultTableModel tableModel = new DefaultTableModel(null, header);
//            this.table = new JTable();
//            this.table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
//            this.table.setModel(tableModel);
//            this.notePanel = new JBScrollPane(table);
//            this.notePanel.setSize(200, 800);
//        }
//
//        public JScrollPane getNotePanel() {
//            return notePanel;
//        }
//
//        public JTable getTable() {
//            return table;
//        }
//    }
//
//    // addNote 方法将翻译插件的原文与译文加入到工具栏窗口的表格视图中
//    public static void addNote(String from, String to) {
//        if (table == null) {
//            return;
//        }
//        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
//        tableModel.addRow(new Object[]{from, to});
//    }
//
//}
