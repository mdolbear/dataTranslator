package com.mjdsft.k8provision.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ErrorMessage {

    private HttpStatus status;
    private String message;

}
