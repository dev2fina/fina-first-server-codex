package net.fina.server.rvc.impl;

import freemarker.template.TemplateException;
import net.fina.fsop.view.api.FsopViewApi;
import net.fina.fsop.view.impl.FsopViewImpl;
import net.fina.fsop.view.model.FsopDataMetaModel;
import net.fina.fsop.view.util.ViewUtil;
import net.fina.server.mdt.xml.v2.Node;
import net.fina.server.returns.xml.Return;
import net.fina.server.rvc.api.xml.MdtXmlParser;
import net.fina.server.rvc.api.xml.ReturnXmlParser;
import net.fina.server.rvc.api.xml.StatusXmlParser;
import net.fina.server.rvc.model.RvcXmlToMetaModelUtil;
import net.fina.server.rvc.xml.ReturnStatusesType;
import net.fina.server.rvc.xml.StatusType;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FsopViewImplTest {

    @Test
    public void test() throws IOException, TemplateException {

        //Read and Convert Return
        byte[] ret = Files.readAllBytes(new File("./src/test/resources/net.fina.server.rvc.impl/return.xml").toPath());

        Return aReturn = null;
        try (InputStream in = new ByteArrayInputStream(ret)) {
            aReturn = ReturnXmlParser.getInstance().convert(in);
        }

        //Read and convert MDT
        byte[] mdt = Files.readAllBytes(new File("./src/test/resources/net.fina.server.rvc.impl/mdt.xml").toPath());

        Node node;
        try (InputStream in = new ByteArrayInputStream(mdt)) {
            node = MdtXmlParser.getInstance().convert(in);
        }

        byte[] statuses = Files.readAllBytes(new File("./src/test/resources/net.fina.server.rvc.impl/status.xml").toPath());

        ReturnStatusesType statusesType;
        try (InputStream in = new ByteArrayInputStream(statuses)) {
            statusesType = StatusXmlParser.getInstance().convert(in);
        }

        StatusType statusType = statusesType.getStatus().get(statusesType.getStatus().size() - 1);

        //Test print
        System.out.println(aReturn.getHeader().getReturnCode());
        System.out.println(node.getType());

        FsopViewApi fsopViewApi = new FsopViewImpl();
        FsopDataMetaModel model = fsopViewApi.getMetaModel(RvcXmlToMetaModelUtil.getNodeMetaModel(node), RvcXmlToMetaModelUtil.getReturnMetaModel(aReturn), RvcXmlToMetaModelUtil.getStatusTypeMetaModel(statusType), "en_US");

        ViewUtil.print(model, System.out, false);

        System.out.println(new String(ViewUtil.printFreeMarker(model)));

    }
}
