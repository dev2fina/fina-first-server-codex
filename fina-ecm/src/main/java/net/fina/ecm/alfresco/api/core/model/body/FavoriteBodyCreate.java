
package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

/**
 * Created by jpascal on 25/08/2016.
 */
public class FavoriteBodyCreate implements BaseRepresentation {
    @JsonProperty("target")
    public Object target;

    public FavoriteBodyCreate() {
    }

    public enum FavoriteTypeEnum {
        SITE("SITE"),

        FILE("FILE"),

        FOLDER("FOLDER");

        private String value;

        FavoriteTypeEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public String value() {
            return value;
        }
    }

    public FavoriteBodyCreate(FavoriteTypeEnum type, String guid) {
        switch (type) {
            case FILE:
                this.target = new FavoriteFileRequest(new FavoriteGuid(guid));
                break;
            case FOLDER:
                this.target = new FavoriteFolderRequest(new FavoriteGuid(guid));
                break;
            case SITE:
                this.target = new FavoriteSiteRequest(new FavoriteGuid(guid));
                break;
            default:
                this.target = null;
        }
    }

    private static class FavoriteGuid {
        @JsonProperty("guid")
        public final String guid;

        public FavoriteGuid(String guid) {
            this.guid = guid;
        }
    }

    private static class FavoriteSiteRequest {
        @JsonProperty("site")
        public final FavoriteGuid site;

        public FavoriteSiteRequest(FavoriteGuid site) {
            this.site = site;
        }
    }

    private static class FavoriteFileRequest {
        @JsonProperty("file")
        public final FavoriteGuid site;

        public FavoriteFileRequest(FavoriteGuid site) {
            this.site = site;
        }
    }

    private static class FavoriteFolderRequest {
        @JsonProperty("folder")
        public final FavoriteGuid site;

        public FavoriteFolderRequest(FavoriteGuid site) {
            this.site = site;
        }
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
