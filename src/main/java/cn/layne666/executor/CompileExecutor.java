package cn.layne666.executor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.module.Module;

import java.util.Objects;
import java.util.function.Consumer;

import cn.layne666.common.SuccessCompileStatusNotification;
import cn.layne666.util.ExceptionUtils;
import cn.layne666.util.PatcherUtil;

public class CompileExecutor {
    private Module module;
    private AnActionEvent event;

    public CompileExecutor(Module module, AnActionEvent event) {
        this.module = module;
        this.event = event;
    }

    public void run(Consumer<CompileContext> execute, Runnable clean) {
        try {
            CompilerManager compilerManager = CompilerManager.getInstance(Objects.requireNonNull(event.getProject()));
            compilerManager.make(module, new SuccessCompileStatusNotification(execute));
            PatcherUtil.showInfo("Code is compiling.", event.getProject());
        } catch (Exception e) {
            e.printStackTrace();
            PatcherUtil.showError(ExceptionUtils.getStructuredErrorString(e), event.getProject());
        } finally {
            clean.run();
        }
    }
}
