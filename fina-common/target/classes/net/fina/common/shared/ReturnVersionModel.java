package net.fina.common.shared;

import java.io.Serializable;

public class ReturnVersionModel implements Serializable {

    private long id;
    private String code;
    private Integer version;
    private long sequence;
    private long nameStrId;
    private String name;

    private static long identitySequence = 0;

    //user management
    private boolean userReturnVersion;
    private boolean userRoleReturnVersion;

    private boolean hasAmendFromRole;


    private boolean canUserReview;
    private boolean canUserAmend;

    public ReturnVersionModel() {
        this.sequence = ++identitySequence;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
        identitySequence = Math.max(sequence, identitySequence);
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isCanUserAmend() {
        return canUserAmend;
    }

    public void setCanUserAmend(boolean canUserAmend) {
        this.canUserAmend = canUserAmend;
        this.canUserReview |= canUserAmend;
    }

    public boolean isCanUserReview() {
        return canUserReview;
    }

    public void setCanUserReview(boolean canUserReview) {
        this.canUserReview = canUserReview;
        this.canUserAmend &= canUserReview;
    }

    public boolean isUserReturnVersion() {
        return userReturnVersion;
    }

    public void setUserReturnVersion(boolean userReturnVersion) {
        this.userReturnVersion = userReturnVersion;
    }

    public boolean isUserRoleReturnVersion() {
        return userRoleReturnVersion;
    }

    public void setUserRoleReturnVersion(boolean userRoleReturnVersion) {
        this.userRoleReturnVersion = userRoleReturnVersion;
    }

    public boolean isHasAmendFromRole() {
        return hasAmendFromRole;
    }

    public void setHasAmendFromRole(boolean hasAmendFromRole) {
        this.hasAmendFromRole = hasAmendFromRole;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
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
        ReturnVersionModel other = (ReturnVersionModel) obj;
        return id == other.id;
    }

}
