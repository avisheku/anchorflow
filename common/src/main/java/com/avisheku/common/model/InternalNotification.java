package com.avisheku.common.model;

public record InternalNotification(
    String id,
    String type,
    String action,
    Object payload,
    long timestamp
) {} 