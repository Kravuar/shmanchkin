package net.kravuar.shmanchkin.domain.security;

import org.springframework.http.server.reactive.ServerHttpRequest;

public interface JWTExtractor {

    String extract(ServerHttpRequest request, String jwtName);
}
