package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.response;

import java.time.LocalDateTime;

public class CommentResponse {
    private Long id; // ✅ Agregado
    private String content;
    private LocalDateTime createdAt;
    private String username;

    public CommentResponse(Long id, String content, LocalDateTime createdAt, String username) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getUsername() {
        return username;
    }
}
