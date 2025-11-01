package com.selimhorri.app.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.selimhorri.app.exception.payload.ExceptionMsg;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiExceptionHandler Unit Tests")
class ApiExceptionHandlerTest {
	
	@InjectMocks
	private ApiExceptionHandler apiExceptionHandler;
	
	@Test
	@DisplayName("Should handle ProductNotFoundException correctly")
	void testHandleProductNotFoundException() {
		// Given
		ProductNotFoundException exception = new ProductNotFoundException("Product with id: 1 not found");
		
		// When
		ResponseEntity<ExceptionMsg> response = apiExceptionHandler.handleApiRequestException(exception);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getMsg().contains("Product with id: 1 not found"));
		assertTrue(response.getBody().getMsg().startsWith("####"));
		assertTrue(response.getBody().getMsg().endsWith("####"));
		assertNotNull(response.getBody().getTimestamp());
	}
	
	@Test
	@DisplayName("Should handle CategoryNotFoundException correctly")
	void testHandleCategoryNotFoundException() {
		// Given
		CategoryNotFoundException exception = new CategoryNotFoundException("Category with id: 5 not found");
		
		// When
		ResponseEntity<ExceptionMsg> response = apiExceptionHandler.handleApiRequestException(exception);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getMsg().contains("Category with id: 5 not found"));
		assertTrue(response.getBody().getMsg().startsWith("####"));
		assertTrue(response.getBody().getMsg().endsWith("####"));
		assertNotNull(response.getBody().getTimestamp());
	}
	
	@Test
	@DisplayName("Should handle ProductNotFoundException with custom message")
	void testHandleProductNotFoundException_CustomMessage() {
		// Given
		ProductNotFoundException exception = new ProductNotFoundException("Invalid product SKU: ABC123");
		
		// When
		ResponseEntity<ExceptionMsg> response = apiExceptionHandler.handleApiRequestException(exception);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getMsg().contains("Invalid product SKU: ABC123"));
	}
	
	@Test
	@DisplayName("Should handle CategoryNotFoundException with custom message")
	void testHandleCategoryNotFoundException_CustomMessage() {
		// Given
		CategoryNotFoundException exception = new CategoryNotFoundException("Category name conflict detected");
		
		// When
		ResponseEntity<ExceptionMsg> response = apiExceptionHandler.handleApiRequestException(exception);
		
		// Then
		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getMsg().contains("Category name conflict detected"));
	}
	
	@Test
	@DisplayName("Should set correct HTTP status as BAD_REQUEST")
	void testHandleApiRequestException_HttpStatus() {
		// Given
		ProductNotFoundException exception = new ProductNotFoundException("Test error");
		
		// When
		ResponseEntity<ExceptionMsg> response = apiExceptionHandler.handleApiRequestException(exception);
		
		// Then
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getHttpStatus());
	}
	
	@Test
	@DisplayName("Should include timestamp in exception message")
	void testHandleApiRequestException_Timestamp() {
		// Given
		ProductNotFoundException exception = new ProductNotFoundException("Timestamp test");
		
		// When
		ResponseEntity<ExceptionMsg> response = apiExceptionHandler.handleApiRequestException(exception);
		
		// Then
		assertNotNull(response.getBody().getTimestamp());
	}
	
	@Test
	@DisplayName("Should format exception message with markers")
	void testHandleApiRequestException_MessageFormat() {
		// Given
		ProductNotFoundException exception = new ProductNotFoundException("Error message");
		
		// When
		ResponseEntity<ExceptionMsg> response = apiExceptionHandler.handleApiRequestException(exception);
		
		// Then
		String message = response.getBody().getMsg();
		assertEquals("#### Error message! ####", message);
	}
	
}

