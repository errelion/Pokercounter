package Tester;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TesterMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("testerGUI.fxml"));
        primaryStage.setTitle("Pokercounter");
        primaryStage.setScene(new Scene(root, 700, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);

        ArrayList <Integer> liste = new ArrayList<>();

        liste.add(1);
    }
}
