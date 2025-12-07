package net.fina.server.tag.api;

import net.fina.server.tag.entity.ObjectTag;
import net.fina.server.tag.entity.ObjectType;
import net.fina.server.tag.entity.Tag;

import java.util.List;

public interface TagLocal {
    List<Tag> getAll();

    Tag save(Tag tag);

    void delete(int tagId);

    void updateObjectTags(List<ObjectTag> objectTags);

    List<Tag> getObjectTags(ObjectType type, long objectId);
}
