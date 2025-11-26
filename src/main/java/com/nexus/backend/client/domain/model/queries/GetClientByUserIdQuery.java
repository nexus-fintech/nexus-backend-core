package com.nexus.backend.client.domain.model.queries;

public record GetClientByUserIdQuery(Long userId) {
    public GetClientByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
}