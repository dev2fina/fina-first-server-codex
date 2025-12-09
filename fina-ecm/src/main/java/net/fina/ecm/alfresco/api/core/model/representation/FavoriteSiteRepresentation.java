
package net.fina.ecm.alfresco.api.core.model.representation;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

/**
 * Favorite
 */
public class FavoriteSiteRepresentation extends FavoriteRepresentation<SiteRepresentation> implements BaseRepresentation {
    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

    public FavoriteSiteRepresentation(SiteRepresentation file) {
        this.target = file;
    }

    public SiteRepresentation getSite() {
        return target;
    }

    public void setSite(SiteRepresentation target) {
        this.target = target;
    }
}
