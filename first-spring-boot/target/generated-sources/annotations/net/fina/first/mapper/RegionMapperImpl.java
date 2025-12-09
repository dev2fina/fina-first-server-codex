package net.fina.first.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import net.fina.first.dto.response.RegionResponse;
import net.fina.first.model.Region;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-09T19:20:37+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class RegionMapperImpl implements RegionMapper {

    @Override
    public RegionResponse toResponse(Region entity) {
        if ( entity == null ) {
            return null;
        }

        RegionResponse.RegionResponseBuilder regionResponse = RegionResponse.builder();

        regionResponse.parentId( entityParentId( entity ) );
        regionResponse.id( entity.getId() );
        regionResponse.code( entity.getCode() );
        regionResponse.name( entity.getName() );
        regionResponse.nameLocal( entity.getNameLocal() );
        regionResponse.level( entity.getLevel() );
        regionResponse.sequence( entity.getSequence() );

        return regionResponse.build();
    }

    @Override
    public RegionResponse toResponseWithChildren(Region entity) {
        if ( entity == null ) {
            return null;
        }

        RegionResponse.RegionResponseBuilder regionResponse = RegionResponse.builder();

        regionResponse.parentId( entityParentId( entity ) );
        regionResponse.id( entity.getId() );
        regionResponse.code( entity.getCode() );
        regionResponse.name( entity.getName() );
        regionResponse.nameLocal( entity.getNameLocal() );
        regionResponse.level( entity.getLevel() );
        regionResponse.sequence( entity.getSequence() );
        regionResponse.children( regionListToRegionResponseList( entity.getChildren() ) );

        return regionResponse.build();
    }

    private Long entityParentId(Region region) {
        if ( region == null ) {
            return null;
        }
        Region parent = region.getParent();
        if ( parent == null ) {
            return null;
        }
        Long id = parent.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<RegionResponse> regionListToRegionResponseList(List<Region> list) {
        if ( list == null ) {
            return null;
        }

        List<RegionResponse> list1 = new ArrayList<RegionResponse>( list.size() );
        for ( Region region : list ) {
            list1.add( regionToRegionResponse( region ) );
        }

        return list1;
    }

    protected RegionResponse regionToRegionResponse(Region region) {
        if ( region == null ) {
            return null;
        }

        RegionResponse.RegionResponseBuilder regionResponse = RegionResponse.builder();

        regionResponse.id( region.getId() );
        regionResponse.code( region.getCode() );
        regionResponse.name( region.getName() );
        regionResponse.nameLocal( region.getNameLocal() );
        regionResponse.level( region.getLevel() );
        regionResponse.sequence( region.getSequence() );
        regionResponse.children( regionListToRegionResponseList( region.getChildren() ) );

        return regionResponse.build();
    }
}
