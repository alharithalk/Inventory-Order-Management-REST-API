package com.Invetory.mangment.Inventory.Order.Management.service;

import com.Invetory.mangment.Inventory.Order.Management.entity.Category;
import com.Invetory.mangment.Inventory.Order.Management.entity.Product;
import com.Invetory.mangment.Inventory.Order.Management.repository.CategoryRepository;
import com.Invetory.mangment.Inventory.Order.Management.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product createProduct(String name, BigDecimal price, int stockQty, Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQty);
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product adjustStock(Long productId, int amount) {
        Product product = getProductById(productId);
        int newStock = product.getStockQuantity() + amount;
        if (newStock < 0) {
            throw new RuntimeException("Stock cannot go below 0");
        }
        product.setStockQuantity(newStock);
        return productRepository.save(product);
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
