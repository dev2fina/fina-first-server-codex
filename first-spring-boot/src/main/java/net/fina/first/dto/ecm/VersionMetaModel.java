package net.fina.first.dto.ecm;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class VersionMetaModel {
    private String id;
    private String versionComment;
    private String name;
    private String nodeType;
    private boolean isFolder;
    private boolean isFile;
    private LocalDateTime modifiedAt;
    private NodeMetaModel.UserInfo modifiedByUser;
    private NodeMetaModel.ContentInfo content;
    private List<String> aspectNames;
    private Map<String, Object> properties;
}
