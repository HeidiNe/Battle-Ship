package client.view;

import client.controller.ClientCtr;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shared.dto.ObjectWrapper;

public class RegisterFrm {

    private ClientCtr mySocket = ClientCtr.getInstance();
    private Stage stage = mySocket.getStage();

    public RegisterFrm() {
    }

    public void openScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Register.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Register");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receivedDataProcessing(ObjectWrapper data) {
    }
}
