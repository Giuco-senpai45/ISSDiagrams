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
import org.json.JSONObject;
import repositories.RepoProduct;

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
    private HttpClient client;

    public void initalizeBasket(Organizer loggedOrganiser, RepoProduct repoProduct,Stage stage, List<Product> basket,MainController mainCtrl) {
        this.loggedOrganiser = loggedOrganiser;
        this.repoProduct = repoProduct;
        this.mainStage = stage;
        this.mainCtrl = mainCtrl;
        this.basketProducts = basket;
        client = HttpClient.newHttpClient();

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
            String adress = textAddress.getText();
            String phone = textPhone.getText();
            String comment = textComment.getText();
            if(adress != null && !adress.equals("") && phone != null && !phone.equals(""))
            {
                try {
                    for(Product prod : basketProducts)
                    {
                        Map<Object, Object> data = new HashMap<>();
                        data.put("name", prod.getName());
                        data.put("quantity", prod.getQuantity());

                        HttpRequest request = HttpRequest.newBuilder()
                                .PUT(ofFormData(data))
                                .header("Content-type","application/x-www-form-urlencoded")
                                .uri(URI.create("http://localhost:3000/api/v1/order_product"))
                                .build();
                        Product orderedProd = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                                .thenApply(HttpResponse::body)
                                .thenApply(BasketController::parseFoundProduct)
                                .join();

                    }

                    Map<Object, Object> data = new HashMap<>();
                    data.put("adress", adress);
                    data.put("phone", phone);
                    data.put("comment", comment);
                    data.put("organizer", loggedOrganiser.getId());

                    HttpRequest request = HttpRequest.newBuilder()
                            .PUT(ofFormData(data))
                            .header("Content-type","application/x-www-form-urlencoded")
                            .uri(URI.create("http://localhost:3000/api/v1/place_order"))
                            .build();

                    String msgOrderPlaced = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenApply(HttpResponse::body)
                            .thenApply(BasketController::parseOrderPlaced)
                            .join();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    System.out.println(msgOrderPlaced);
                    alert.setTitle("Order placed successfully");
                    alert.setContentText(msgOrderPlaced);
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK.");
                        }
                    });

                    Stage stage = (Stage) textComment.getScene().getWindow();
                    stage.close();
                    mainCtrl.loadProducts();
                    mainCtrl.emptyBasket();
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
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                System.out.println("Please fill in the required fields");
                alert.setTitle("Error");
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

    public static String parseOrderPlaced(String responseBody) {
        JSONObject resp = new JSONObject(responseBody);
        return resp.getString("message");
    }
}
