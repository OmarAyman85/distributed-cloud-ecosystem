package com.ayman.distributed.simuclothing.service;

import com.ayman.distributed.simuclothing.model.Product;
import com.ayman.distributed.simuclothing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Page<Product> searchProducts(
            String category, String brand, String size, String color,
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable
    ) {
        Specification<Product> spec = Specification.where(null);

        if (StringUtils.hasText(category)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (StringUtils.hasText(brand)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("brand"), brand));
        }
        if (StringUtils.hasText(size)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("size"), size));
        }
        if (StringUtils.hasText(color)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("color"), color));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        return productRepository.findAll(spec, pageable);
    }
}
