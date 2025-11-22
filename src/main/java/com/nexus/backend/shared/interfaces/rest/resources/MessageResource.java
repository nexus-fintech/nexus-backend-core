package com.nexus.backend.shared.interfaces.rest.resources;

/**
 * Resource representing a simple message response.
 *
 * @param message the message content
 */
public record MessageResource(String message) {
}