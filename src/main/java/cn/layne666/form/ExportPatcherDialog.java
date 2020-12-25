package cn.layne666.form;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import cn.layne666.common.PatcherConfig;
import cn.layne666.common.PatcherConstant;
import cn.layne666.executor.CompileExecutor;
import cn.layne666.util.FilesUtil;
import cn.layne666.util.PatcherUtil;
import cn.layne666.util.PathResult;

public class ExportPatcherDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField textField;
    private JButton fileChooseBtn;
    private JPanel filePanel;
    private JComboBox<String> moduleComboBox;
    private JCheckBox deleteCheckBox;
    private JCheckBox sourceCheckBox;
    private JComboBox<String> webPathComboBox;
    private AnActionEvent event;
    private JBList<VirtualFile> fileList;
    private Module module;
    private final PatcherConfig config;

    public ExportPatcherDialog(final AnActionEvent event) {
        this.event = event;
        this.config = PatcherConfig.getInstance(event.getProject());
        setTitle("Export Patcher Dialog");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        final ModuleManager moduleManager = ModuleManager.getInstance(Objects.requireNonNull(event.getProject()));
        Module[] modules = moduleManager.getModules();
        // 获取当前文件所属模块
        module = PatcherUtil.getModule(modules, event);
        final String userDir = System.getProperty("user.home");
        String exportPath = userDir + File.separator + "Desktop";
        if (config != null && config.getExportPathMap().containsKey(module.getName())) {
            exportPath = config.getExportPathMap().get(module.getName());
        }
        textField.setText(exportPath);
        // 保存路径按钮事件
        fileChooseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(userDir);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            int flag = fileChooser.showOpenDialog(null);
            if (flag == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        // 增加空选项，防止第一项无法选中
        moduleComboBox.addItem("");
        for (Module module : modules) {
            moduleComboBox.addItem(module.getName());
        }
        if (module != null) {
            moduleComboBox.setSelectedItem(module.getName());
        }
        moduleComboBox.addItemListener(e -> module = moduleManager.findModuleByName((String) e.getItem()));

        webPathComboBox.addItem(PatcherConstant.WEB_PATH_BOOT_INF);
        webPathComboBox.addItem(PatcherConstant.WEB_PATH_WEB_INF);
    }

    private void createUIComponents() {
        VirtualFile[] data = event.getData(LangDataKeys.VIRTUAL_FILE_ARRAY);
        if (data != null) {
            fileList = new JBList<>(data);
            fileList.setEmptyText("No File Selected!");
            ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fileList);
            filePanel = decorator.createPanel();
        }
    }

    private void onOK() {
        // 条件校验
        if (null == textField.getText() || "".equals(textField.getText())) {
            Messages.showErrorDialog(this, "Please select save path!", "Error");
            return;
        }
        VirtualFile[] selectedFiles = event.getData(LangDataKeys.VIRTUAL_FILE_ARRAY);
        if (selectedFiles == null || selectedFiles.length == 0) {
            Messages.showErrorDialog("Please select at least one file!", "Error");
            return;
        }
        if (module == null) {
            Messages.showErrorDialog(this, "Please select module!", "Error");
            return;
        }
        Map<String, String> exportPathMap = config.getExportPathMap();
        if (StringUtil.isNotEmpty(textField.getText())) {
            exportPathMap.put(module.getName(), textField.getText());
        } else {
            exportPathMap.remove(module.getName());
        }
        if (sourceCheckBox.isSelected()) {
            this.execute(null);
            this.dispose();
        } else {
            CompileExecutor compileExecutor = new CompileExecutor(module, event);
            compileExecutor.run(this::execute, this::dispose);
        }
    }

    private void onCancel() {
        dispose();
    }

    private void execute(CompileContext compileContext) {
        // 导出目录
        String exportPath = textField.getText();
        if (exportPath.endsWith(File.separator)) {
            exportPath += module.getName() + File.separator;
        } else {
            exportPath += File.separator + module.getName() + File.separator;
        }
        ListModel<VirtualFile> selectedFiles = fileList.getModel();
        PathResult result = PatcherUtil.getPathResult(module, selectedFiles, exportPath, compileContext,
                webPathComboBox.getModel().getSelectedItem().toString());
        // 删除原有文件
        if (deleteCheckBox.isSelected()) {
            FilesUtil.delete(exportPath);
        }
        // 导出
        result.getFromTo().forEach(FilesUtil::copy);
        // 提示信息
        StringBuilder message = new StringBuilder();
        int notExportSize = result.getUnsettledList().size();
        int fileCount = selectedFiles.getSize() - notExportSize;
        message.append("Export ").append(fileCount).append(" files. ");
        if (fileCount != 0) {
            message.append("(<a href=\"file://").append(exportPath).append("\" target=\"blank\">open</a>)<br>");
        }
        if (notExportSize > 0) {
            message.append("<b>Warning:</b>");
            for (int i = 0; i < notExportSize; i++) {
                message.append(result.getUnsettledList().get(i));
                if (i < notExportSize - 1) {
                    message.append(",<br>");
                }
            }
            message.append(" <b>is not exported!</b><br><b>Please make sure web path is right and these files are not tests.</b>");
        }
        PatcherUtil.showInfo(message.toString(), event.getProject());
    }
}
