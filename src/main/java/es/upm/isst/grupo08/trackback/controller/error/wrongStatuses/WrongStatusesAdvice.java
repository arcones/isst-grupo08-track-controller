package es.upm.isst.grupo08.trackback.controller.error.wrongStatuses;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WrongStatusesAdvice {

    @ResponseBody
    @ExceptionHandler(WrongStatusesException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String wrongStatusesHandler(WrongStatusesException ex) {
        return ex.getMessage();
    }
}
