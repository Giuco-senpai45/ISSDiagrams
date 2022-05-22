package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mains.StartGUI;
import model.Admin;
import model.Organizer;
import model.Product;
import org.json.JSONArray;
import org.json.JSONObject;

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

public class AdminController {

    @FXML
    public TextField textUserName;

    @FXML
    public TextField textPassword;

    @FXML
    public ListView<Organizer> usersListView;

    @FXML
    private ListView<Product> productsListView;

    @FXML
    private TextField searchBar;

    @FXML
    private TextField textName;

    @FXML
    private TextField textDetails;

    @FXML
    private TextField textPrice;

    @FXML
    private Spinner<Integer> quantitySpinner;

    private Stage mainStage;
    private Admin loggedAdmin;
    private HttpClient client;
    private Product currentProduct;
    private Organizer currentUser;

    public void loadAppLoggedAdmin(Admin loggedAdmin, Stage mainStage) {
        this.loggedAdmin = loggedAdmin;
        this.mainStage = mainStage;

        productsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                fillName(event);
            }
        });

        usersListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                fillUsername(event);
            }
        });

        client = HttpClient.newHttpClient();
        loadProducts();
        loadUsers();
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

    private void loadProducts() {
        productsListView.getItems().clear();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-type","application/x-www-form-urlencoded")
                .uri(URI.create("http://localhost:3000/api/v1/get_products"))
                .build();
        List<Product> lMatches = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(AdminController::parseProductsList)
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

    private void loadUsers() {
        usersListView.getItems().clear();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Content-type","application/x-www-form-urlencoded")
                .uri(URI.create("http://localhost:3000/api/v1/get_users"))
                .build();
        List<Organizer> organizers = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(AdminController::parseUsersList)
                .join();
        usersListView.setItems(FXCollections.observableArrayList(organizers));
    }

    private static List<Organizer>  parseUsersList(String responseBody) {
        JSONObject resp = new JSONObject(responseBody);
        JSONArray jsonProdArr = resp.getJSONArray("data");
        List<Organizer> organizers = new ArrayList<>();
        for (int i = 0; i < jsonProdArr.length(); i++)
        {
            JSONObject prJson =(JSONObject) jsonProdArr.get(i);
            Integer id = prJson.getInt("id");
            String name = prJson.getString("name");
            String password = prJson.getString("password");

            Organizer o = new Organizer(id,name,password);
            organizers.add(o);
        }
        return organizers;
    }

    public void fillName(MouseEvent mouseEvent) {
        Product prod = productsListView.getSelectionModel().getSelectedItem();
        this.currentProduct = prod;
        textName.setText(prod.getName());
    }

    private void fillUsername(MouseEvent event) {
        Organizer org = usersListView.getSelectionModel().getSelectedItem();
        this.currentUser = org;
        textUserName.setText(org.getName());
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
                        .thenApply(AdminController::parseProductsList)
                        .join();
                productsListView.getItems().clear();
                productsListView.setItems(FXCollections.observableArrayList(lMatches));
            }
            else {
                productsListView.getItems().clear();
                loadProducts();
            }
        }
    }

    public void removeUserHandler(ActionEvent actionEvent) {
        Map<Object, Object> data = new HashMap<>();
        data.put("id", currentUser.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .POST(ofFormData(data))
                .header("Content-type","application/x-www-form-urlencoded")
                .uri(URI.create("http://localhost:3000/api/v1/delete_user"))
                .build();
        Organizer org = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(AdminController::parseUser)
                .join();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successfully deleted user");
        alert.setContentText(org.toString());
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
        loadUsers();
        resetTextFieldsUser();
    }

    public void addUserHandler(ActionEvent actionEvent) {
        Map<Object, Object> data = new HashMap<>();
        data.put("name", textUserName.getText());
        data.put("password", textPassword.getText());

        HttpRequest request = HttpRequest.newBuilder()
                .POST(ofFormData(data))
                .header("Content-type","application/x-www-form-urlencoded")
                .uri(URI.create("http://localhost:3000/api/v1/save_user"))
                .build();
        Organizer org = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(AdminController::parseUser)
                .join();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successfully added user");
        alert.setContentText(org.toString());
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
        loadUsers();
        resetTextFieldsUser();
    }

    private static Organizer parseUser(String responseBody)
    {
        JSONObject resp = new JSONObject(responseBody);
        JSONObject orgJson = resp.getJSONObject("data");
        Integer id = orgJson.getInt("id");
        String name = orgJson.getString("name");
        String password = orgJson.getString("password");

        return new Organizer(id,name,password);
    }

    private static Product parseProduct(String responseBody)
    {
        JSONObject resp = new JSONObject(responseBody);
        JSONObject orgJson = resp.getJSONObject("data");
        Integer id = orgJson.getInt("id");
        String name = orgJson.getString("name");
        String details = orgJson.getString("details");
        Double price = orgJson.getDouble("price");
        Integer quantity = orgJson.getInt("quantity");

        return new Product(id,name,details,price,quantity);
    }

    public void updateProductHandler(ActionEvent actionEvent) {
        Map<Object, Object> data = new HashMap<>();
        data.put("id", currentProduct.getId());
        data.put("name", textName.getText());
        data.put("details", textDetails.getText());
        data.put("price", textPrice.getText());
        data.put("quantity", quantitySpinner.getValue());

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(ofFormData(data))
                .header("Content-type","application/x-www-form-urlencoded")
                .uri(URI.create("http://localhost:3000/api/v1/update_product"))
                .build();
        Product pr = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(AdminController::parseProduct)
                .join();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successfully updated product");
        alert.setContentText(pr.toString());
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
        loadProducts();
        resetTextFieldsProduct();
    }

    public void addProductHandler(ActionEvent actionEvent) {
        Map<Object, Object> data = new HashMap<>();
        data.put("name", textName.getText());
        data.put("details", textDetails.getText());
        data.put("price", textPrice.getText());
        data.put("quantity", quantitySpinner.getValue());

        HttpRequest request = HttpRequest.newBuilder()
                .POST(ofFormData(data))
                .header("Content-type","application/x-www-form-urlencoded")
                .uri(URI.create("http://localhost:3000/api/v1/add_product"))
                .build();
        Product pr = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(AdminController::parseProduct)
                .join();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successfully updated product");
        alert.setContentText(pr.toString());
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
        loadProducts();
        resetTextFieldsProduct();
    }

    public void viewProductDetailsHandler(ActionEvent actionEvent) {
        if(currentProduct != null)
        {
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
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setContentText("No product selected");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
    }

    private void resetTextFieldsProduct() {
        textName.setText(null);
        textPrice.setText(null);
        textDetails.setText(null);
        quantitySpinner.getValueFactory().setValue(0);

    }

    private void resetTextFieldsUser() {
        textUserName.setText(null);
        textPassword.setText(null);
    }
}
