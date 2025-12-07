package net.fina.server.mdt.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity(name = "IN_MDT_DEPENDENT_NODES")
@Table(name = "IN_MDT_DEPENDENT_NODES")
@NamedQueries({
        @NamedQuery(name = "MDTDependentNode.findAll", query = "select m from IN_MDT_DEPENDENT_NODES m "),
        @NamedQuery(name = "MDTDependentNode.findByNodeAndDepNodeIds", query = "select m from IN_MDT_DEPENDENT_NODES m where m.depNode.nodeId in :nodeIds or m.depNode.dependentNodeId in :dependentNodeIds"),
        @NamedQuery(name = "MDTDependentNode.findAllByDependentNodeIds", query = "select m from IN_MDT_DEPENDENT_NODES m where m.depNode.dependentNodeId in :dependentNodeIds "),
        @NamedQuery(name = "MDTDependentNodePK.findAllByNodeIds", query = "select m.depNode from IN_MDT_DEPENDENT_NODES m where m.depNode.nodeId in :nodeIds "),
        @NamedQuery(name = "MDTDependentNode.deleteComparisonDependency", query = "delete from IN_MDT_DEPENDENT_NODES where depNode.nodeId=:nodeId")
})
@SuppressWarnings("serial")
public class MDTDependentNode implements Serializable, Audited {
    private MDTDependentNodePK depNode;

    @EmbeddedId
    public MDTDependentNodePK getDepNode() {
        return depNode;
    }

    public void setDepNode(MDTDependentNodePK depNode) {
        this.depNode = depNode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((depNode == null) ? 0 : depNode.hashCode());
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
        MDTDependentNode other = (MDTDependentNode) obj;
        if (depNode == null) {
            if (other.depNode != null)
                return false;
        } else if (!depNode.equals(other.depNode))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MDTDependentNode [depNode=" + depNode + "]";
    }

}
