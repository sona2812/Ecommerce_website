package com.example.demo.frontend;

import com.example.demo.model.User;
import com.example.demo.service.ProductService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import java.time.LocalDateTime;

/**
 * Singleton class for managing user session information across screens
 */
public class SessionManager {
    private static SessionManager instance;
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    
    private User currentUser;
    private Cart cart;
    private ProductService productService;
    private OrderService orderService;
    private UserService userService;
    private LocalDateTime lastActivityTime;
    private String sessionToken;
    
    private SessionManager() {
        // Private constructor to prevent instantiation
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public User getCurrentUser() {
        checkSessionValidity();
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            this.lastActivityTime = LocalDateTime.now();
            this.sessionToken = generateSessionToken();
        } else {
            this.lastActivityTime = null;
            this.sessionToken = null;
        }
    }
    
    private String generateSessionToken() {
        return java.util.UUID.randomUUID().toString();
    }
    
    private void checkSessionValidity() {
        if (lastActivityTime != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(lastActivityTime.plusMinutes(SESSION_TIMEOUT_MINUTES))) {
                clearSession();
                throw new SessionExpiredException("Your session has expired. Please login again.");
            }
            // Update last activity time
            lastActivityTime = now;
        }
    }
    
    public Cart getCart() {
        checkSessionValidity();
        return cart;
    }
    
    public void setCart(Cart cart) {
        checkSessionValidity();
        this.cart = cart;
    }
    
    public ProductService getProductService() {
        return productService;
    }
    
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    
    public OrderService getOrderService() {
        return orderService;
    }
    
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
    
    public UserService getUserService() {
        return userService;
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    public void clearSession() {
        currentUser = null;
        cart = null;
        lastActivityTime = null;
        sessionToken = null;
        // Don't clear services as they're shared resources
    }

    public boolean isLoggedIn() {
        try {
            checkSessionValidity();
            return currentUser != null;
        } catch (SessionExpiredException e) {
            return false;
        }
    }

    public boolean isAdmin() {
        try {
            checkSessionValidity();
            return isLoggedIn() && currentUser.getRole().toString().equals("ADMIN");
        } catch (SessionExpiredException e) {
            return false;
        }
    }
    
    public String getSessionToken() {
        checkSessionValidity();
        return sessionToken;
    }
    
    public static class SessionExpiredException extends RuntimeException {
        public SessionExpiredException(String message) {
            super(message);
        }
    }
} 