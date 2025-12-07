package net.fina.first.dto.ecm;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class NodeMetaModel {
    private String id;
    private String parentId;
    private String name;
    private String nodeType;
    private Boolean isFolder;
    private Boolean isFile;
    private Boolean isLocked;
    private Boolean isLink;
    private LocalDateTime modifiedAt;
    private LocalDateTime createdAt;
    private UserInfo modifiedBy;
    private UserInfo createdBy;
    private ContentInfo content;
    private List<String> aspects;
    private Map<String, Object> properties;
    private List<String> allowableOperations;
    private PathInfo path;
    private String versionComment;

    @Data
    public static class UserInfo {
        private String id;
        private String displayName;
    }

    @Data
    public static class ContentInfo {
        private String mimeType;
        private String mimeTypeName;
        private Long sizeInBytes;
        private String encoding;
    }

    @Data
    public static class PathInfo {
        private String name;
        private Boolean isComplete;
        private List<PathElement> elements;

        @Data
        public static class PathElement {
            private String id;
            private String name;
            private String nodeType;
        }
    }
}
