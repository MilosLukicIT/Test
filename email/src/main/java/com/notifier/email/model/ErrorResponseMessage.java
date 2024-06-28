package com.notifier.email.model;

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
