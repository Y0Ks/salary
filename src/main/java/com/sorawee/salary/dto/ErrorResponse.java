package com.sorawee.salary.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private String errorMessage;
    private LocalDateTime errorTime;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorTime = LocalDateTime.now();
    }
}