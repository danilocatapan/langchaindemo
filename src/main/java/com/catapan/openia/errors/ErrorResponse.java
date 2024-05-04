package com.catapan.openia.errors;

import jakarta.ws.rs.core.Response;

/**
 * Classe que representa uma resposta de erro para a API.
 * Contém informações sobre o erro que podem ser facilmente interpretadas pelo cliente.
 */
public class ErrorResponse {

    private int status;
    private String message;
    private String developerMessage;

    public ErrorResponse(int status, String message, String developerMessage) {
        this.status = status;
        this.message = message;
        this.developerMessage = developerMessage;
    }

    public ErrorResponse(String message) {
        this.status = Response.Status.BAD_REQUEST.getStatusCode();
        this.message = message;
        this.developerMessage = null;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }
}
