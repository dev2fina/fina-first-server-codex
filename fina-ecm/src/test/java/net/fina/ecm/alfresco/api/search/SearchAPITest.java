package net.fina.ecm.alfresco.api.search;

import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.search.body.*;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchAPITest extends AlfrescoAPITestCase {
    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void querySearchTest() throws Exception {
        RequestQuery query = new RequestQuery().query("*jack*").language(RequestQuery.LanguageEnum.AFTS);
        QueryBody body = new QueryBody().query(query);

        // Request
        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);
        System.out.println(resultSet.getCount());

    }

    @Test
    public void testQueryCMISSearchPlaceHolders() {
        RequestQuery query = new RequestQuery().query("select * from fina:fiRegistryPlaceholder where fina:fiRegistryPlaceholderIdNumber='01001068'").language(RequestQuery.LanguageEnum.CMIS);
        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);

        List<ResultNodeRepresentation> results = resultSet.getObjects();
        results.forEach(System.out::println);
        System.out.println(results.size());
    }


    @Test
    public void testQueryCMISBranches() {
        RequestQuery query = new RequestQuery().query("select * from fina:fiRegistryBranch").language(RequestQuery.LanguageEnum.CMIS);
        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);

        List<ResultNodeRepresentation> results = resultSet.getObjects();
        System.out.println(results.size());
    }

    @Test
    public void testQueryCMISBranchByName() {
        RequestQuery query = new RequestQuery().query("select * from fina:fiRegistryBranch where cmis:name='Branch-005'").language(RequestQuery.LanguageEnum.CMIS);
        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);

        List<ResultNodeRepresentation> results = resultSet.getObjects();
        System.out.println(results.size());
    }


    @Test
    public void testQueryCMISBankCode() {
        RequestQuery query = new RequestQuery().query("select * from fina:fiRegistry where cmis:name='FI_001'").language(RequestQuery.LanguageEnum.CMIS);
        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);

        List<ResultNodeRepresentation> results = resultSet.getObjects();
        System.out.println(results.size());
    }

    @Test
    public void testSmisInQuery() {
        String queryString = "select * from fina:fiRegistry where IN_FOLDER('9311bee1-c32b-40ec-bba9-d2534570d2dd') and fina:fiRegistryFiTypeCode IN ('BNK','BRC')";
        RequestQuery query = new RequestQuery().query(queryString).language(RequestQuery.LanguageEnum.CMIS);

        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties")).paging(new RequestPagination().skipCount(0).maxItems(Integer.MAX_VALUE));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);
        System.out.println(resultSet.getObjects().size());


    }

    @Test
    public void testQueryCMISBankName() {
        RequestQuery query = new RequestQuery().query("select * from fina:fiRegistry where fina:fiRegistryName='nameX_9vy1N'").language(RequestQuery.LanguageEnum.CMIS);
        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);

        List<ResultNodeRepresentation> results = resultSet.getObjects();
        System.out.println(results.size());
    }

    @Test
    public void testQueryCMISBankType() {
        RequestQuery query = new RequestQuery().query("select * from fina:fiRegistry where fina:fiRegistryFiType='BNK'").language(RequestQuery.LanguageEnum.CMIS);
        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);

        List<ResultNodeRepresentation> results = resultSet.getObjects();
        System.out.println(results.size());
    }

    @Test
    public void testQueryCMISQuestionaries() {
        RequestQuery query = new RequestQuery().query("select * from fina:questionnaire ").language(RequestQuery.LanguageEnum.CMIS);
        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);

        List<ResultNodeRepresentation> results = resultSet.getObjects();
        System.out.println(results.size());
    }


    @Test
    public void testRecentFilesQuery() {
        QueryBody queryBody = new QueryBody();
        RequestQuery query = new RequestQuery();
        query.setLanguage(RequestQuery.LanguageEnum.AFTS);
        query.setQuery("*");

        queryBody.setQuery(query);

        RequestSortDefinition sort = new RequestSortDefinition();
        sort.setType(RequestSortDefinition.TypeEnum.FIELD);
        sort.setField("cm:modified");

        queryBody.setSort(Collections.singletonList(sort));

        RequestPagination pagination = new RequestPagination();
        pagination.setMaxItems(100);
        pagination.setSkipCount(0);

        queryBody.setPaging(pagination);
        List<String> include = Arrays.asList("properties", "path");

        queryBody.setInclude(include);

        queryBody.setFilterQueries(Arrays.asList(new RequestFilterQuery().query("cm:modified:[NOW/DAY-30DAYS TO NOW/DAY+1DAY]"),
                new RequestFilterQuery().query("cm:modifier:admin OR cm:creator:admin"),
                new RequestFilterQuery().query("TYPE:\"content\" AND -PNAME:\"0/wiki\" AND -TYPE:\"app:filelink\" AND -TYPE:\"fm:post\" AND -TYPE:\"cm:thumbnail\" AND -TYPE:\"cm:failedThumbnail\" AND -TYPE:\"cm:rating\" AND -TYPE:\"dl:dataList\" AND -TYPE:\"dl:todoList\" AND -TYPE:\"dl:issue\" AND -TYPE:\"dl:contact\" AND -TYPE:\"dl:eventAgenda\" AND -TYPE:\"dl:event\" AND -TYPE:\"dl:task\" AND -TYPE:\"dl:simpletask\" AND -TYPE:\"dl:meetingAgenda\" AND -TYPE:\"dl:location\" AND -TYPE:\"fm:topic\" AND -TYPE:\"fm:post\" AND -TYPE:\"ia:calendarEvent\" AND -TYPE:\"lnk:link\""))
        );

        ResultSetRepresentation<ResultNodeRepresentation> resultset = client.getSearchAPI().search(queryBody);
        System.out.println(resultset.getCount());
    }


    @Test
    public void testAFTSSearch() {
        String searchStr = "fina:fiRegistryEmail:\"*fina*\"";
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.AFTS);
        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = client.getSearchAPI().search(queryBody);


        resultSearch.getObjects().forEach(System.out::println);
        System.out.println(resultSearch.getCount());
    }


    @Test
    public void testAFTSSortedSearch() {
        String searchStr = "fina:fiRegistryIdentity:'*010000*'";
        List<String> sortProps = Arrays.asList("fina:fiRegistryName", "fina:fiRegistryFiTypeCode");
        List<RequestSortDefinition> sortDefinitions = new ArrayList<>();

        for (String sortProp : sortProps) {
            RequestSortDefinition rsd = new RequestSortDefinition().field(sortProp).ascending(true);
            sortDefinitions.add(rsd);
        }

        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.AFTS);
        QueryBody queryBody = new QueryBody().query(query)
                .include(Arrays.asList("properties", "path", "association"))
                .sort(sortDefinitions);

        try {
            ResultSetRepresentation<ResultNodeRepresentation> resultSearch = client.getSearchAPI().search(queryBody);
            List<ResultNodeRepresentation> result = resultSearch.getObjects();
            if (result != null) {
                for (ResultNodeRepresentation rnr : result) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(rnr.getProperties().get("fina:fiRegistryIdentity")).append(" ");

                    for (String propName : sortProps) {
                        sb.append(rnr.getProperties().get(propName)).append(" ");
                    }
                    System.out.println(sb.toString());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
    }

    @Test
    public void testSearchLinkedInformation() {
        String searchStr = "(fina:fiRegistryIdentity:\"1222\" OR nbg:fiRegistryNumberOfOrder:\"null\") OR ((fina:fiPersonFirstName:\"Otar\" AND fina:fiPersonLastName:\"Iantbelidze\") OR fina:fiPersonPersonalNumber:\"sad\") OR ((fina:fiPersonFirstName:\"giorgi\" AND fina:fiPersonLastName:\"leonidze\") OR fina:fiPersonPersonalNumber:\"asda\")";
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.AFTS);
        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"))
                .filterQueries(Arrays.asList(
                        new RequestFilterQuery().query("-PATH:'//cm:_x0030_003//*'"),
                        new RequestFilterQuery().query("-cm:name:'0003'"),
                        new RequestFilterQuery().query("+TYPE:'nbg:fiBeneficiary' OR +TYPE:'nbg:fiAuthorizedPerson' OR +TYPE:'fina:fiRegistry'"),
                        new RequestFilterQuery().query("(fina:fiRegistryIdentity:\"*Otar*\" OR nbg:fiRegistryNumberOfOrder:\"*Otar*\") OR (fina:fiPersonFirstName:\"*Otar*\" OR fina:fiPersonLastName:\"*Otar*\")")
                )).sort(Collections.singletonList(new RequestSortDefinition().field("cm:name")));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = client.getSearchAPI().search(queryBody);


        resultSearch.getObjects().forEach(System.out::println);
        System.out.println(resultSearch.getCount());
    }

    @Test
    public void testSearchBeneficiariesAndAuthorithies() {
        String searchStr = "*";
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.AFTS);
        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"))
                .filterQueries(Arrays.asList(
                        new RequestFilterQuery().query("+PATH:'//cm:" + AlfrescoUtil.getISO9075String("0002") + "//*'"),
                        new RequestFilterQuery().query("+TYPE:'nbg:fiBeneficiary' OR +TYPE:'nbg:fiAuthorizedPerson'")
                ));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = client.getSearchAPI().search(queryBody);


        resultSearch.getObjects().forEach(System.out::println);
        System.out.println(resultSearch.getCount());
    }

    @Test
    public void searchUserANdGroupsTest() {
        String queryTemplate = "authorityName:*%* OR userName:*%*";
        RequestQuery requestQuery = new RequestQuery().query(queryTemplate).language(RequestQuery.LanguageEnum.AFTS);

        QueryBody queryBody = new QueryBody().query(requestQuery);
        RequestFilterQuery reqQuery = new RequestFilterQuery();
        reqQuery.setQuery("TYPE:'cm:authority'");

        queryBody.setFilterQueries(Arrays.asList(reqQuery));

        ResultSetRepresentation<ResultNodeRepresentation> result = client.getSearchAPI().search(queryBody);
        List<ResultNodeRepresentation> filtered = result.getObjects().stream()
                .filter(o -> (o.getId().startsWith("GROUP_") && !o.getId().startsWith("GROUP_site")) || o.getNodeType().equals("cm:person"))
                .collect(Collectors.toList());
        System.out.println(filtered);
    }

}