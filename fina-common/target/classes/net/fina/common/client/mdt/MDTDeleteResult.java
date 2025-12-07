package net.fina.common.client.mdt;

import java.io.Serializable;
import java.util.*;

/**
 * User: Alexander
 * Date: 11/27/13
 * Time: 8:34 AM
 */
public class MDTDeleteResult implements Serializable {
    private Set<String> parentDefinitionTableNodeWarnings;
    private Set<String> definitionTableNodeWarnings;
    private Map<String, String> dependentNodeWarnings;
    private Set<String> inReturnItemsNodeWarnings;
    private List<String> deletedNodes;
    private List<String> dependentUsers;
    private List<String> dependentRoles;

    public MDTDeleteResult() {
        this.parentDefinitionTableNodeWarnings = new HashSet<String>();
        this.definitionTableNodeWarnings = new HashSet<String>();
        this.dependentNodeWarnings = new HashMap<String, String>();
        this.inReturnItemsNodeWarnings = new HashSet<String>();
        this.deletedNodes = new ArrayList<String>();
        this.dependentUsers = new ArrayList<String>();
        this.dependentRoles = new ArrayList<String>();
    }

    public int getTotalSize() {
        return parentDefinitionTableNodeWarnings.size() + definitionTableNodeWarnings.size() + dependentNodeWarnings.size() + inReturnItemsNodeWarnings.size()
                + dependentUsers.size() + dependentRoles.size();
    }

    public Set<String> getParentDefinitionTableNodeWarnings() {
        return parentDefinitionTableNodeWarnings;
    }

    public Set<String> getDefinitionTableNodeWarnings() {
        return definitionTableNodeWarnings;
    }

    public Map<String, String> getDependentNodeWarnings() {
        return dependentNodeWarnings;
    }

    public Set<String> getInReturnItemsNodeWarnings() {
        return inReturnItemsNodeWarnings;
    }

    public List<String> getDeletedNodes() {
        return deletedNodes;
    }

    public List<String> getDependentUsers() {
        return dependentUsers;
    }

    public List<String> getDependentRoles() {
        return dependentRoles;
    }
}
