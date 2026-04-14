package com.epam.gym.workload.controller.advice;

import com.epam.gym.workload.exception.NotAuthenticatedException;
import com.epam.gym.workload.exception.TrainerNotFoundException;
import com.epam.gym.workload.exception.TrainingAlreadyExistException;
import com.epam.gym.workload.exception.TrainingNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final String TRAINER_USERNAME = "trainer.username";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2026, 4, 12);
    private static final String VALIDATION_ERROR = "validation error message";
    private static final String ERROR_MESSAGE_500 = "An unexpected error occurred on the server side.";
    private static final String NOT_AUTHENTICATED_MESSAGE = "Authentication required. Please login to access this resource";
    private static final String GENERAL_EXCEPTION_MESSAGE = "unexpected system failure";
    private static final String FRAGMENT_NOT_FOUND = "not found";
    private static final String FRAGMENT_ALREADY_EXIST = "already exist";
    private static final String FRAGMENT_WAS_NOT_FOUND = "was not found";
    private static final String ERROR_UNAUTHORIZED = "UNAUTHORIZED";
    private static final String ERROR_NOT_FOUND = "NOT_FOUND";
    private static final String ERROR_CONFLICT = "CONFLICT";
    private static final String ERROR_VALIDATION_FAILED = "VALIDATION_FAILED";
    private static final String ERROR_UNSUPPORTED_MEDIA_TYPE = "UNSUPPORTED_MEDIA_TYPE";
    private static final String ERROR_INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    @Mock
    private WebRequest request;
    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private ObjectError objectError;
    @Mock
    private HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException;

    @InjectMocks
    private GlobalExceptionHandler testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(
            request,
            methodArgumentNotValidException,
            bindingResult,
            objectError,
            httpMediaTypeNotSupportedException
        );
    }

    @Test
    void handleNotAuthenticatedException() {
        var response = testObject.handleUnauthorizedException(new NotAuthenticatedException(), request);

        assertResponse(response, HttpStatus.UNAUTHORIZED, ERROR_UNAUTHORIZED);
        assertDescriptionContains(response, NOT_AUTHENTICATED_MESSAGE);
    }

    @Test
    void handleTrainingNotFoundException() {
        var exception = mock(TrainingNotFoundException.class);
        doReturn(TRAINER_USERNAME).when(exception).getUsername();
        doReturn(TRAINING_DATE).when(exception).getDate();

        var response = testObject.handleException(exception, request);

        assertResponse(response, HttpStatus.NOT_FOUND, ERROR_NOT_FOUND);
        assertDescriptionContains(response, TRAINER_USERNAME);
        assertDescriptionContains(response, TRAINING_DATE.toString());
        assertDescriptionContains(response, FRAGMENT_NOT_FOUND);
    }

    @Test
    void handleTrainingAlreadyExistException() {
        var exception = mock(TrainingAlreadyExistException.class);
        doReturn(TRAINER_USERNAME).when(exception).getUsername();
        doReturn(TRAINING_DATE).when(exception).getDate();

        var response = testObject.handleException(exception, request);

        assertResponse(response, HttpStatus.CONFLICT, ERROR_CONFLICT);
        assertDescriptionContains(response, TRAINER_USERNAME);
        assertDescriptionContains(response, TRAINING_DATE.toString());
        assertDescriptionContains(response, FRAGMENT_ALREADY_EXIST);
    }

    @Test
    void handleTrainerNotFoundException() {
        var exception = mock(TrainerNotFoundException.class);
        doReturn(TRAINER_USERNAME).when(exception).getUsername();

        var response = testObject.handleException(exception, request);

        assertResponse(response, HttpStatus.NOT_FOUND, ERROR_NOT_FOUND);
        assertDescriptionContains(response, TRAINER_USERNAME);
        assertDescriptionContains(response, FRAGMENT_WAS_NOT_FOUND);
    }

    @Test
    void handleMethodArgumentNotValid() {
        doReturn(VALIDATION_ERROR).when(objectError).getDefaultMessage();
        doReturn(List.of(objectError)).when(bindingResult).getAllErrors();
        doReturn(bindingResult).when(methodArgumentNotValidException).getBindingResult();

        var response = testObject.handleMethodArgumentNotValid(
            methodArgumentNotValidException,
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );

        assertResponse(response, HttpStatus.BAD_REQUEST, ERROR_VALIDATION_FAILED);
        assertDescriptionContains(response, VALIDATION_ERROR);
    }

    @Test
    void handleHttpMediaTypeNotSupported() {
        doReturn(MediaType.TEXT_PLAIN).when(httpMediaTypeNotSupportedException).getContentType();
        doReturn(List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML))
            .when(httpMediaTypeNotSupportedException).getSupportedMediaTypes();

        var response = testObject.handleHttpMediaTypeNotSupported(
            httpMediaTypeNotSupportedException,
            new HttpHeaders(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            request
        );

        assertResponse(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE, ERROR_UNSUPPORTED_MEDIA_TYPE);
        assertDescriptionContains(response, MediaType.TEXT_PLAIN.toString());
        assertDescriptionContains(response, MediaType.APPLICATION_JSON.toString());
        assertDescriptionContains(response, MediaType.APPLICATION_XML.toString());
    }

    @Test
    void handleGeneralException() {
        var response = testObject.handleGeneralException(new RuntimeException(GENERAL_EXCEPTION_MESSAGE), request);

        assertResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNAL_SERVER_ERROR);
        assertDescriptionContains(response, ERROR_MESSAGE_500);
    }

    private void assertResponse(ResponseEntity<?> response, HttpStatus expectedStatus, String expectedError) {
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatusCode());
        assertInstanceOf(ErrorDto.class, response.getBody());
        assertEquals(expectedError, ((ErrorDto) response.getBody()).error());
    }

    private void assertDescriptionContains(ResponseEntity<?> response, String fragment) {
        var dto = (ErrorDto) response.getBody();
        assertNotNull(dto);
        assertThat(dto.description()).contains(fragment);
    }
}
