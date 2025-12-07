package net.fina.server.returns.proxy;


import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.ContentModel;
import net.fina.fsop.view.api.FsopViewApi;
import net.fina.fsop.view.impl.FsopViewImpl;
import net.fina.fsop.view.model.FsopDataMetaModel;
import net.fina.fsop.view.model.FsopReviewModel;
import net.fina.server.mdt.xml.v2.Node;
import net.fina.server.returns.xml.Return;
import net.fina.server.rvc.api.ReturnVersionControlLocal;
import net.fina.server.rvc.api.xml.MdtXmlParser;
import net.fina.server.rvc.api.xml.ReturnXmlParser;
import net.fina.server.rvc.api.xml.StatusXmlParser;
import net.fina.server.rvc.impl.ReturnVersionControlFileTypes;
import net.fina.server.rvc.model.RvcXmlToMetaModelUtil;
import net.fina.server.rvc.xml.FileType;
import net.fina.server.rvc.xml.ReturnStatusesType;
import net.fina.server.rvc.xml.StatusType;
import net.fina.server.store.model.RepositoryFile;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Oto Iantbelidze
 * Created: 24.10.25
 */
@Stateless
@SecurityDomain("FinASecurityDomain")
@PermitAll
public class ReturnVersionControlProxySession {
    private final Logger log = Logger.getLogger(getClass());
    @Inject
    private ReturnVersionControlLocal returnVersionControlLocal;

    public ContentModel loadReturnVersionContent(long returnId, List<Long> statusIds, String fileExtension, String langCode) throws FinATypeException {

        RepositoryFile repositoryFile = returnVersionControlLocal.loadReturnFileByIdAndFileType(
                returnId,
                ReturnVersionControlFileTypes.STATUS_FILE_NAME
        );


        if (repositoryFile.getContent() == null) {
            throw new FinATypeException("Selected version doesn't exist.");
        } else {
            try (InputStream inputStream = new ByteArrayInputStream(repositoryFile.getContent())) {

                ReturnStatusesType returnStatusesType = StatusXmlParser.getInstance().convert(inputStream);
                List<FsopDataMetaModel> dataMetaModelList = new ArrayList<>();
                FsopViewApi fsopViewApi = new FsopViewImpl();

                for (StatusType statusType : returnStatusesType.getStatus()) {

                    if (statusIds.contains(Long.parseLong(statusType.getId()))) {
                        FileType fileType = statusType.getFile();

                        Return ret = ReturnXmlParser.getInstance().convert(
                                returnVersionControlLocal.getFileStreamByVersion(repositoryFile.getPath(), ReturnVersionControlFileTypes.RETURN_FILE_NAME, fileType.getReturnFileVersionId())
                        );

                        Node node = MdtXmlParser.getInstance().convert(
                                returnVersionControlLocal.getFileStreamByVersion(repositoryFile.getPath(), ReturnVersionControlFileTypes.MDT_FILE_NAME, fileType.getMdtFileVersionId())
                        );

                        FsopDataMetaModel dataModel = fsopViewApi.getMetaModel(RvcXmlToMetaModelUtil.getNodeMetaModel(node), RvcXmlToMetaModelUtil.getReturnMetaModel(ret), RvcXmlToMetaModelUtil.getStatusTypeMetaModel(statusType), langCode);
                        dataModel.setStatusDate(statusType.getStatusDate());
                        dataMetaModelList.add(dataModel);
                    }
                }

                if (!dataMetaModelList.isEmpty()) {
                    FsopReviewModel reviewModel = returnVersionControlLocal.getFsopReviewModel(dataMetaModelList, langCode, fileExtension);

                    byte[] content = reviewModel.getContent();
                    String fileName = reviewModel.getFileName();


                    return new ContentModel(content, fileName, null);

                }
            } catch (IOException e) {
                throw new FinATypeException("Error while loading return version content.");
            }
        }

        log.warn("Return version content not found. in repository ");
        throw new RuntimeException("Error while loading return version content.");
    }
}
