package com.ecommerce.backend.ProductManagement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    /**
     * Fetch all products grouped by category
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, List<ProductEntity>>> getAllProductsGroupedByCategory() {
        List<ProductEntity> products = productService.getAllProducts();
        
        // Grouping products by category name
        Map<String, List<ProductEntity>> groupedProducts = products.stream()
            .collect(Collectors.groupingBy(ProductEntity::getCategory));

        return ResponseEntity.ok(groupedProducts);
    }

    /**
     * Fetch products of a specific category
     */
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<ProductEntity>> getProductsByCategory(@PathVariable String categoryName) {
        List<ProductEntity> products = productService.getProductsByCategory(categoryName);
        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(products);
        }
        return ResponseEntity.ok(products);
    }

    /**
     * Admin adds a product to a specific category
     */
    @PostMapping("/add/{category}")
    @PreAuthorize("@customSecurity.hasRole(authentication, 'ADMIN')")
    public ResponseEntity<ProductEntity> addProduct(@PathVariable String category, @RequestBody @Valid ProductEntity product) {
        product.setCategory(category); // Assign category dynamically
        return ResponseEntity.ok(productService.addProduct(product));
    }

    /**
     * Admin updates a product
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("@customSecurity.hasRole(authentication, 'ADMIN')")
    public ResponseEntity<ProductEntity> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductEntity product) {
        ProductEntity existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        existingProduct.setUrl(product.getUrl());
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory()); // Update category

        return ResponseEntity.ok(productService.updateProduct(existingProduct));
    }

    /**
     * Admin deletes a product
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@customSecurity.hasRole(authentication, 'ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

