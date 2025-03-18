package com.ecommerce.backend.OrderManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecommerce.backend.ProductManagement.ProductRepo;
import com.ecommerce.backend.UserData.UserRepo;

@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private OrderRepo orderRepository;

    @Autowired
    private UserRepo userRepository;
    

    //📌 1. API for Dashboard Statistics
   @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatus("Pending"));
        stats.put("totalRevenue", orderRepository.getTotalRevenue("Delivered"));
        return stats;
    }
    

    // 📌 2. API to Get Recent Orders (Last 5 Orders)
    @GetMapping("/recent-orders")
    public List<OrderEntity> getRecentOrders() {
        return orderRepository.findTop5ByOrderByCreatedAtDesc();
    }

}
