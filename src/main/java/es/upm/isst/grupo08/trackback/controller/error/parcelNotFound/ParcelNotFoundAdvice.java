package es.upm.isst.grupo08.trackback.controller.error.parcelNotFound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ParcelNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(ParcelNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String parcelNotFoundHandler(ParcelNotFoundException ex) {
        return ex.getMessage();
    }
}
