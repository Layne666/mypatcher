package cn.layne666.action;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import cn.layne666.form.CvsChangeListDialog;
import cn.layne666.util.PatcherUtil;

public class CvsChangeListAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        VirtualFile[] selectedFiles = event.getData(LangDataKeys.VIRTUAL_FILE_ARRAY);
        if (selectedFiles == null || selectedFiles.length == 0) {
            PatcherUtil.showError("Please select at least one file!", event.getProject());
        } else if (PatcherUtil.isNotSameModule(selectedFiles)) {
            PatcherUtil.showWarning("Please select the module manually!", event.getProject());
        }
        CvsChangeListDialog dialog = new CvsChangeListDialog(event);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();
    }
}
