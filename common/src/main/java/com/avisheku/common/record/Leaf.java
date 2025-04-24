package com.avisheku.common.record;

import java.util.Optional;

public record Leaf(String name, String type, Optional<String> description) {
}