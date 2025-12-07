package net.fina.server.fsop;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.returns.ProcessResult;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.server.fsop.impl.FsopImportSession;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CheckProcessResultTest {

    @Test
    public void test() {
        try {
            FsopImportSession fsopImportSession = new FsopImportSession();

            Method method = fsopImportSession.getClass().getDeclaredMethod("changeImportedReturnStatus", FsopImportedReturnMetaModel.class, ProcessResult.class);
            method.setAccessible(true);

            FsopImportedReturnMetaModel importedReturn = new FsopImportedReturnMetaModel();
            importedReturn.setStatus(ImportStatus.IMPORTED);

            ProcessResult processResult = new ProcessResult();
            processResult.setStatus(ProcessStatus.STATUS_PROCESSED);
            processResult.setProcessNote("Process OK.");

            method.invoke(fsopImportSession, importedReturn, processResult);

            Assert.assertEquals(importedReturn.getMessage(), processResult.getProcessNote());

            System.out.println(importedReturn);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
    }
}
