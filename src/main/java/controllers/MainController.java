package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mains.StartGUI;
import model.Organizer;
import model.Product;
import repositories.RepoOrganizer;
import repositories.RepoProduct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private Button viewBasketBtn;

    @FXML
    private Button addToBasketBtn;

    @FXML
    private Button viewProductDetailsBtn;

    @FXML
    private ListView<Product> productsListView;

    @FXML
    private Label loginErrorLabel;

    @FXML
    private Label labelErrorMatch;

    @FXML
    private TextField textName;

    @FXML
    private Label labelNoItemsBasket;

    @FXML
    private Spinner<Integer> quantitySpinner;

    @FXML
    private TextField textQuantity;

    @FXML
    private TextField textCustomerName;

    private Stage mainStage;
    private Stage prodStage;
    private RepoOrganizer repoOrganizer;
    private RepoProduct repoProduct;
    private Organizer loggedOrganiser;
    private List<Product> basketProducts;

    public void loadAppLoggedUser(Organizer connectedOrganiser, Stage stage, RepoOrganizer repoOrganizer, RepoProduct repoProduct,Stage prodStage) {
        this.mainStage = stage;
        this.prodStage = prodStage;
        this.repoOrganizer = repoOrganizer;
        this.repoProduct = repoProduct;
        this.loggedOrganiser = connectedOrganiser;
        this.basketProducts = new ArrayList<>();
        productsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                fillName(event);
            }
        });
        loadProducts();
    }

    public void loadProducts(){
        Iterable<Product> matches  = repoProduct.findAll();
        List<Product> lMatches = new ArrayList<>();
        matches.forEach(lMatches::add);
        productsListView.setItems(FXCollections.observableArrayList(lMatches));
    }

    public void viewBasketHandler(ActionEvent actionEvent) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("../basketview.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            BasketController basketController = fxmlLoader.getController();
            basketController.initalizeBasket(loggedOrganiser,repoProduct,prodStage,basketProducts,this);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        stage.sizeToScene();
        stage.setScene(scene);
        stage.setTitle("Basket");
        stage.setResizable(false);
        stage.show();

        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::loadNewProds);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("INTRU IN CLOSESSS");
                loadProducts();
                System.exit(0);
            }
        });
    }

    private void loadNewProds(WindowEvent t) {
        Iterable<Product> matches  = repoProduct.findAll();
        List<Product> lMatches = new ArrayList<>();
        matches.forEach(lMatches::add);
        productsListView.setItems(FXCollections.observableArrayList(lMatches));
        modifyNoItemsBasket();
    }

    private void modifyNoItemsBasket()
    {
        Integer siz = Integer.valueOf(basketProducts.size());
        labelNoItemsBasket.setText(siz.toString());
    }

    public void addToBasketHandler(ActionEvent actionEvent) {
        if(textName.getText() != null){
            try {
                Product prod = repoProduct.findByName(textName.getText());
                Integer quant = quantitySpinner.getValue();
                prod.setQuantity(quant);
                basketProducts.add(prod);
                resetFields();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Added to basket");
                alert.setContentText("Produs adaugat in cos");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
                modifyNoItemsBasket();
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

    public void fillName(MouseEvent mouseEvent) {
        Product prod = productsListView.getSelectionModel().getSelectedItem();
        textName.setText(prod.getName());
    }

    private void resetFields() {
        textName.setText(null);
        quantitySpinner.getValueFactory().setValue(0);
    }
}
