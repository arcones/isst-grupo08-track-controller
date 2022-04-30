package es.upm.isst.grupo08.trackback.controller.error.dataInconsistency;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DataInconsistencyAdvice {

    @ResponseBody
    @ExceptionHandler(DataInconsistencyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String dataInconsistencyHandler(DataInconsistencyException ex) {
        return ex.getMessage();
    }
}
