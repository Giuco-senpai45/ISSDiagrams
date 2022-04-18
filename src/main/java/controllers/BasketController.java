package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Organizer;
import model.Product;
import repositories.RepoProduct;

import java.util.ArrayList;
import java.util.List;

public class BasketController {

    @FXML
    private TextField textAddress;

    @FXML
    private TextField textPhone;

    @FXML
    private TextField textComment;

    @FXML
    private Label totalLabel;

    @FXML
    private ListView<Product> productsListView;

    private Stage mainStage;
    private Organizer loggedOrganiser;
    private MainController mainCtrl;
    private RepoProduct repoProduct;
    private List<Product> basketProducts;

    public void initalizeBasket(Organizer loggedOrganiser, RepoProduct repoProduct,Stage stage, List<Product> basket,MainController mainCtrl) {
        this.loggedOrganiser = loggedOrganiser;
        this.repoProduct = repoProduct;
        this.mainStage = stage;
        this.mainCtrl = mainCtrl;
        this.basketProducts = basket;
        calculateTotal();
        loadProducts();
    }

    public void calculateTotal()
    {
        Double total = 0.0;
        for (Product product : basketProducts)
        {
            total += product.getPrice() * product.getQuantity();
        }
        totalLabel.setText(total.toString());
    }

    public void loadProducts(){
        productsListView.setItems(FXCollections.observableArrayList(basketProducts));
    }

    public void placeOrderHandler(ActionEvent actionEvent) {
        if(textAddress.getText() != null && textPhone.getText() != null)
        {
            String address = textAddress.getText();
            String phone = textPhone.getText();
            String comment = textComment.getText();
            try {
                for(Product prod : basketProducts)
                {
                    Product repoProd = repoProduct.findById(prod.getId());
                    Integer newQuant = repoProd.getQuantity() - prod.getQuantity();
                    if(newQuant > 0)
                    {
                        repoProd.setQuantity(newQuant);
                        repoProduct.update(repoProd, repoProd.getId());
                        productsListView.setItems(null);
                        Stage stage = (Stage) textComment.getScene().getWindow();
                        stage.close();
                        mainCtrl.loadProducts();
                    }
                    else {
                        throw new Exception("Cantitatea solicitata nu este disponibila");
                    }
                }
            }
            catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                System.out.println(e.getMessage());
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            }
        }
    }
}
