package com.avisheku.common.neo4j;

import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

@Node("Leaf")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeafEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String type;

    @NonNull
    private String name;

    private String displayName;

    private String description;
}