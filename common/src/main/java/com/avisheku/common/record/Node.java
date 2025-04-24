package com.avisheku.common.record;

import java.util.List;
import java.util.Optional;

public record Node(String name, String type, Optional<String> description, Optional<List<Node>> nodes, Optional<List<Leaf>> leafs) {
}

