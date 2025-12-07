
package net.fina.server.dcs.uploadfile.util;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.dcs.UploadType;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.ContentModel;
import net.fina.messages.MessagesUtil;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.util.AbstractFilePrintUtil;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class UploadFilePrintUtil extends AbstractFilePrintUtil {

    public static ContentModel generateFile(String fileType, List<UploadFile> uploadFiles, String contentPath, String langCode) throws FinATypeException {
        TableModel tableModel = constructTableModel(uploadFiles, langCode);
        return generateFile(fileType, tableModel, contentPath, "UploadFiles");
    }

    private static TableModel constructTableModel(List<UploadFile> uploadFiles, String langCode) {
        String[] columnNames = {
                MessagesUtil.getString("net.fina.dcs.uploadedFilesTab.filterToolbar.bankCodeEmptyText", langCode),
                MessagesUtil.getString("net.fina.dcs.uploadedFilesTab.filterToolbar.userEmptyText", langCode),
                MessagesUtil.getString("net.fina.dcs.uploadedFilesTab.statusColumnTitle", langCode),
                MessagesUtil.getString("net.fina.dcs.postbox.tabView.grid.header.fileName", langCode),
                MessagesUtil.getString("net.fina.dcs.postbox.tabView.grid.header.uploadTime", langCode),
                MessagesUtil.getString("net.fina.web.client.calendar.grid.type", langCode)
        };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, uploadFiles.size());

        DateFormat df = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");

        for (int i = 0; i < uploadFiles.size(); i++) {
            UploadFile uf = uploadFiles.get(i);
            tableModel.setValueAt(uf.getBankCode(), i, 0);
            tableModel.setValueAt(uf.getUser().getLogin(), i, 1);
            tableModel.setValueAt(MessagesUtil.getString(UploadFileStatus.values()[Integer.parseInt(uf.getStatus())].getCode(), langCode), i, 2);
            tableModel.setValueAt(uf.getFileName(), i, 3);
            tableModel.setValueAt(df.format(uf.getUploadedTime()), i, 4);
            tableModel.setValueAt(MessagesUtil.getString(UploadType.values()[uf.getType().ordinal()].getCode(), langCode), i, 5);
        }

        return tableModel;
    }
}