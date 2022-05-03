package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mains.StartGUI;
import model.Organizer;
import model.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import repositories.RepoOrganizer;
import repositories.RepoProduct;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TextField searchBar;

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
    private HttpClient client;
    private Product currentProduct;


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

        productsListView.setCellFactory(cell -> {
            return new ListCell<Product>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.getName() + "\t\t\t\t\t\tPrice: " + item.getPrice());

                        setFont(Font.font(16));
                    }
                }
            };
        });

        client = HttpClient.newHttpClient();
        loadProducts();
    }

    public void loadProducts(){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-type","application/x-www-form-urlencoded")
                .uri(URI.create("http://localhost:3000/api/v1/get_products"))
                .build();
        List<Product> lMatches = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(MainController::parseProductsList)
                .join();
        productsListView.setItems(FXCollections.observableArrayList(lMatches));
    }

    private static List<Product>  parseProductsList(String responseBody) {
        JSONObject resp = new JSONObject(responseBody);
        JSONArray jsonProdArr = resp.getJSONArray("data");
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < jsonProdArr.length(); i++)
        {
            JSONObject prJson =(JSONObject) jsonProdArr.get(i);
            Integer id = prJson.getInt("id");
            String name = prJson.getString("name");
            String details = prJson.getString("details");
            Double price =Double.parseDouble(prJson.getString("price"));
            Integer quantity = prJson.getInt("quantity");

            Product pr = new Product(id,name,details,price,quantity);
            products.add(pr);
        }
        return products;
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
    }

    public void emptyBasket()
    {
        basketProducts.clear();
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

                Map<Object, Object> data = new HashMap<>();
                data.put("name", textName.getText());
                HttpRequest request = HttpRequest.newBuilder()
                        .POST(ofFormData(data))
                        .header("Content-type","application/x-www-form-urlencoded")
                        .uri(URI.create("http://localhost:3000/api/v1/find_product"))
                        .build();
                Product prod = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenApply(MainController::parseFoundProduct)
                        .join();
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

    public static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    public static Product parseFoundProduct(String responseBody) {
        JSONObject resp = new JSONObject(responseBody);
        JSONObject prJson = resp.getJSONObject("data");
        Integer id = prJson.getInt("id");
        String name = prJson.getString("name");
        String details = prJson.getString("details");
        Double price =Double.parseDouble(prJson.getString("price"));
        Integer quantity = prJson.getInt("quantity");

        return new Product(id,name,details,price,quantity);
    }

    public void fillName(MouseEvent mouseEvent) {
        Product prod = productsListView.getSelectionModel().getSelectedItem();
        this.currentProduct = prod;
        textName.setText(prod.getName());
    }

    private void resetFields() {
        textName.setText(null);
        quantitySpinner.getValueFactory().setValue(0);
    }

    public void searchProductsHandler(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER))
        {
            if(!searchBar.getText().equals("") && searchBar.getText() != null)
            {
                Map<Object, Object> data = new HashMap<>();
                data.put("query", searchBar.getText());

                HttpRequest request = HttpRequest.newBuilder()
                        .POST(ofFormData(data))
                        .header("Content-type","application/x-www-form-urlencoded")
                        .uri(URI.create("http://localhost:3000/api/v1/search_products"))
                        .build();
                List<Product> lMatches = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenApply(MainController::parseProductsList)
                        .join();
                productsListView.setItems(FXCollections.observableArrayList(lMatches));
            }
            else {
                loadProducts();
            }
        }
    }

    public void viewProductDetailsHandler(ActionEvent actionEvent) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("../product-details-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            ProductDetailsController productDetailsController = fxmlLoader.getController();
            productDetailsController.viewProduct(currentProduct);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        stage.sizeToScene();
        stage.setScene(scene);
        stage.setTitle("Product details");
        stage.setResizable(false);
        stage.show();
    }
}
