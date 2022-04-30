package es.upm.isst.grupo08.trackback.controller.error.wrongCredentials;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WrongCredentialsAdvice {

    @ResponseBody
    @ExceptionHandler(WrongCredentialsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String wrongCredentialsHandler(WrongCredentialsException ex) {
        return ex.getMessage();
    }
}
