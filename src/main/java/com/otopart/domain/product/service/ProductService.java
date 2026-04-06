package com.otopart.domain.product.service;

import com.otopart.domain.product.entity.Product;
import com.otopart.domain.product.repository.ProductRepository;
import com.otopart.shared.enums.DeliveryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> search(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable);
    }

    public Page<Product> getByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
    }

    public Page<Product> getDiscounted(Pageable pageable) {
        return productRepository.findDiscountedProducts(pageable);
    }

    public Page<Product> getExpress(Pageable pageable) {
        return productRepository.findByExpressAvailableTrueAndActiveTrue(pageable);
    }

    public Page<Product> searchByTireSize(String tireSize, Pageable pageable) {
        return productRepository.findByTireSizeContainingIgnoreCaseAndActiveTrue(tireSize, pageable);
    }

    public Page<Product> getByDeliveryType(DeliveryType deliveryType, Pageable pageable) {
        return productRepository.findByDeliveryTypeAndActiveTrue(deliveryType, pageable);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));
    }

    public Product getBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));
    }

    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = getById(id);
        product.setActive(false);
        productRepository.save(product);
    }
}