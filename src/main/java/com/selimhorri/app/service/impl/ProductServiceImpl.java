package com.selimhorri.app.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.CategoryNotFoundException;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.helper.ProductMappingHelper;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public List<ProductDto> findAll() {
		log.info("*** ProductDto List, service; fetch all products *");
		return this.productRepository.findAllWithoutDeleted()
				.stream()
				.map(ProductMappingHelper::map)
				.distinct()
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public ProductDto findById(final Integer productId) {
		log.info("*** ProductDto, service; fetch product by id *");
		return this.productRepository.findByIdWithoutDeleted(productId)
				.map(ProductMappingHelper::map)
				.orElseThrow(
						() -> new ProductNotFoundException(String.format("Product with id: %d not found", productId)));
	}

	@Override
	public ProductDto save(final ProductDto productDto) {
		log.info("*** ProductDto, service; save product *");

		// Validación de campos obligatorios
		if (productDto.getProductTitle() == null || productDto.getProductTitle().isEmpty()) {
			throw new IllegalArgumentException("El título del producto es requerido");
		}

		if (productDto.getImageUrl() == null || productDto.getImageUrl().isEmpty()) {
			throw new IllegalArgumentException("La URL de la imagen es requerida");
		}

		if (productDto.getSku() == null || productDto.getSku().isEmpty()) {
			throw new IllegalArgumentException("El SKU es requerido");
		}

		if (productDto.getPriceUnit() == null) {
			throw new IllegalArgumentException("El precio unitario es requerido");
		}

		if (productDto.getQuantity() == null) {
			throw new IllegalArgumentException("La cantidad es requerida");
		}

		if (productDto.getCategoryDto() == null || productDto.getCategoryDto().getCategoryId() == null) {
			throw new IllegalArgumentException("La categoría es requerida");
		}

		// Validar que la categoría exista (usando Integer como ID)
		Integer categoryId = productDto.getCategoryDto().getCategoryId();
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada con ID: " + categoryId));

		productDto.setProductId(null);
		
		// Mapear DTO a entidad
		Product newProduct = ProductMappingHelper.map(productDto);
		
		// Asegurar que la categoría esté correctamente asignada
		newProduct.setCategory(category);
		
		// Setear createdAt manualmente si JPA Auditing no está funcionando
		if (newProduct.getCreatedAt() == null) {
			newProduct.setCreatedAt(Instant.now());
		}

		return ProductMappingHelper.map(this.productRepository.save(newProduct));
	}

	@Override
	public ProductDto update(final ProductDto productDto) {
		log.info("*** ProductDto, service; update product *");

		// Validar que el producto exista
		if (productDto.getProductId() == null || !productRepository.existsById(productDto.getProductId())) {
			throw new ProductNotFoundException("Producto no encontrado con ID: " + productDto.getProductId());
		}

		return ProductMappingHelper.map(this.productRepository
				.save(ProductMappingHelper.map(productDto)));
	}

	@Override
	public ProductDto update(final Integer productId, final ProductDto productDto) {
		log.info("*** ProductDto, service; update product with productId *");

		// Verificar que el producto exista y cargar la categoría completa
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Producto no encontrado con ID: " + productId));

		// Cargar la categoría completa desde el repositorio
		Integer categoryId = productDto.getCategoryDto() != null ? productDto.getCategoryDto().getCategoryId() : null;
		Category category = null;
		if (categoryId != null) {
			category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new CategoryNotFoundException("Categoría no encontrada con ID: " + categoryId));
		} else {
			// Si no se proporciona categoría, mantener la existente
			category = existingProduct.getCategory();
		}

		// Preservar createdAt del producto existente
		Instant originalCreatedAt = existingProduct.getCreatedAt();
		
		// Actualizar los campos del producto existente con los del DTO
		existingProduct.setProductTitle(productDto.getProductTitle());
		existingProduct.setImageUrl(productDto.getImageUrl());
		existingProduct.setSku(productDto.getSku());
		existingProduct.setPriceUnit(productDto.getPriceUnit());
		existingProduct.setQuantity(productDto.getQuantity());
		existingProduct.setCategory(category);
		
		// Preservar createdAt (no debe cambiar en un update)
		if (originalCreatedAt != null) {
			existingProduct.setCreatedAt(originalCreatedAt);
		} else {
			// Si por alguna razón no tiene createdAt, setearlo ahora
			existingProduct.setCreatedAt(Instant.now());
		}
		
		// Setear updatedAt manualmente si JPA Auditing no está funcionando
		existingProduct.setUpdatedAt(Instant.now());

		return ProductMappingHelper.map(this.productRepository.save(existingProduct));
	}

	@Override
	public void deleteById(final Integer productId) {
		log.info("*** Void, service; soft delete product by id *");

		// 1. Verificar si el producto existe
		Product product = this.productRepository.findByIdWithoutDeleted(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product with id: " + productId + " not found"));

		// 2. Buscar la categoría "Deleted"
		Category deletedCategory = this.categoryRepository.findByCategoryTitle("Deleted")
				.orElseThrow(() -> new RuntimeException("Category 'Deleted' not found in database"));

		// 3. Actualizar la categoría del producto a "Deleted" (soft delete)
		product.setCategory(deletedCategory);
		this.productRepository.save(product);
	}
}









