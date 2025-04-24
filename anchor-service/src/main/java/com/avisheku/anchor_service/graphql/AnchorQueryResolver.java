package com.avisheku.anchor_service.graphql;

import com.avisheku.anchor_service.service.AnchorService;
import com.avisheku.common.neo4j.AnchorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class AnchorQueryResolver {

    private final AnchorService anchorService;

    @Autowired
    public AnchorQueryResolver(AnchorService anchorService) {
        this.anchorService = anchorService;
    }

    @QueryMapping
    public Optional<AnchorEntity> getAnchorByName(@Argument String name) {
        return anchorService.getAnchorByName(name);
    }

    @QueryMapping
    public List<AnchorEntity> getAllAnchors() {
        return anchorService.getAllAnchors();
    }
}
