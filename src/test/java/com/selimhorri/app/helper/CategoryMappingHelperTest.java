package com.selimhorri.app.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.CategoryDto;

@DisplayName("CategoryMappingHelper Unit Tests")
class CategoryMappingHelperTest {
	
	private Category testCategory;
	private CategoryDto testCategoryDto;
	private Category parentCategory;
	private CategoryDto parentCategoryDto;
	
	@BeforeEach
	void setUp() {
		// Setup test data
		parentCategory = Category.builder()
				.categoryId(0)
				.categoryTitle("Root")
				.imageUrl("https://example.com/root.jpg")
				.build();
		
		parentCategoryDto = CategoryDto.builder()
				.categoryId(0)
				.categoryTitle("Root")
				.imageUrl("https://example.com/root.jpg")
				.build();
		
		testCategory = Category.builder()
				.categoryId(1)
				.categoryTitle("Electronics")
				.imageUrl("https://example.com/electronics.jpg")
				.parentCategory(parentCategory)
				.build();
		
		testCategoryDto = CategoryDto.builder()
				.categoryId(1)
				.categoryTitle("Electronics")
				.imageUrl("https://example.com/electronics.jpg")
				.parentCategoryDto(parentCategoryDto)
				.build();
	}
	
	@Test
	@DisplayName("Should map Category to CategoryDto successfully")
	void testMapCategoryToDto_Success() {
		// When
		CategoryDto result = CategoryMappingHelper.map(testCategory);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCategoryId());
		assertEquals("Electronics", result.getCategoryTitle());
		assertEquals("https://example.com/electronics.jpg", result.getImageUrl());
		assertNotNull(result.getParentCategoryDto());
		assertEquals(0, result.getParentCategoryDto().getCategoryId());
		assertEquals("Root", result.getParentCategoryDto().getCategoryTitle());
	}
	
	@Test
	@DisplayName("Should map CategoryDto to Category successfully")
	void testMapDtoToCategory_Success() {
		// When
		Category result = CategoryMappingHelper.map(testCategoryDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCategoryId());
		assertEquals("Electronics", result.getCategoryTitle());
		assertEquals("https://example.com/electronics.jpg", result.getImageUrl());
		assertNotNull(result.getParentCategory());
		assertEquals(0, result.getParentCategory().getCategoryId());
		assertEquals("Root", result.getParentCategory().getCategoryTitle());
	}
	
	@Test
	@DisplayName("Should handle null parent category gracefully")
	void testMapCategoryWithNullParent() {
		// Given
		Category categoryWithoutParent = Category.builder()
				.categoryId(2)
				.categoryTitle("Root Category")
				.imageUrl("https://example.com/root-cat.jpg")
				.parentCategory(null)
				.build();
		
		// When
		CategoryDto result = CategoryMappingHelper.map(categoryWithoutParent);
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.getCategoryId());
		assertEquals("Root Category", result.getCategoryTitle());
		// Helper creates empty Category object for null parent
		assertNotNull(result.getParentCategoryDto());
	}
	
	@Test
	@DisplayName("Should handle null parent category DTO gracefully")
	void testMapDtoWithNullParent() {
		// Given
		CategoryDto categoryDtoWithoutParent = CategoryDto.builder()
				.categoryId(3)
				.categoryTitle("Orphan Category")
				.imageUrl("https://example.com/orphan.jpg")
				.parentCategoryDto(null)
				.build();
		
		// When
		Category result = CategoryMappingHelper.map(categoryDtoWithoutParent);
		
		// Then
		assertNotNull(result);
		assertEquals(3, result.getCategoryId());
		assertEquals("Orphan Category", result.getCategoryTitle());
		// Helper returns null for null parent
		assertNull(result.getParentCategory());
	}
	
	@Test
	@DisplayName("Should maintain bidirectional mapping consistency")
	void testBidirectionalMapping_Consistency() {
		// When - Category to DTO and back
		CategoryDto mappedDto = CategoryMappingHelper.map(testCategory);
		Category mappedBackCategory = CategoryMappingHelper.map(mappedDto);
		
		// Then
		assertEquals(testCategory.getCategoryId(), mappedBackCategory.getCategoryId());
		assertEquals(testCategory.getCategoryTitle(), mappedBackCategory.getCategoryTitle());
		assertEquals(testCategory.getImageUrl(), mappedBackCategory.getImageUrl());
		assertEquals(testCategory.getParentCategory().getCategoryId(), mappedBackCategory.getParentCategory().getCategoryId());
	}
	
	@Test
	@DisplayName("Should map category with empty string values")
	void testMapCategoryWithEmptyStrings() {
		// Given
		Category categoryWithEmptyStrings = Category.builder()
				.categoryId(4)
				.categoryTitle("")
				.imageUrl("")
				.parentCategory(parentCategory)
				.build();
		
		// When
		CategoryDto result = CategoryMappingHelper.map(categoryWithEmptyStrings);
		
		// Then
		assertNotNull(result);
		assertEquals(4, result.getCategoryId());
		assertEquals("", result.getCategoryTitle());
		assertEquals("", result.getImageUrl());
	}
	
}

