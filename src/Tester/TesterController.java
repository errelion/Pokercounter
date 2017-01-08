package Tester;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class TesterController {

    @FXML
    Button buttonToPress;



    @FXML
    protected void buttonToPressPressed(){
        buttonToPress.setText("lol");
    }
}