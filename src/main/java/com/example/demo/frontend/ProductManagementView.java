package com.example.demo.frontend;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.text.NumberFormat;
import java.util.Locale;

@Component
public class ProductManagementView {
    private final ProductService productService;
    private Stage stage;
    private TableView<Product> productTable;
    
    @Autowired
    public ProductManagementView(ProductService productService) {
        this.productService = productService;
    }

    public void show(Stage stage) {
        this.stage = stage;
        
        // Create main layout
        VBox rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(20));
        
        // Add title
        Label titleLabel = new Label("Manage Products");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        // Create product table
        productTable = createProductTable();
        
        // Create buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button addButton = new Button("Add Product");
        Button editButton = new Button("Edit Product");
        Button deleteButton = new Button("Delete Product");
        Button backButton = new Button("Back to Dashboard");
        
        addButton.setOnAction(e -> showAddProductDialog());
        editButton.setOnAction(e -> showEditProductDialog());
        deleteButton.setOnAction(e -> deleteSelectedProduct());
        backButton.setOnAction(e -> new AdminDashboard(stage).show());
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, backButton);
        
        // Add all components to root layout
        rootLayout.getChildren().addAll(titleLabel, productTable, buttonBox);
        
        // Load products
        loadProducts();
        
        // Set the scene
        Scene scene = new Scene(rootLayout, 1000, 600);
        stage.setTitle("Product Management");
        stage.setScene(scene);
        stage.show();
    }
    
    private TableView<Product> createProductTable() {
        TableView<Product> table = new TableView<>();
        
        // Create columns
        TableColumn<Product, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        
        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        
        TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        
        TableColumn<Product, BigDecimal> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        priceColumn.setCellFactory(tc -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });
        
        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStockQuantity()).asObject());
        
        TableColumn<Product, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        
        TableColumn<Product, String> imageUrlColumn = new TableColumn<>("Image URL");
        imageUrlColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImageUrl()));
        
        // Add columns to table
        table.getColumns().addAll(idColumn, nameColumn, descriptionColumn, priceColumn, 
                                stockColumn, categoryColumn, imageUrlColumn);
        
        return table;
    }
    
    private void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            productTable.setItems(FXCollections.observableArrayList(products));
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage());
        }
    }
    
    private void showAddProductDialog() {
        Dialog<ProductDTO> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter product details");
        
        // Create form fields
        TextField nameField = new TextField();
        TextArea descriptionField = new TextArea();
        TextField priceField = new TextField();
        TextField stockField = new TextField();
        TextField categoryField = new TextField();
        TextField imageUrlField = new TextField();
        
        // Create form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);
        grid.add(new Label("Image URL:"), 0, 5);
        grid.add(imageUrlField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        
        // Add input validation for price
        priceField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            try {
                new BigDecimal(newText);
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
        
        // Convert result to ProductDTO
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(nameField.getText());
                productDTO.setDescription(descriptionField.getText());
                productDTO.setPrice(new BigDecimal(priceField.getText()));
                productDTO.setStockQuantity(Integer.parseInt(stockField.getText()));
                productDTO.setCategory(categoryField.getText());
                productDTO.setImageUrl(imageUrlField.getText());
                return productDTO;
            }
            return null;
        });
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(productDTO -> {
            try {
                Product product = productService.createProduct(productDTO);
                loadProducts();
                showAlert("Success", "Product added successfully!");
            } catch (Exception e) {
                showAlert("Error", "Failed to add product: " + e.getMessage());
            }
        });
    }
    
    private void showEditProductDialog() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "Please select a product to edit");
            return;
        }
        
        Dialog<ProductDTO> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Edit product details");
        
        // Create form fields with current values
        TextField nameField = new TextField(selectedProduct.getName());
        TextArea descriptionField = new TextArea(selectedProduct.getDescription());
        TextField priceField = new TextField(selectedProduct.getPrice().toString());
        TextField stockField = new TextField(selectedProduct.getStockQuantity().toString());
        TextField categoryField = new TextField(selectedProduct.getCategory());
        TextField imageUrlField = new TextField(selectedProduct.getImageUrl());
        
        // Create form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);
        grid.add(new Label("Image URL:"), 0, 5);
        grid.add(imageUrlField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        // Add input validation for price
        priceField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            try {
                new BigDecimal(newText);
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
        
        // Convert result to ProductDTO
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(nameField.getText());
                productDTO.setDescription(descriptionField.getText());
                productDTO.setPrice(new BigDecimal(priceField.getText()));
                productDTO.setStockQuantity(Integer.parseInt(stockField.getText()));
                productDTO.setCategory(categoryField.getText());
                productDTO.setImageUrl(imageUrlField.getText());
                return productDTO;
            }
            return null;
        });
        
        // Show dialog and handle result
        dialog.showAndWait().ifPresent(productDTO -> {
            try {
                Product product = productService.updateProduct(selectedProduct.getId(), productDTO);
                loadProducts();
                showAlert("Success", "Product updated successfully!");
            } catch (Exception e) {
                showAlert("Error", "Failed to update product: " + e.getMessage());
            }
        });
    }
    
    private void deleteSelectedProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "Please select a product to delete");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Product");
        alert.setContentText("Are you sure you want to delete this product?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    productService.deleteProduct(selectedProduct.getId());
                    loadProducts();
                    showAlert("Success", "Product deleted successfully!");
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete product: " + e.getMessage());
                }
            }
        });
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 