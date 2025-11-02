package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Unit Tests")
class ProductServiceImplTest {
	
	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	@InjectMocks
	private ProductServiceImpl productService;
	
	private Product testProduct;
	private ProductDto testProductDto;
	private Category testCategory;
	private CategoryDto testCategoryDto;
	
	@BeforeEach
	void setUp() {
		// Setup test data
		testCategory = Category.builder()
				.categoryId(1)
				.categoryTitle("Electronics")
				.imageUrl("https://example.com/electronics.jpg")
				.build();
		
		testCategoryDto = CategoryDto.builder()
				.categoryId(1)
				.categoryTitle("Electronics")
				.imageUrl("https://example.com/electronics.jpg")
				.build();
		
		testProduct = Product.builder()
				.productId(1)
				.productTitle("Laptop ASUS")
				.imageUrl("https://example.com/laptop.jpg")
				.sku("LAP-ASUS-001")
				.priceUnit(1299.99)
				.quantity(50)
				.category(testCategory)
				.build();
		
		testProductDto = ProductDto.builder()
				.productId(1)
				.productTitle("Laptop ASUS")
				.imageUrl("https://example.com/laptop.jpg")
				.sku("LAP-ASUS-001")
				.priceUnit(1299.99)
				.quantity(50)
				.categoryDto(testCategoryDto)
				.build();
	}
	
	@Test
	@DisplayName("Should find all products successfully")
	void testFindAll_Success() {
		// Given
		List<Product> products = Arrays.asList(testProduct);
		when(productRepository.findAllWithoutDeleted()).thenReturn(products);
		
		// When
		List<ProductDto> result = productService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Laptop ASUS", result.get(0).getProductTitle());
		assertEquals(1299.99, result.get(0).getPriceUnit());
		verify(productRepository, times(1)).findAllWithoutDeleted();
	}
	
	@Test
	@DisplayName("Should return empty list when no products exist")
	void testFindAll_EmptyList() {
		// Given
		when(productRepository.findAllWithoutDeleted()).thenReturn(Collections.emptyList());
		
		// When
		List<ProductDto> result = productService.findAll();
		
		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(productRepository, times(1)).findAllWithoutDeleted();
	}
	
	@Test
	@DisplayName("Should find product by id successfully")
	void testFindById_Success() {
		// Given
		when(productRepository.findByIdWithoutDeleted(1)).thenReturn(Optional.of(testProduct));
		
		// When
		ProductDto result = productService.findById(1);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getProductId());
		assertEquals("Laptop ASUS", result.getProductTitle());
		assertEquals("LAP-ASUS-001", result.getSku());
		verify(productRepository, times(1)).findByIdWithoutDeleted(1);
	}
	
	@Test
	@DisplayName("Should throw ProductNotFoundException when product not found")
	void testFindById_NotFound() {
		// Given
		when(productRepository.findByIdWithoutDeleted(999)).thenReturn(Optional.empty());
		
		// When & Then
		ProductNotFoundException exception = assertThrows(
				ProductNotFoundException.class,
				() -> productService.findById(999)
		);
		
		assertTrue(exception.getMessage().contains("Product with id: 999 not found"));
		verify(productRepository, times(1)).findByIdWithoutDeleted(999);
	}
	
	@Test
	@DisplayName("Should save product successfully")
	void testSave_Success() {
		// Given
		ProductDto newProductDto = ProductDto.builder()
				.productTitle("New Product")
				.imageUrl("https://example.com/new.jpg")
				.sku("NEW-001")
				.priceUnit(99.99)
				.quantity(10)
				.categoryDto(testCategoryDto)
				.build();
		
		Product savedProduct = Product.builder()
				.productId(2)
				.productTitle("New Product")
				.imageUrl("https://example.com/new.jpg")
				.sku("NEW-001")
				.priceUnit(99.99)
				.quantity(10)
				.category(testCategory)
				.build();
		
		when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
		when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
		
		// When
		ProductDto result = productService.save(newProductDto);
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.getProductId());
		assertEquals("New Product", result.getProductTitle());
		assertEquals("NEW-001", result.getSku());
		verify(categoryRepository, times(1)).findById(1);
		verify(productRepository, times(1)).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Should update product successfully")
	void testUpdate_Success() {
		// Given
		ProductDto updatedProductDto = ProductDto.builder()
				.productId(1)
				.productTitle("Updated Laptop ASUS")
				.imageUrl("https://example.com/laptop-updated.jpg")
				.sku("LAP-ASUS-001")
				.priceUnit(1199.99)
				.quantity(40)
				.categoryDto(testCategoryDto)
				.build();
		
		Product updatedProduct = Product.builder()
				.productId(1)
				.productTitle("Updated Laptop ASUS")
				.imageUrl("https://example.com/laptop-updated.jpg")
				.sku("LAP-ASUS-001")
				.priceUnit(1199.99)
				.quantity(40)
				.category(testCategory)
				.build();
		
		when(productRepository.existsById(1)).thenReturn(true);
		when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
		
		// When
		ProductDto result = productService.update(updatedProductDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getProductId());
		assertEquals("Updated Laptop ASUS", result.getProductTitle());
		assertEquals(1199.99, result.getPriceUnit());
		verify(productRepository, times(1)).existsById(1);
		verify(productRepository, times(1)).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Should update product by id successfully")
	void testUpdateById_Success() {
		// Given
		when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
		// Mock categoryRepository.findById() para cargar la categoría completa
		when(categoryRepository.findById(testCategory.getCategoryId())).thenReturn(Optional.of(testCategory));
		
		Product updatedProduct = Product.builder()
				.productId(1)
				.productTitle("Updated Laptop ASUS")
				.imageUrl("https://example.com/laptop-updated.jpg")
				.sku("LAP-ASUS-001")
				.priceUnit(1199.99)
				.quantity(40)
				.category(testCategory)
				.build();
		
		when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
		
		// When
		ProductDto result = productService.update(1, testProductDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getProductId());
		verify(productRepository, times(1)).findById(1);
		verify(categoryRepository, times(1)).findById(testCategory.getCategoryId());
		verify(productRepository, times(1)).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Should delete product by id successfully")
	void testDeleteById_Success() {
		// Given
		Category deletedCategory = Category.builder()
				.categoryId(999)
				.categoryTitle("Deleted")
				.build();
		when(productRepository.findByIdWithoutDeleted(1)).thenReturn(Optional.of(testProduct));
		when(categoryRepository.findByCategoryTitle("Deleted")).thenReturn(Optional.of(deletedCategory));
		when(productRepository.save(any(Product.class))).thenReturn(testProduct);
		
		// When
		productService.deleteById(1);
		
		// Then
		verify(productRepository, times(1)).findByIdWithoutDeleted(1);
		verify(categoryRepository, times(1)).findByCategoryTitle("Deleted");
		verify(productRepository, times(1)).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Should throw ProductNotFoundException when trying to delete non-existent product")
	void testDeleteById_NotFound() {
		// Given
		when(productRepository.findByIdWithoutDeleted(999)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(
				ProductNotFoundException.class,
				() -> productService.deleteById(999)
		);
		
		verify(productRepository, times(1)).findByIdWithoutDeleted(999);
		verify(productRepository, never()).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Should handle multiple products and return distinct list")
	void testFindAll_MultipleProducts() {
		// Given
		Product product2 = Product.builder()
				.productId(2)
				.productTitle("Smartphone")
				.imageUrl("https://example.com/phone.jpg")
				.sku("PHN-001")
				.priceUnit(699.99)
				.quantity(30)
				.category(testCategory)
				.build();
		
		List<Product> products = Arrays.asList(testProduct, product2);
		when(productRepository.findAllWithoutDeleted()).thenReturn(products);
		
		// When
		List<ProductDto> result = productService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Laptop ASUS", result.get(0).getProductTitle());
		assertEquals("Smartphone", result.get(1).getProductTitle());
		verify(productRepository, times(1)).findAllWithoutDeleted();
	}
	
}

