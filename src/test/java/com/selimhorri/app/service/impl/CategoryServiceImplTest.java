package com.selimhorri.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImpl Unit Tests")
class CategoryServiceImplTest {
	
	@Mock
	private CategoryRepository categoryRepository;
	
	@InjectMocks
	private CategoryServiceImpl categoryService;
	
	private Category testCategory;
	private CategoryDto testCategoryDto;
	private Category parentCategory;
	
	@BeforeEach
	void setUp() {
		// Setup test data
		parentCategory = Category.builder()
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
				.parentCategoryDto(CategoryDto.builder()
					.categoryId(0)
					.categoryTitle("Root")
					.imageUrl("https://example.com/root.jpg")
					.build())
				.build();
	}
	
	@Test
	@DisplayName("Should find all categories successfully")
	void testFindAll_Success() {
		// Given
		List<Category> categories = Arrays.asList(testCategory);
		when(categoryRepository.findAll()).thenReturn(categories);
		
		// When
		List<CategoryDto> result = categoryService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Electronics", result.get(0).getCategoryTitle());
		verify(categoryRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should return empty list when no categories exist")
	void testFindAll_EmptyList() {
		// Given
		when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
		
		// When
		List<CategoryDto> result = categoryService.findAll();
		
		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(categoryRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should find category by id successfully")
	void testFindById_Success() {
		// Given
		when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
		
		// When
		CategoryDto result = categoryService.findById(1);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCategoryId());
		assertEquals("Electronics", result.getCategoryTitle());
		verify(categoryRepository, times(1)).findById(1);
	}
	
	@Test
	@DisplayName("Should throw CategoryNotFoundException when category not found")
	void testFindById_NotFound() {
		// Given
		when(categoryRepository.findById(999)).thenReturn(Optional.empty());
		
		// When & Then
		CategoryNotFoundException exception = assertThrows(
				CategoryNotFoundException.class,
				() -> categoryService.findById(999)
		);
		
		assertTrue(exception.getMessage().contains("Category with id: 999 not found"));
		verify(categoryRepository, times(1)).findById(999);
	}
	
	@Test
	@DisplayName("Should save category successfully")
	void testSave_Success() {
		// Given
		CategoryDto newCategoryDto = CategoryDto.builder()
				.categoryTitle("Clothing")
				.imageUrl("https://example.com/clothing.jpg")
				.build();
		
		Category savedCategory = Category.builder()
				.categoryId(2)
				.categoryTitle("Clothing")
				.imageUrl("https://example.com/clothing.jpg")
				.parentCategory(parentCategory)
				.build();
		
		when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
		
		// When
		CategoryDto result = categoryService.save(newCategoryDto);
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.getCategoryId());
		assertEquals("Clothing", result.getCategoryTitle());
		verify(categoryRepository, times(1)).save(any(Category.class));
	}
	
	@Test
	@DisplayName("Should update category successfully")
	void testUpdate_Success() {
		// Given
		CategoryDto updatedCategoryDto = CategoryDto.builder()
				.categoryId(1)
				.categoryTitle("Updated Electronics")
				.imageUrl("https://example.com/electronics-updated.jpg")
				.build();
		
		Category updatedCategory = Category.builder()
				.categoryId(1)
				.categoryTitle("Updated Electronics")
				.imageUrl("https://example.com/electronics-updated.jpg")
				.parentCategory(parentCategory)
				.build();
		
		when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
		
		// When
		CategoryDto result = categoryService.update(updatedCategoryDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCategoryId());
		assertEquals("Updated Electronics", result.getCategoryTitle());
		verify(categoryRepository, times(1)).save(any(Category.class));
	}
	
	@Test
	@DisplayName("Should update category by id successfully")
	void testUpdateById_Success() {
		// Given
		when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
		
		Category updatedCategory = Category.builder()
				.categoryId(1)
				.categoryTitle("Updated Electronics")
				.imageUrl("https://example.com/electronics-updated.jpg")
				.parentCategory(parentCategory)
				.build();
		
		when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
		
		// When
		CategoryDto result = categoryService.update(1, testCategoryDto);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.getCategoryId());
		verify(categoryRepository, times(1)).findById(1);
		verify(categoryRepository, times(1)).save(any(Category.class));
	}
	
	@Test
	@DisplayName("Should delete category by id successfully")
	void testDeleteById_Success() {
		// Given
		// No mocking needed - deleteById doesn't return value
		
		// When
		categoryService.deleteById(1);
		
		// Then
		verify(categoryRepository, times(1)).deleteById(1);
	}
	
	@Test
	@DisplayName("Should handle multiple categories and return distinct list")
	void testFindAll_MultipleCategories() {
		// Given
		Category category2 = Category.builder()
				.categoryId(2)
				.categoryTitle("Clothing")
				.imageUrl("https://example.com/clothing.jpg")
				.parentCategory(parentCategory)
				.build();
		
		List<Category> categories = Arrays.asList(testCategory, category2);
		when(categoryRepository.findAll()).thenReturn(categories);
		
		// When
		List<CategoryDto> result = categoryService.findAll();
		
		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Electronics", result.get(0).getCategoryTitle());
		assertEquals("Clothing", result.get(1).getCategoryTitle());
		verify(categoryRepository, times(1)).findAll();
	}
	
	@Test
	@DisplayName("Should handle categories with null parent category")
	void testSave_CategoryWithNullParent() {
		// Given
		CategoryDto categoryWithoutParent = CategoryDto.builder()
				.categoryTitle("Root Category")
				.imageUrl("https://example.com/root.jpg")
				.parentCategoryDto(null)
				.build();
		
		Category savedCategory = Category.builder()
				.categoryId(3)
				.categoryTitle("Root Category")
				.imageUrl("https://example.com/root.jpg")
				.parentCategory(null)
				.build();
		
		when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
		
		// When
		CategoryDto result = categoryService.save(categoryWithoutParent);
		
		// Then
		assertNotNull(result);
		assertEquals(3, result.getCategoryId());
		assertEquals("Root Category", result.getCategoryTitle());
		verify(categoryRepository, times(1)).save(any(Category.class));
	}
	
}

