package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.repository.ProductRepository;

/**
 * Pruebas de Integración para ProductService
 * Estas pruebas usan la base de datos real (H2 en memoria)
 * y prueban la integración completa entre capas
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Product Service Integration Tests")
class ProductServiceIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private Category testCategory;
	private CategoryDto testCategoryDto;
	
	@BeforeEach
	void setUp() {
		// Clean database before each test
		productRepository.deleteAll();
		categoryRepository.deleteAll();
		
		// Create reserved categories first (required by the service)
		Category deletedCategory = Category.builder()
				.categoryTitle("Deleted")
				.imageUrl("https://example.com/deleted.jpg")
				.build();
		categoryRepository.save(deletedCategory);
		
		Category noCategory = Category.builder()
				.categoryTitle("No category")
				.imageUrl("https://example.com/nocategory.jpg")
				.build();
		categoryRepository.save(noCategory);
		
		// Create test category
		testCategory = Category.builder()
				.categoryId(1)
				.categoryTitle("Electronics")
				.imageUrl("https://example.com/electronics.jpg")
				.build();
		
		testCategory = categoryRepository.save(testCategory);
		
		testCategoryDto = CategoryDto.builder()
				.categoryId(testCategory.getCategoryId())
				.categoryTitle(testCategory.getCategoryTitle())
				.imageUrl(testCategory.getImageUrl())
				.build();
	}
	
	@Test
	@DisplayName("Should create product successfully via REST API")
	void testCreateProduct_Success() throws Exception {
		// Given
		ProductDto productDto = ProductDto.builder()
				.productTitle("Integration Test Laptop")
				.imageUrl("https://example.com/laptop.jpg")
				.sku("INT-TEST-001")
				.priceUnit(1299.99)
				.quantity(10)
				.categoryDto(testCategoryDto)
				.build();
		
		// When & Then
		mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(productDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.productTitle").value("Integration Test Laptop"))
				.andExpect(jsonPath("$.sku").value("INT-TEST-001"))
				.andExpect(jsonPath("$.priceUnit").value(1299.99))
				.andExpect(jsonPath("$.quantity").value(10));
		
		// Verify it was saved in database
		assertTrue(productRepository.count() > 0);
	}
	
	@Test
	@DisplayName("Should retrieve product by id via REST API")
	void testGetProductById_Success() throws Exception {
		// Given
		Product savedProduct = createProductInDatabase();
		
		// When & Then
		mockMvc.perform(get("/api/products/" + savedProduct.getProductId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId").value(savedProduct.getProductId()))
				.andExpect(jsonPath("$.productTitle").value("Test Product"))
				.andExpect(jsonPath("$.sku").exists());
	}
	
	@Test
	@DisplayName("Should retrieve all products via REST API")
	void testGetAllProducts_Success() throws Exception {
		// Given
		createProductInDatabase();
		createProductInDatabase();
		
		// When & Then
		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isArray())
				.andExpect(jsonPath("$.collection.length()").value(2));
	}
	
	@Test
	@DisplayName("Should update product successfully via REST API")
	void testUpdateProduct_Success() throws Exception {
		// Given
		Product savedProduct = createProductInDatabase();
		
		ProductDto updatedProductDto = ProductDto.builder()
				.productId(savedProduct.getProductId())
				.productTitle("Updated Product Title")
				.imageUrl("https://example.com/updated.jpg")
				.sku(savedProduct.getSku())
				.priceUnit(999.99)
				.quantity(5)
				.categoryDto(testCategoryDto)
				.build();
		
		// When & Then
		mockMvc.perform(put("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedProductDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.productTitle").value("Updated Product Title"))
				.andExpect(jsonPath("$.priceUnit").value(999.99))
				.andExpect(jsonPath("$.quantity").value(5));
	}
	
	@Test
	@DisplayName("Should update product by id via REST API")
	void testUpdateProductById_Success() throws Exception {
		// Given
		Product savedProduct = createProductInDatabase();
		
		ProductDto updatedProductDto = ProductDto.builder()
				.productTitle("Updated Via ID")
				.imageUrl("https://example.com/updated-id.jpg")
				.sku("UPDATED-SKU-001")
				.priceUnit(1999.99)
				.quantity(15)
				.categoryDto(testCategoryDto)
				.build();
		
		// When & Then
		mockMvc.perform(put("/api/products/" + savedProduct.getProductId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedProductDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.productId").value(savedProduct.getProductId()));
	}
	
	@Test
	@DisplayName("Should delete product successfully via REST API")
	void testDeleteProduct_Success() throws Exception {
		// Given
		Product savedProduct = createProductInDatabase();
		Long productId = Long.valueOf(savedProduct.getProductId());
		
		// When & Then
		mockMvc.perform(delete("/api/products/" + productId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(true));
		
		// Verify it was soft deleted (not found in findAllWithoutDeleted)
		assertTrue(productRepository.findByIdWithoutDeleted(savedProduct.getProductId()).isEmpty());
	}
	
	@Test
	@DisplayName("Should return 400 error when product not found")
	void testGetProductById_NotFound() throws Exception {
		// When & Then
		mockMvc.perform(get("/api/products/999999"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should persist product with category relationship")
	void testProductWithCategoryRelationship() throws Exception {
		// Given
		ProductDto productDto = ProductDto.builder()
				.productTitle("Product with Category")
				.imageUrl("https://example.com/product.jpg")
				.sku("CAT-PROD-001")
				.priceUnit(299.99)
				.quantity(20)
				.categoryDto(testCategoryDto)
				.build();
		
		// When
		String response = mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(productDto)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		
		ProductDto result = objectMapper.readValue(response, ProductDto.class);
		
		// Then - Verify relationship in database
		Product dbProduct = productRepository.findById(result.getProductId()).orElseThrow();
		assertNotNull(dbProduct.getCategory());
		assertEquals(testCategory.getCategoryId(), dbProduct.getCategory().getCategoryId());
		assertEquals("Electronics", dbProduct.getCategory().getCategoryTitle());
	}
	
	@Test
	@DisplayName("Should validate product uniqueness by SKU")
	void testProductSKUUniqueness() throws Exception {
		// Given - Create first product
		ProductDto productDto1 = ProductDto.builder()
				.productTitle("Product 1")
				.imageUrl("https://example.com/product1.jpg")
				.sku("UNIQUE-SKU-001")
				.priceUnit(100.0)
				.quantity(10)
				.categoryDto(testCategoryDto)
				.build();
		
		mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(productDto1)))
				.andExpect(status().isOk());
		
		// When & Then - Try to create duplicate SKU
		ProductDto productDto2 = ProductDto.builder()
				.productTitle("Product 2")
				.imageUrl("https://example.com/product2.jpg")
				.sku("UNIQUE-SKU-001") // Same SKU
				.priceUnit(200.0)
				.quantity(20)
				.categoryDto(testCategoryDto)
				.build();
		
		// This will fail due to unique constraint on SKU
		try {
			mockMvc.perform(post("/api/products")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(productDto2)))
					.andExpect(status().is5xxServerError());
		} catch (Exception e) {
			// Expected - duplicate key exception
			assertTrue(true);
		}
	}
	
	@Test
	@DisplayName("Should retrieve products with category information")
	void testGetProductsWithCategory() throws Exception {
		// Given
		createProductInDatabase();
		
		// When & Then
		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection[0].category").exists())
				.andExpect(jsonPath("$.collection[0].category.categoryId").exists())
				.andExpect(jsonPath("$.collection[0].category.categoryTitle").value("Electronics"));
	}
	
	/**
	 * Helper method to create a product in the database
	 */
	private Product createProductInDatabase() {
		// Generate unique SKU to avoid constraint violations
		String uniqueSku = "TEST-SKU-" + System.currentTimeMillis();
		
		Product product = Product.builder()
				.productTitle("Test Product")
				.imageUrl("https://example.com/test.jpg")
				.sku(uniqueSku)
				.priceUnit(199.99)
				.quantity(15)
				.category(testCategory)
				.build();
		
		return productRepository.save(product);
	}
	
}

