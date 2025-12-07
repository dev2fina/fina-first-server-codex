package net.fina.ecm.alfresco.api.core.model.representation;


public class FavoriteNodeRepresentation extends FavoriteRepresentation<NodeRepresentation>
{
    public FavoriteNodeRepresentation(NodeRepresentation file)
    {
        this.target = file;
    }

    public NodeRepresentation getFile()
    {
        return target;
    }

    public void setFile(NodeRepresentation target)
    {
        this.target = target;
    }
}
