package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentCreateRequest {

    @NotBlank(message = "El contenido no puede estar vacío.")
    @Size(max = 500, message = "El contenido del comentario no puede superar los 500 caracteres.")
    private String content;

    public CommentCreateRequest() {
    }

    public CommentCreateRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
