package net.fina.first.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class FileUtils {

    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "txt", "csv", "rtf", "odt", "ods", "odp",
            "jpg", "jpeg", "png", "gif", "bmp", "tiff",
            "zip", "rar", "7z"
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB

    static {
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("doc", "application/msword");
        MIME_TYPES.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MIME_TYPES.put("xls", "application/vnd.ms-excel");
        MIME_TYPES.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_TYPES.put("ppt", "application/vnd.ms-powerpoint");
        MIME_TYPES.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("csv", "text/csv");
        MIME_TYPES.put("rtf", "application/rtf");
        MIME_TYPES.put("odt", "application/vnd.oasis.opendocument.text");
        MIME_TYPES.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        MIME_TYPES.put("odp", "application/vnd.oasis.opendocument.presentation");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("bmp", "image/bmp");
        MIME_TYPES.put("tiff", "image/tiff");
        MIME_TYPES.put("zip", "application/zip");
        MIME_TYPES.put("rar", "application/x-rar-compressed");
        MIME_TYPES.put("7z", "application/x-7z-compressed");
    }

    private FileUtils() {
        // Utility class
    }

    public static String getExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }

    public static String getBaseName(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return filename;
        }
        return filename.substring(0, dotIndex);
    }

    public static String getMimeType(String filename) {
        String extension = getExtension(filename);
        return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
    }

    public static boolean isAllowedExtension(String filename) {
        String extension = getExtension(filename);
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    public static boolean isAllowedFileSize(long size) {
        return size > 0 && size <= MAX_FILE_SIZE;
    }

    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }

    public static String sanitizeFilename(String filename) {
        if (filename == null) {
            return null;
        }
        // Remove path separators and other dangerous characters
        return filename.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    public static boolean isImage(String filename) {
        String extension = getExtension(filename);
        return Set.of("jpg", "jpeg", "png", "gif", "bmp", "tiff").contains(extension);
    }

    public static boolean isPdf(String filename) {
        return "pdf".equals(getExtension(filename));
    }

    public static boolean isDocument(String filename) {
        String extension = getExtension(filename);
        return Set.of("doc", "docx", "xls", "xlsx", "ppt", "pptx",
                "txt", "csv", "rtf", "odt", "ods", "odp", "pdf").contains(extension);
    }
}
