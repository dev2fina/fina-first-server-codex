package net.fina.server.dcs;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.UploadFileSession;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * nikoloz on 10/3/14.
 */
public class UploadFileManageDcsTypeExceptionTest {

    @Test
    public void testCase1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String testError = "Test Error";
        UploadFile uploadFile = new UploadFile();
        DcsTypeException dcsTypeException = new DcsTypeException(testError);

        UploadFileSession uploadFileSession = new UploadFileSession();
        Method method = uploadFileSession.getClass().getDeclaredMethod("manageDcsTypeException", UploadFile.class, DcsTypeException.class);
        method.setAccessible(true);
        Object r = method.invoke(uploadFileSession, uploadFile, dcsTypeException);

        System.out.println("Status = " + UploadFileStatus.values()[Integer.parseInt(uploadFile.getStatus())]);
        System.out.println("Reason = " + uploadFile.getReason());

        Assert.assertEquals(UploadFileStatus.MATRIX_ERROR.ordinal(), Integer.parseInt(uploadFile.getStatus()));
        Assert.assertEquals(testError, uploadFile.getReason());

    }

    @Test
    public void testCase2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UploadFile uploadFile = new UploadFile();

        DcsTypeException dcsTypeException = new DcsTypeException(Arrays.asList("blah", "blah"));
        dcsTypeException.setType(DcsTypeException.Type.INVALID_PASSWORD);

        UploadFileSession uploadFileSession = new UploadFileSession();
        Method method = uploadFileSession.getClass().getDeclaredMethod("manageDcsTypeException", UploadFile.class, DcsTypeException.class);
        method.setAccessible(true);
        Object r = method.invoke(uploadFileSession, uploadFile, dcsTypeException);

        System.out.println("Status = " + UploadFileStatus.values()[Integer.parseInt(uploadFile.getStatus())]);
        System.out.println("Reason = " + uploadFile.getReason());

        Assert.assertEquals(UploadFileStatus.INVALID_SECURITY.ordinal(), Integer.parseInt(uploadFile.getStatus()));
        Assert.assertEquals("blah;blah", uploadFile.getReason());

    }
}
