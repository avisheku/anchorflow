package com.avisheku.common.record;

import java.util.List;
import java.util.Optional;

public record Anchor(String name, String type, Optional<String> description, Optional<List<Node>> nodes) {
}