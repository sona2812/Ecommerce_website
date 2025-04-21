package com.example.demo.frontend;

import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AdminDashboard {
    private Stage stage;
    private final ApplicationContext applicationContext;
    
    @Autowired
    public AdminDashboard(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.stage = null; // Will be set when show() is called
    }
    
    // Constructor for direct instantiation (used for navigation)
    public AdminDashboard(Stage stage) {
        this.stage = stage;
        this.applicationContext = null; // Not available when directly instantiated
    }

    // Method for use when injected by Spring
    public void show(Stage stage) {
        this.stage = stage;
        show();
    }

    public void show() {
        if (stage == null) {
            throw new IllegalStateException("Stage must be set before calling show()");
        }
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Button manageProductsBtn = new Button("Manage Products");
        Button manageUsersBtn = new Button("Manage Users");
        Button viewOrdersBtn = new Button("View Orders");
        Button logoutBtn = new Button("Logout");

        manageProductsBtn.setMaxWidth(Double.MAX_VALUE);
        manageUsersBtn.setMaxWidth(Double.MAX_VALUE);
        viewOrdersBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setMaxWidth(Double.MAX_VALUE);

        manageProductsBtn.setOnAction(e -> {
            try {
                ProductManagementView productManagementView;
                if (applicationContext != null) {
                    productManagementView = applicationContext.getBean(ProductManagementView.class);
                } else {
                    // Fallback: Get ProductService directly and create a new view
                    ProductService productService = SessionManager.getInstance().getProductService();
                    if (productService == null) {
                        throw new RuntimeException("Product service not available");
                    }
                    productManagementView = new ProductManagementView(productService);
                }
                productManagementView.show(stage);
            } catch (Exception ex) {
                System.err.println("Error showing product management: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        manageUsersBtn.setOnAction(e -> {
            try {
                UserManagementView userManagementView;
                if (applicationContext != null) {
                    userManagementView = applicationContext.getBean(UserManagementView.class);
                } else {
                    // Fallback: Get UserService directly and create a new view
                    UserService userService = SessionManager.getInstance().getUserService();
                    if (userService == null) {
                        throw new RuntimeException("User service not available");
                    }
                    userManagementView = new UserManagementView(userService);
                }
                userManagementView.show(stage);
            } catch (Exception ex) {
                System.err.println("Error showing user management: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        viewOrdersBtn.setOnAction(e -> {
            try {
                OrderManagementView orderManagementView;
                if (applicationContext != null) {
                    orderManagementView = applicationContext.getBean(OrderManagementView.class);
                } else {
                    // Fallback: Get OrderService directly and create a new view
                    OrderService orderService = SessionManager.getInstance().getOrderService();
                    if (orderService == null) {
                        throw new RuntimeException("Order service not available");
                    }
                    orderManagementView = new OrderManagementView(orderService);
                }
                orderManagementView.show(stage);
            } catch (Exception ex) {
                System.err.println("Error showing order management: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        logoutBtn.setOnAction(e -> {
            SessionManager.getInstance().clearSession();
            if (applicationContext != null) {
                LoginScreen loginScreen = applicationContext.getBean(LoginScreen.class);
                loginScreen.show(stage);
            } else {
                // Fallback: Create a new LoginScreen with required dependencies
                UserService userService = SessionManager.getInstance().getUserService();
                if (userService == null) {
                    throw new RuntimeException("User service not available");
                }
                new LoginScreen(userService, null).show(stage);
            }
        });

        root.getChildren().addAll(manageProductsBtn, manageUsersBtn, viewOrdersBtn, logoutBtn);

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
