package FileServer.SaveFile.controller;

import FileServer.SaveFile.Exception.CustomException;
import FileServer.SaveFile.dtos.common.ErrorApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorApi> handleMiException(CustomException e) {
        ErrorApi error = buildError(e.getMessage(), e.getStatus());
        return ResponseEntity.status(e.getStatus()).body(error);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorApi> handleError(Exception e) {
        ErrorApi error = buildError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> handleError(MethodArgumentNotValidException e) {
        ErrorApi error = buildError(e.getMessage(),HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    private ErrorApi buildError(String message, HttpStatus status) {
        return ErrorApi.builder().timestamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(status.getReasonPhrase())
                .status(status.value())
                .message(message)
                .build();
    }
}


