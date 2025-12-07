package net.fina.server.dcs.mail;

import freemarker.template.TemplateException;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.mail.entity.Message;
import net.fina.server.dcs.mail.impl.MailResponseProcessor;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.returns.entity.ImportedReturn;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class MailResponseProcessorTest {

    @Test
    public void test() throws IOException, TemplateException {
        Message message = new Message();
        message.setReceivedDate(new Date());
        message.setReadDate(new Date());

        List<UploadFile> uploadFiles = new ArrayList<>();

        UploadFile f1 = new UploadFile();
        f1.setId(1);
        f1.setFileName("f1");
        f1.setStatus(Integer.toString(UploadFileStatus.CONVERTED.ordinal()));

        UploadFile f2 = new UploadFile();
        f2.setId(2);
        f2.setFileName("f2");
        f2.setReason("BLAH");
        f2.setStatus(Integer.toString(UploadFileStatus.ERROR.ordinal()));

        uploadFiles.add(f1);
        uploadFiles.add(f2);

        List<FsopImportedReturnMetaModel> importedReturns = new ArrayList<>();
        FsopImportedReturnMetaModel r1 = new FsopImportedReturnMetaModel();
        r1.setReturnCode("a");
        r1.setMessage("TEST");

        FsopImportedReturnMetaModel r2 = new FsopImportedReturnMetaModel();
        r2.setReturnCode("b");
        r2.setMessage("TEST");

        importedReturns.add(r2);
        importedReturns.add(r1);

        Map<Long, List<FsopImportedReturnMetaModel>> map = new HashMap<>();
        map.put(f1.getId(), importedReturns);

        message.setUploadFiles(uploadFiles);

        MailResponseProcessor processor2 = new MailResponseProcessor("en");
        System.out.println(processor2.process(message, map));

    }
}
