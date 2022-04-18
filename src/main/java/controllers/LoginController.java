package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mains.StartGUI;
import model.Organizer;
import repositories.RepoOrganizer;
import repositories.RepoProduct;

import java.io.IOException;

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
            try {
                Organizer loggedOrganiser = repoOrganizer.findOrganiserLogin(name,password);
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

    public void setRepos(RepoOrganizer repoOrganiser, RepoProduct repoProduct,Stage stage) {
        this.repoOrganizer = repoOrganiser;
        this.repoProduct = repoProduct;
        this.mainStage = stage;
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
