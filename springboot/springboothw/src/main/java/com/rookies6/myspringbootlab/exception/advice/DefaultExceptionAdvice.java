package com.rookies6.myspringbootlab.exception.advice;

import com.rookies6.myspringbootlab.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorObject> handleResourceNotFoundException(BusinessException ex) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(ex.getHttpStatus().value());
        errorObject.setMessage(ex.getMessage());

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(ex.getHttpStatus().value()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("message", e.getMessage());
        result.put("httpStatus", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorObject> handleException(RuntimeException e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setMessage(e.getMessage());

        log.error(e.getMessage(), e);

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(500));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.error(ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach((error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                400,
                "입력항목 검증 오류",
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.badRequest().body(response);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ValidationErrorResponse {
        private int status;
        private String message;
        private LocalDateTime timestamp;
        private Map<String, String> errors;
    }
}
