package mains;

import controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repositories.RepoOrganizer;
import repositories.RepoProduct;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartGUI  extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Properties props=new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }

        RepoOrganizer repoOrganiser = new RepoOrganizer(props);
        RepoProduct repoProduct = new RepoProduct(props);

        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("../login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 300);
        LoginController loginController = fxmlLoader.getController();
        loginController.setRepos(repoOrganiser,repoProduct,stage);
        stage.setTitle("Basket Match");
        stage.setScene(scene);
        stage.show();
    }
}
