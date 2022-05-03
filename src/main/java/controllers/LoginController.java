package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mains.StartGUI;
import model.Organizer;
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
import java.util.HashMap;
import java.util.Map;

public class LoginController {
    @FXML
    private Button loginBtn;

    @FXML
    private Label loginErrorLabel;

    @FXML
    private TextField textPassword;

    @FXML
    private TextField textName;

    private Stage mainStage;
    private RepoOrganizer repoOrganizer;
    private RepoProduct repoProduct;
    private HttpClient client;

    public void loginOrganiserHandler(ActionEvent actionEvent) {
        if(textName.getText().equals("")){
            loginErrorLabel.setText("Name cannot be empty!");
        }
        else if (textPassword.getText().equals("")) {
            loginErrorLabel.setText("Password cannot be empty!");
        }
        else {
            String name = textName.getText();
            String password = textPassword.getText();

            Map<Object, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("password", password);
            try {
//                Organizer loggedOrganiser = repoOrganizer.findOrganiserLogin(name,password);
                HttpRequest request = HttpRequest.newBuilder()
                        .POST(ofFormData(data))
                        .header("Content-type","application/x-www-form-urlencoded")
                        .uri(URI.create("http://localhost:3000/api/v1/authenticate_user"))
                        .build();
                Organizer loggedOrganiser = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenApply(LoginController::parseLoggedOrganiser)
                        .join();
                if(loggedOrganiser == null){
                    loginErrorLabel.setText("We couldn't find that username!");
                    resetTextFields();
                    return;
                }
                else {
                    connectUser(loggedOrganiser, actionEvent);
                }
                loginErrorLabel.setVisible(false);
            }
            catch(Exception e){
//                loginErrorLabel.setText(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                System.out.println(e.getMessage());
                alert.setTitle("Error");
                alert.setHeaderText("Look, an Information Dialog");
                alert.setContentText(e.getMessage());
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
                loginErrorLabel.setVisible(true);
            }
        }
        resetTextFields();
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

    public static Organizer parseLoggedOrganiser(String responseBody) {
        JSONObject resp = new JSONObject(responseBody);
        JSONObject orgJson = resp.getJSONObject("data");
        Integer id = orgJson.getInt("id");
        String name = orgJson.getString("name");
        String password = orgJson.getString("password");

        return new Organizer(id,name,password);
    }

    public void setRepos(RepoOrganizer repoOrganiser, RepoProduct repoProduct,Stage stage) {
        this.repoOrganizer = repoOrganiser;
        this.repoProduct = repoProduct;
        this.mainStage = stage;

         client = HttpClient.newHttpClient();
    }

    public void connectUser(Organizer connectedOrganiser, ActionEvent actionEvent)
    {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("../matches-main-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            MainController matchesController = fxmlLoader.getController();
            matchesController.loadAppLoggedUser(connectedOrganiser,stage,repoOrganizer,repoProduct,stage);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        stage.sizeToScene();
        stage.setScene(scene);
        stage.setTitle("Matches");
        stage.setResizable(false);
        stage.show();
        mainStage.hide();
    }

    private void resetTextFields(){
        textName.setText(null);
        textPassword.setText(null);
    }
}
