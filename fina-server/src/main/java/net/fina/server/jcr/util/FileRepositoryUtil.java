package net.fina.server.jcr.util;

import net.fina.common.shared.jcr.JcrCustomConstants;

public class FileRepositoryUtil {

    public static String getFiDocumentsRootFolderPath() {
        return JcrCustomConstants.USER_ROOT_FOLDER + "/" + JcrCustomConstants.USER_FILES_FOLDER;
    }

    public static String getStoredReportRootFolderPath() {
        return JcrCustomConstants.USER_ROOT_FOLDER + "/" + JcrCustomConstants.USER_STORED_REPORTS_FOLDER;
    }

    public static String getUploadedFilesRootFolderPath() {
        return JcrCustomConstants.USER_ROOT_FOLDER + "/" + JcrCustomConstants.UPLOADED_FILES;
    }

    public static String getImportedReturnRootFolderPath() {
        return JcrCustomConstants.USER_ROOT_FOLDER + "/" + JcrCustomConstants.USER_IMPORTED_RETURNS_FOLDER;
    }

    public static String getEmsRootFolderPath() {
        return JcrCustomConstants.USER_ROOT_FOLDER + "/" + JcrCustomConstants.EMS_FOLDER;
    }

    public static String getEmsDocumentPath(String fiCode, String folderName, Object id) {
        return getEmsRootFolderPath() + "/" + fiCode + '/' + folderName + "/" + id;
    }

}
