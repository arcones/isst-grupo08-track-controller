package es.upm.isst.grupo08.trackback.controller.error.duplicateParcelsInFile;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DuplicateParcelsAdvice {

    @ResponseBody
    @ExceptionHandler(DuplicateParcelsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String duplicateParcelsHandler(DuplicateParcelsException ex) {
        return ex.getMessage();
    }
}
