package org.openmrs.module.metadatamapping.web.rest;

import org.openmrs.api.context.Context;
import org.openmrs.module.metadatamapping.MetadataSource;
import org.openmrs.module.metadatamapping.api.MetadataMappingService;
import org.openmrs.module.metadatamapping.web.controller.MetadataMappingRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MetadataSourceSearchHandler implements SearchHandler {

    private final SearchConfig searchConfig = new SearchConfig("default",
            RestConstants.VERSION_1 + MetadataMappingRestController.METADATA_MAPPING_REST_NAMESPACE + "/source",
            Arrays.asList("1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"),
            new SearchQuery.Builder("Allows you to find metadata sources by name").withRequiredParameters("name").withOptionalParameters("searchType").build());

    private static String SEARCH_TYPE_EQUALS = "equals";
    private static String SEARCH_TYPE_STARTS_WITH = "startsWith";

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     * @should return metadata source by name
     */
    @Override
    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#search(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        String name = context.getParameter("name");
        String searchType = context.getParameter("searchType");

        if(searchType!=null&&!SEARCH_TYPE_EQUALS.equals(searchType)&&!SEARCH_TYPE_STARTS_WITH.equals(searchType)){
            throw new InvalidSearchException("Invalid searchType parameter");
        }

        if( name != null){
            List<MetadataSource> metadataSources = getService().getMetadataSources(context.getIncludeAll());

        } else {
            List<MetadataSource> metadataSources = getService().getMetadataSources(context.getStartIndex(), context.getLimit(), context.getIncludeAll());
            Long count = getService().getCountOfMetadataSource(context.getIncludeAll());
            boolean hasMore = count > context.getStartIndex() + context.getLimit();
            return new AlreadyPaged<MetadataSource>(context, metadataSources, hasMore);
        }
    }

    public MetadataMappingService getService() {
        return Context.getService(MetadataMappingService.class);
    }
}
