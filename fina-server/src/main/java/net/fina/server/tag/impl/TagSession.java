package net.fina.server.tag.impl;

import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.tag.api.TagLocal;
import net.fina.server.tag.entity.ObjectTag;
import net.fina.server.tag.entity.ObjectType;
import net.fina.server.tag.entity.Tag;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Local(TagLocal.class)
@Interceptors(RecordingAuditor.class)
public class TagSession implements TagLocal {

    @Inject
    private EntityManager em;

    @Override
    public List<Tag> getAll() {
        return em.createQuery("select t from IN_TAGS t", Tag.class).getResultList();
    }

    @Override
    public Tag save(Tag tag) {
        if (tag.getId() > 0) {
            tag = em.merge(tag);
        } else {
            tag.setId(0);
            em.persist(tag);
        }
        return tag;
    }

    @Override
    public void delete(int tagId) {
        em.createQuery("delete from IN_TAGS t where t.id=:tyagId").setParameter("tyagId", tagId).executeUpdate();
    }

    @Override
    public void updateObjectTags(List<ObjectTag> objectTags) {

        if (objectTags != null) {
            List<Long> objectIds = objectTags.stream().map(ObjectTag::getObjectId).collect(Collectors.toList());

            em.createQuery("delete from IN_OBJECT_TAGS ot where ot.objectId in(:objectIds)").setParameter("objectIds", objectIds).executeUpdate();

            objectTags.forEach(objectTag -> em.persist(objectTag));
        }
    }

    @Override
    public List<Tag> getObjectTags(ObjectType type, long objectId) {
        return em.createQuery("select ot.tag from IN_OBJECT_TAGS ot where ot.objectType=:type and  ot.objectId=:objectId", Tag.class)
                .setParameter("type", type)
                .setParameter("objectId", objectId)
                .getResultList();
    }
}
