package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import model.Product;

public class ProductDetailsController {
    @FXML
    private Label textName;

    @FXML
    private Label textQuantity;

    @FXML
    private Label textPrice;

    @FXML
    private TextArea textDescription;

    public void viewProduct(Product currentProduct) {
        textName.setText(currentProduct.getName());
        textQuantity.setText(currentProduct.getQuantity().toString());
        textPrice.setText(currentProduct.getPrice().toString());
        textDescription.setText(currentProduct.getDetails());
    }
}
