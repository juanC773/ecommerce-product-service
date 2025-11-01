package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.repository.CategoryRepository;

/**
 * Pruebas de Integración para CategoryService
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Category Service Integration Tests")
class CategoryServiceIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		categoryRepository.deleteAll();
		
		// Create reserved categories (required by the service)
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
	}
	
	@Test
	@DisplayName("Should create category successfully via REST API")
	void testCreateCategory_Success() throws Exception {
		// Given
		CategoryDto categoryDto = CategoryDto.builder()
				.categoryTitle("Electronics")
				.imageUrl("https://example.com/electronics.jpg")
				.build();
		
		// When & Then
		mockMvc.perform(post("/api/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoryDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.categoryTitle").value("Electronics"))
				.andExpect(jsonPath("$.imageUrl").value("https://example.com/electronics.jpg"));
		
		// Verify it was saved
		assertTrue(categoryRepository.count() > 0);
	}
	
	@Test
	@DisplayName("Should retrieve category by id via REST API")
	void testGetCategoryById_Success() throws Exception {
		// Given
		Category savedCategory = createCategoryInDatabase();
		
		// When & Then
		mockMvc.perform(get("/api/categories/" + savedCategory.getCategoryId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.categoryId").value(savedCategory.getCategoryId()))
				.andExpect(jsonPath("$.categoryTitle").value("Test Category"));
	}
	
	@Test
	@DisplayName("Should retrieve all categories via REST API")
	void testGetAllCategories_Success() throws Exception {
		// Given
		createCategoryInDatabase();
		createCategoryInDatabase();
		
		// When & Then
		mockMvc.perform(get("/api/categories"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isArray())
				.andExpect(jsonPath("$.collection.length()").value(2));
	}
	
	@Test
	@DisplayName("Should update category successfully via REST API")
	void testUpdateCategory_Success() throws Exception {
		// Given
		Category savedCategory = createCategoryInDatabase();
		
		CategoryDto updatedCategoryDto = CategoryDto.builder()
				.categoryId(savedCategory.getCategoryId())
				.categoryTitle("Updated Electronics")
				.imageUrl("https://example.com/updated.jpg")
				.build();
		
		// When & Then
		mockMvc.perform(put("/api/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedCategoryDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.categoryTitle").value("Updated Electronics"))
				.andExpect(jsonPath("$.categoryId").value(savedCategory.getCategoryId()));
	}
	
	@Test
	@DisplayName("Should update category by id via REST API")
	void testUpdateCategoryById_Success() throws Exception {
		// Given
		Category savedCategory = createCategoryInDatabase();
		
		CategoryDto updatedCategoryDto = CategoryDto.builder()
				.categoryTitle("Updated Category")
				.imageUrl("https://example.com/updated-id.jpg")
				.build();
		
		// When & Then
		mockMvc.perform(put("/api/categories/" + savedCategory.getCategoryId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedCategoryDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.categoryId").value(savedCategory.getCategoryId()));
	}
	
	@Test
	@DisplayName("Should delete category successfully via REST API")
	void testDeleteCategory_Success() throws Exception {
		// Given
		Category savedCategory = createCategoryInDatabase();
		Integer categoryId = savedCategory.getCategoryId();
		
		// When & Then
		mockMvc.perform(delete("/api/categories/" + categoryId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(true));
		
		// Verify it was deleted
		assertTrue(categoryRepository.findById(categoryId).isEmpty());
	}
	
	@Test
	@DisplayName("Should return 400 error when category not found")
	void testGetCategoryById_NotFound() throws Exception {
		// When & Then
		mockMvc.perform(get("/api/categories/999999"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return empty collection when no categories exist")
	void testGetAllCategories_Empty() throws Exception {
		// When & Then
		mockMvc.perform(get("/api/categories"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.collection").isEmpty());
	}
	
	/**
	 * Helper method to create a category in the database
	 */
	private Category createCategoryInDatabase() {
		Category category = Category.builder()
				.categoryTitle("Test Category")
				.imageUrl("https://example.com/test.jpg")
				.build();
		
		return categoryRepository.save(category);
	}
	
}

