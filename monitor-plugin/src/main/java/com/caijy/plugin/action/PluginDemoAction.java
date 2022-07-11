package com.caijy.plugin.action;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import com.caijy.plugin.utils.MessageUtil;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author liguang
 * 获取选中区域选中的类
 * @date 2022/5/18 星期三 2:34 下午
 */
public class PluginDemoAction extends AnAction {
    public PluginDemoAction() {
        super("PluginDemoAction");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (null != psiFile) {
            PsiElement elementAt = psiFile.findElementAt(e.getDataContext().getData(CommonDataKeys.EDITOR)
                .getCaretModel().getOffset());
            PsiElement psiElement = PsiTreeUtil.getContextOfType(elementAt, new Class[] {PsiFile.class});
            PsiJavaFile psiJavaFile = (PsiJavaFile)psiElement;
            List<String> downStreamServices = Lists.newArrayList();
            for (PsiClass psiClass : psiJavaFile.getClasses()) {
                PsiField[] psiClassFields = psiClass.getFields();
                for (PsiField classField : psiClassFields) {
                    for (PsiAnnotation annotation : classField.getAnnotations()) {
                        if (Objects.isNull(annotation)) {
                            continue;
                        }
                        PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
                        if (Objects.isNull(referenceElement)) {
                            continue;
                        }
                        PsiElement referenceNameElement = referenceElement.getReferenceNameElement();
                        if (Objects.isNull(referenceNameElement)) {
                            continue;
                        }
                        ASTNode elementNode = referenceNameElement.getNode();
                        if (Objects.isNull(elementNode)) {
                            continue;
                        }
                        String annotationText = elementNode.getText();
                        if (StringUtils.isBlank(annotationText)) {
                            continue;
                        }
                        // psiClass.getFields()[0].getAnnotations()[0].getNameReferenceElement()
                        // .getReferenceNameElement()
                        // .getNode().getText()
                        if (annotationText.equalsIgnoreCase("Autowired") || annotationText.equalsIgnoreCase("Resource")
                            || annotationText.equalsIgnoreCase("DubboRefrence")) {
                            //
                            downStreamServices.add(classField.getType().getCanonicalText());
                        }
                    }
                }
            }
            System.out.println("下游应用 >>>>：" + StringUtils.join(downStreamServices, "\n"));

            String packageName = psiJavaFile.getPackageName();
            System.out.println("psiClass = " + psiElement);
            String path = psiElement.getContainingFile().getOriginalFile().getVirtualFile().getPath();
            // /Users/jianyingcai/IdeaProjects/springcloud2020
            String testDir = "src" + File.separator + "test";
            if (path.indexOf(testDir) != -1) {
                MessageUtil.info("isTestClass = true");
                try {

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
        // com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit4 com.apollo.config.test.Test01,test01
        VirtualMachine.list().stream().forEach(t -> t.displayName());
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (null != virtualFile) {
            SelectionModel model = e.getRequiredData(CommonDataKeys.EDITOR).getSelectionModel();
            String selectedText = model.getSelectedText();
            System.out.println("selectedText = " + selectedText);
            MessageUtil.error("selectedText = " + selectedText);
            // 这里是点击不同地方的事件监听
            model.addSelectionListener(new SelectionListener() {
                @Override
                public void selectionChanged(@NotNull SelectionEvent e) {
                    System.out.println(
                        String.format("oldSelection:%s,newSelection:%s", e.getOldRanges(),
                            e.getNewRanges()));
                    SelectionListener.super.selectionChanged(e);
                }
            });
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {

    }

    public static String readIoToString(InputStream is) {
        String result = null;
        byte[] data = null;
        try {
            data = new byte[is.available()];
            is.read(data);
            result = new String(data, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) { is.close(); }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (data != null) { data = null; }
        }
        return result;
    }

    /**
     * 显示欢迎的弹窗
     **/
    private void actionPerformedOperatioin(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        DataContext context = e.getDataContext();
        ActionManager manager = e.getActionManager();
        System.out.println(project);
        System.out.println(context);
        System.out.println(manager);
        Project data = e.getData(PlatformDataKeys.PROJECT);
        System.out.println(data.getBasePath() + "," + data.getName());
        Messages.showMessageDialog(project, "Hello Idea Plugin!", "Greeting", Messages.getInformationIcon());
    }

    /**
     * 功能：查看action之前 会检查是否选中了yml文件 如果选中才显示此action 否则不显示
     **/
    private void actionUpdateOperation(@NotNull AnActionEvent event) {
        super.update(event);
        //在Action显示之前,根据选中文件扩展名判定是否显示此Action
        VirtualFile file = PlatformDataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        boolean show = file.isDirectory() || (file.getExtension().contains("yaml") || file.getExtension().contains(
            "yml"));
        this.getTemplatePresentation().setEnabled(show);
        this.getTemplatePresentation().setVisible(show);
    }

}
