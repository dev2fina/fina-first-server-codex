package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class CapabilitiesRepresentation implements BaseRepresentation {
  private boolean isGuest;
  private boolean isAdmin;
  private boolean isMutable;

  public CapabilitiesRepresentation() {
  }

  @JsonProperty("isGuest")
  public boolean isGuest() {
    return isGuest;
  }

  public void setGuest(boolean guest) {
    isGuest = guest;
  }

  @JsonProperty("isAdmin")
  public boolean isAdmin() {
    return isAdmin;
  }

  public void setAdmin(boolean admin) {
    isAdmin = admin;
  }

  @JsonProperty("isMutable")
  public boolean isMutable() {
    return isMutable;
  }

  public void setMutable(boolean mutable) {
    isMutable = mutable;
  }

  @Override
  public String toString() {
    return "{" +
      "isGuest=" + isGuest +
      ", isAdmin=" + isAdmin +
      ", isMutable=" + isMutable +
      '}';
  }
}
