package com.avisheku.common.neo4j;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Node("Anchor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnchorEntity {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String type;

    @NonNull
    private String name;

    private String displayName;

    private String description;

    @Relationship(type = "INCLUDES", direction = Relationship.Direction.OUTGOING)
    private List<NodeEntity> nodes;
}