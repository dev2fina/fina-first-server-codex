package net.fina.server.postbox.impl;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinAAccessDeniedException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.security.api.AuthorizationLocal;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.postbox.api.PostboxLocal;
import net.fina.server.postbox.entity.PostboxFile;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.server.util.PostboxFileUtil;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.text.MessageFormat;
import java.util.*;

@Stateless
@Local(PostboxLocal.class)
@Interceptors(RecordingAuditor.class)
public class PostboxSession implements PostboxLocal {

    @Inject
    private EntityManager em;

    @EJB
    private UserLocal current;

    @EJB
    private AuthorizationLocal authorizationLocal;

    @Override
    public PostboxFile findById(long id) {

        PostboxFile postboxFile = em.find(PostboxFile.class, id);

        //Check permission
        checkPostBoxFilePermission(postboxFile);

        return postboxFile;
    }

    @Override
    public List<PostboxFile> load(int limit, int offset, SortInfo sortInfo) {
        boolean internalUser = authorizationLocal.hasUserPermission(current.getCurrentUserLogin(), PermissionIdNames.FINA_WEB_INTERNAL_USER);

        TypedQuery<PostboxFile> query;

        if (internalUser) {
            String queryString = "select pbf from IN_POSTBOX_FILES pbf order by pbf.{0} {1}";
            queryString = MessageFormat.format(queryString, sortInfo.getSortField(), sortInfo.getSortDir());

            query = em.createQuery(
                    queryString,
                    PostboxFile.class
            );
        } else {
            String queryString = "select pbf from IN_POSTBOX_FILES pbf left outer join pbf.users pbfu where pbfu.id = :userId order by pbf.{0} {1}";
            queryString = MessageFormat.format(queryString, sortInfo.getSortField(), sortInfo.getSortDir());

            query = em.createQuery(
                    queryString,
                    PostboxFile.class
            );
            query.setParameter("userId", current.getCurrentUserId());
        }

        if (limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public PostboxFile save(PostboxFile file) {
        User currentUser = em.find(User.class, current.getCurrentUserId());

        if (file.getUsers() == null || file.getUsers().isEmpty()) {
            file.setUsers(Collections.singletonList(currentUser));
        } else {
            Collection<User> users = new HashSet<User>(file.getUsers().size());
            for (User user : file.getUsers()) {
                users.add(em.find(User.class, user.getId()));
            }
            file.setUsers(users);
        }

        if (file.getId() > 0) {
            PostboxFile tmp = em.find(PostboxFile.class, file.getId());
            file.setUploadTime(tmp.getUploadTime());
            file.setPublisher(tmp.getPublisher());
            file = em.merge(file);
        } else {
            file.setId(0);
            file.setUploadTime(new Date());
            file.setPublisher(currentUser);
            em.persist(file);
        }
        return file;
    }

    @Override
    public void delete(long id) throws FinATypeException {
        PostboxFile file = findById(id);

        new PostboxFileUtil().removeContent(file.getId(), file.getName());

        em.createQuery("delete from IN_POSTBOX_FILES pbf where pbf.id=:id")
                .setParameter("id", file.getId())
                .executeUpdate();
    }


    @Override
    public void saveContent(PostboxFile file, byte[] content) throws FinATypeException {
        new PostboxFileUtil().saveContent(file.getId(), file.getName(), content);
    }

    @Override
    public byte[] getContent(PostboxFile file) throws FinATypeException {
        if (!authorizationLocal.hasUserPermission(current.getCurrentUserLogin(), PermissionIdNames.FINA_WEB_INTERNAL_USER)) {
            checkPostBoxFilePermission(file);
        }

        return new PostboxFileUtil().getContent(file.getId(), file.getName());
    }

    @Override
    public long getFileCount() {
        boolean internalUser = authorizationLocal.hasUserPermission(current.getCurrentUserLogin(), PermissionIdNames.FINA_WEB_INTERNAL_USER);

        TypedQuery<Long> query;

        if (internalUser) {
            query = em.createQuery(
                    "select count(pbf) from IN_POSTBOX_FILES pbf",
                    Long.class
            );
        } else {
            query = em.createQuery(
                    "select count(pbf) from IN_POSTBOX_FILES pbf left outer join pbf.users pbfu where pbfu.id = :userId",
                    Long.class
            );
            query.setParameter("userId", current.getCurrentUserId());
        }

        return query.getSingleResult();
    }

    private void checkPostBoxFilePermission(PostboxFile file) {
        long currentUserId = current.getCurrentUserId();

        if (currentUserId == file.getPublisher().getId()) {
            return;
        }

        for (User user : file.getUsers()) {
            if (user.getId() == currentUserId) {
                return;
            }
        }

        throw new FinAAccessDeniedException();
    }

}
