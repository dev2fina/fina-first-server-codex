package net.fina.ecm.alfresco;

import net.fina.ecm.alfresco.api.core.*;
import net.fina.ecm.alfresco.api.dictionary.DictionaryAPI;
import net.fina.ecm.alfresco.api.search.SearchAPI;
import net.fina.ecm.alfresco.api.workflow.WorkflowAPI;


public class AlfrescoClient extends AbstractClient<AlfrescoClient> {

    private static final Object LOCK = new Object();

    private static AlfrescoClient mInstance;

    private NodesAPI nodesAPI;

    private VersionAPI versionAPI;

    private PeopleAPI peopleAPI;

    private GroupsAPI groupsAPI;

    private QueriesAPI queriesAPI;

    private SearchAPI searchAPI;

    private DictionaryAPI dictionaryAPI;

    private WorkflowAPI workflowAPI;

    private FavoritesAPI favoritesAPI;

    private DownloadAPI downloadAPI;

    private TrashAPI trashAPI;

    private SitesAPI sitesAPI;

    private ActionsAPI actionsAPI;

    private RenditionsAPI renditionsAPI;

    private TagsAPI tagsAPI;

    private WebScriptApi webScriptApi;

    private FileImportAPI fileImportAPI;

    private SharedLinksAPI sharedLinksAPI;

    public static AlfrescoClient getInstance() {
        synchronized (LOCK) {
            return mInstance;
        }
    }

    private AlfrescoClient(RestClient restClient) {
        super(restClient);
    }

    public static class Builder extends AbstractClient.Builder<AlfrescoClient> {

        @Override
        public AlfrescoClient create(RestClient restClient) {
            return new AlfrescoClient(restClient);
        }

        @Override
        public AlfrescoClient build() {
            mInstance = super.build();
            return mInstance;
        }
    }

    public NodesAPI getNodesAPI() {
        if (nodesAPI == null) {
            nodesAPI = getAPI(NodesAPI.class);
        }
        return nodesAPI;
    }

    public VersionAPI getVersionAPI() {
        if (versionAPI == null) {
            versionAPI = getAPI(VersionAPI.class);
        }
        return versionAPI;
    }

    public PeopleAPI getPeopleAPI() {
        if (peopleAPI == null) {
            peopleAPI = getAPI(PeopleAPI.class);
        }
        return peopleAPI;
    }

    public GroupsAPI getGroupsAPI() {
        if (groupsAPI == null) {
            groupsAPI = getAPI(GroupsAPI.class);
        }
        return groupsAPI;
    }

    public QueriesAPI getQueriseAPI() {
        if (queriesAPI == null) {
            queriesAPI = getAPI(QueriesAPI.class);
        }
        return queriesAPI;
    }

    public SearchAPI getSearchAPI() {
        if (searchAPI == null) {
            searchAPI = getAPI(SearchAPI.class);
        }
        return searchAPI;
    }

    public DictionaryAPI getDictionaryAPI() {
        if (dictionaryAPI == null) {
            dictionaryAPI = getAPI(DictionaryAPI.class);
        }
        return dictionaryAPI;
    }

    public WorkflowAPI getWorkflowAPI() {
        if (workflowAPI == null) {
            workflowAPI = getAPI(WorkflowAPI.class);
        }
        return workflowAPI;
    }

    public FavoritesAPI getFavoritesAPI() {
        if (favoritesAPI == null) {
            favoritesAPI = getAPI(FavoritesAPI.class);
        }
        return favoritesAPI;
    }

    public DownloadAPI getDownloadAPI() {
        if (downloadAPI == null) {
            downloadAPI = getAPI(DownloadAPI.class);
        }

        return downloadAPI;
    }

    public TrashAPI getTrashAPI() {
        if (trashAPI == null) {
            trashAPI = getAPI(TrashAPI.class);
        }

        return trashAPI;
    }

    public SitesAPI getSitesAPI() {
        if (sitesAPI == null) {
            sitesAPI = getAPI(SitesAPI.class);
        }
        return sitesAPI;
    }

    public ActionsAPI getActionsAPI() {
        if (actionsAPI == null) {
            actionsAPI = getAPI(ActionsAPI.class);
        }
        return actionsAPI;
    }

    public RenditionsAPI getRenditionsAPI() {
        if (renditionsAPI == null) {
            renditionsAPI = getAPI(RenditionsAPI.class);
        }
        return renditionsAPI;
    }

    public TagsAPI getTagsAPI() {
        if (tagsAPI == null) {
            tagsAPI = getAPI(TagsAPI.class);
        }
        return tagsAPI;
    }

    public WebScriptApi getWebScriptApi() {
        if (webScriptApi == null) {
            webScriptApi = getAPI(WebScriptApi.class);
        }
        return webScriptApi;
    }

    public FileImportAPI getFileImportAPI() {
        if (fileImportAPI == null) {
            fileImportAPI = getAPI(FileImportAPI.class);
        }
        return fileImportAPI;
    }

    public SharedLinksAPI getSharedLinksAPI() {
        if (sharedLinksAPI == null) {
            sharedLinksAPI = getAPI(SharedLinksAPI.class);
        }
        return sharedLinksAPI;
    }

}
