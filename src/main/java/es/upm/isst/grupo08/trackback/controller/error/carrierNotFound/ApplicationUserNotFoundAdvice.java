package es.upm.isst.grupo08.trackback.controller.error.carrierNotFound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationUserNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(ApplicationUserNotFoundException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    String carrierNotFoundHandler(ApplicationUserNotFoundException ex) {
        return ex.getMessage();
    }
}
