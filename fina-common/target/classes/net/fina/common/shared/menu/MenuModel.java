package net.fina.common.shared.menu;

import java.io.Serializable;
import java.util.List;


@SuppressWarnings("serial")
public class MenuModel implements Serializable {
    private Menus menu;
    private String code;
    private String label;
    private boolean folder;
    private boolean expanded;

    private String resource;

    private boolean access;

    private List<String> permissions;

    private List<MenuModel> children;

    private String iconName;
    private boolean active;
    private String targetFrame;

    public MenuModel() {
    }

    public MenuModel(Menus menu, boolean folder) {
        this.code = menu.getCode();
        this.menu = menu;
        this.folder = folder;
    }

    public MenuModel(String code, Menus menu, boolean folder) {
        this(menu, folder);
        this.code = code;
    }

    public MenuModel(String code, Menus menu, boolean folder, List<MenuModel> children) {
        this(code, menu, folder);
        this.children = children;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<MenuModel> getChildren() {
        return children;
    }

    public void setChildren(List<MenuModel> children) {
        this.children = children;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Menus getMenu() {
        return menu;
    }

    public void setMenu(Menus menu) {
        this.menu = menu;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTargetFrame() {
        return targetFrame;
    }

    public void setTargetFrame(String targetFrame) {
        this.targetFrame = targetFrame;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MenuModel other = (MenuModel) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MenuModel{" +
                "menu=" + menu +
                ", code='" + code + '\'' +
                ", label='" + label + '\'' +
                ", folder=" + folder +
                ", expanded=" + expanded +
                ", resource='" + resource + '\'' +
                ", access=" + access +
                ", permissions=" + permissions +
                ", children=" + children +
                ", iconName='" + iconName + '\'' +
                ", active=" + active +
                '}';
    }
}
