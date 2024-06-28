package com.service.user.model.exception;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseMessage {

    private int statusCode;
    private String Message;
}
