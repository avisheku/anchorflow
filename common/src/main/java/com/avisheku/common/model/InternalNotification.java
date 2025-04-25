package com.avisheku.common.model;

public record InternalNotification(
    String id,
    String type,
    String action,
    String payload,
    long timestamp
) {} 