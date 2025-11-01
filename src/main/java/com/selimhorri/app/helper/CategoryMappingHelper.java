package com.selimhorri.app.helper;

import java.util.Optional;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.dto.CategoryDto;

public interface CategoryMappingHelper {
	
	public static CategoryDto map(final Category category) {
		
		final var parentCategory = Optional.ofNullable(category
				.getParentCategory()).orElseGet(() -> new Category());
		
		return CategoryDto.builder()
				.categoryId(category.getCategoryId())
				.categoryTitle(category.getCategoryTitle())
				.imageUrl(category.getImageUrl())
				.parentCategoryDto(
						CategoryDto.builder()
							.categoryId(parentCategory.getCategoryId())
							.categoryTitle(parentCategory.getCategoryTitle())
							.imageUrl(parentCategory.getImageUrl())
							.build())
				.build();
	}
	
	public static Category map(final CategoryDto categoryDto) {
		
		Category.CategoryBuilder categoryBuilder = Category.builder()
				.categoryId(categoryDto.getCategoryId())
				.categoryTitle(categoryDto.getCategoryTitle())
				.imageUrl(categoryDto.getImageUrl());
		
		// Only set parentCategory if parentCategoryDto exists and has a valid ID
		if (categoryDto.getParentCategoryDto() != null && 
		    categoryDto.getParentCategoryDto().getCategoryId() != null) {
			categoryBuilder.parentCategory(
						Category.builder()
						.categoryId(categoryDto.getParentCategoryDto().getCategoryId())
						.categoryTitle(categoryDto.getParentCategoryDto().getCategoryTitle())
						.imageUrl(categoryDto.getParentCategoryDto().getImageUrl())
						.build());
		}
		
		return categoryBuilder.build();
	}
	
	
	
}










