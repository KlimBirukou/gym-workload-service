package com.epam.gym.workload.controller.advice;

import com.epam.gym.workload.exception.NotAuthenticatedException;
import com.epam.gym.workload.exception.TrainerNotFoundException;
import com.epam.gym.workload.exception.TrainingAlreadyExistException;
import com.epam.gym.workload.exception.TrainingNotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String MESSAGE_500 = "An unexpected error occurred on the server side.";
    private static final String CONTENT_TYPE_MESSAGE =
        "Content type '%s' is not supported. Supported media types are: %s";
    private static final String TRAINING_NOT_FOUND = "Record of training for trainer %s on day %s not found";
    private static final String TRAINER_NOT_FOUND = "Trainer [%s] was not found";
    private static final String TRAINING_ALREADY_EXIST = "Record of training for trainer %s on day %s already exist";
    private static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    private static final String NOT_AUTHENTICATED_MESSAGE =
        "Authentication required. Please login to access this resource";

   @ExceptionHandler
    public ResponseEntity<@NonNull Object> handleUnauthorizedException(NotAuthenticatedException exception,
                                                                       WebRequest request) {
       return getObjectResponseEntity(
           exception,
           request,
           NOT_AUTHENTICATED_MESSAGE,
           HttpStatus.UNAUTHORIZED
       );
    }

    @ExceptionHandler
    public ResponseEntity<@NonNull Object> handleException(TrainingNotFoundException exception, WebRequest request) {
        return getObjectResponseEntity(
            exception,
            request,
            TRAINING_NOT_FOUND.formatted(exception.getUsername(), exception.getDate()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<@NonNull Object> handleException(TrainingAlreadyExistException exception, WebRequest request) {
        return getObjectResponseEntity(
            exception,
            request,
            TRAINING_ALREADY_EXIST.formatted(exception.getUsername(), exception.getDate()),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler
    public ResponseEntity<@NonNull Object> handleException(TrainerNotFoundException exception, WebRequest request) {
        return getObjectResponseEntity(
            exception,
            request,
            TRAINER_NOT_FOUND.formatted(exception.getUsername()),
            HttpStatus.NOT_FOUND
        );
    }

    @Override
    protected @Nullable ResponseEntity<@NonNull Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                                     @NonNull HttpHeaders headers,
                                                                                     @NonNull HttpStatusCode status,
                                                                                     @NonNull WebRequest request) {
        var message = ex.getBindingResult()
            .getAllErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining("; "));
        return getObjectResponseEntity(ex, request, message, VALIDATION_FAILED, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected @Nullable ResponseEntity<@NonNull Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                                        @NonNull HttpHeaders headers,
                                                                                        @NonNull HttpStatusCode status,
                                                                                        @NonNull WebRequest request) {
        var supportedTypes = ex.getSupportedMediaTypes()
            .stream()
            .map(MediaType::toString)
            .collect(Collectors.joining("; "));
        return getObjectResponseEntity(
            ex,
            request,
            CONTENT_TYPE_MESSAGE.formatted(ex.getContentType(), supportedTypes),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull Object> handleGeneralException(Exception exception, WebRequest request) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("Unexpected error", exception);
        var errorDto = ErrorDto.builder()
            .error(status.name())
            .description(MESSAGE_500)
            .build();
        return super.handleExceptionInternal(exception, errorDto, new HttpHeaders(), status, request);
    }

    private ResponseEntity<@NonNull Object> getObjectResponseEntity(Exception exception,
                                                                    WebRequest request,
                                                                    String message,
                                                                    HttpStatus status) {
        return getObjectResponseEntity(exception, request, message, status.name(), status);
    }

    private ResponseEntity<@NonNull Object> getObjectResponseEntity(Exception exception,
                                                                    WebRequest request,
                                                                    String message,
                                                                    String error,
                                                                    HttpStatus status) {
        log.info(message);
        var errorDto = ErrorDto.builder()
            .error(error)
            .description(message)
            .build();
        return super.handleExceptionInternal(exception, errorDto, new HttpHeaders(), status, request);
    }
}
