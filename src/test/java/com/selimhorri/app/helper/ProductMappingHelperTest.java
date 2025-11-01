package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;

@DisplayName("ProductMappingHelper Unit Tests")
class ProductMappingHelperTest {
	
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
	@DisplayName("Should map Product to ProductDto successfully")
	void testMapProductToDto_Success() {
		// When
		ProductDto result = ProductMappingHelper.map(testProduct);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getProductId());
		assertEquals("Laptop ASUS", result.getProductTitle());
		assertEquals("https://example.com/laptop.jpg", result.getImageUrl());
		assertEquals("LAP-ASUS-001", result.getSku());
		assertEquals(1299.99, result.getPriceUnit());
		assertEquals(50, result.getQuantity());
		assertNotNull(result.getCategoryDto());
		assertEquals(1, result.getCategoryDto().getCategoryId());
		assertEquals("Electronics", result.getCategoryDto().getCategoryTitle());
	}
	
	@Test
	@DisplayName("Should map ProductDto to Product successfully")
	void testMapDtoToProduct_Success() {
		// When
		Product result = ProductMappingHelper.map(testProductDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getProductId());
		assertEquals("Laptop ASUS", result.getProductTitle());
		assertEquals("https://example.com/laptop.jpg", result.getImageUrl());
		assertEquals("LAP-ASUS-001", result.getSku());
		assertEquals(1299.99, result.getPriceUnit());
		assertEquals(50, result.getQuantity());
		assertNotNull(result.getCategory());
		assertEquals(1, result.getCategory().getCategoryId());
		assertEquals("Electronics", result.getCategory().getCategoryTitle());
	}
	
	@Test
	@DisplayName("Should map Product with zero values correctly")
	void testMapProductWithZeroValues() {
		// Given
		Product productWithZeros = Product.builder()
				.productId(2)
				.productTitle("Free Product")
				.imageUrl("https://example.com/free.jpg")
				.sku("FREE-001")
				.priceUnit(0.0)
				.quantity(0)
				.category(testCategory)
				.build();
		
		// When
		ProductDto result = ProductMappingHelper.map(productWithZeros);
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.getProductId());
		assertEquals(0.0, result.getPriceUnit());
		assertEquals(0, result.getQuantity());
	}
	
	@Test
	@DisplayName("Should maintain bidirectional mapping consistency")
	void testBidirectionalMapping_Consistency() {
		// When - Product to DTO and back
		ProductDto mappedDto = ProductMappingHelper.map(testProduct);
		Product mappedBackProduct = ProductMappingHelper.map(mappedDto);
		
		// Then
		assertEquals(testProduct.getProductId(), mappedBackProduct.getProductId());
		assertEquals(testProduct.getProductTitle(), mappedBackProduct.getProductTitle());
		assertEquals(testProduct.getImageUrl(), mappedBackProduct.getImageUrl());
		assertEquals(testProduct.getSku(), mappedBackProduct.getSku());
		assertEquals(testProduct.getPriceUnit(), mappedBackProduct.getPriceUnit());
		assertEquals(testProduct.getQuantity(), mappedBackProduct.getQuantity());
		assertEquals(testProduct.getCategory().getCategoryId(), mappedBackProduct.getCategory().getCategoryId());
	}
	
	@Test
	@DisplayName("Should map ProductDto with decimal price correctly")
	void testMapDtoWithDecimalPrice() {
		// Given
		ProductDto productDtoWithDecimal = ProductDto.builder()
				.productTitle("Expensive Item")
				.sku("EXP-001")
				.priceUnit(9999.99)
				.quantity(1)
				.categoryDto(testCategoryDto)
				.build();
		
		// When
		Product result = ProductMappingHelper.map(productDtoWithDecimal);
		
		// Then
		assertNotNull(result);
		assertEquals(9999.99, result.getPriceUnit());
		assertEquals(1, result.getQuantity());
	}
	
}

