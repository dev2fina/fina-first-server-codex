package net.fina.server.tag.proxy;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.tag.api.TagLocal;
import net.fina.server.tag.model.TagMetaModel;
import net.fina.server.tag.model.TagModelHelper;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class TagProxySession {

    @EJB
    private TagLocal tagLocal;

    public PaginatedListWrapper<TagMetaModel> loadTags() {
        PaginatedListWrapper<TagMetaModel> listWrapper = new PaginatedListWrapper<>();

        listWrapper.setList(TagModelHelper.get(tagLocal.getAll()));

        listWrapper.setTotalResults(listWrapper.getList().size());

        return listWrapper;
    }

    public TagMetaModel save(TagMetaModel model) {
        return TagModelHelper.get(tagLocal.save(TagModelHelper.get(model)));
    }

    public void delete(int tagId) {
        tagLocal.delete(tagId);
    }
}
