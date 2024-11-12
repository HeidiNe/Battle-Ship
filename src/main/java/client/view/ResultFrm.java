package client.view;

import client.controller.ClientCtr;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import shared.dto.ObjectWrapper;
import shared.dto.PlayerHistory;
import shared.model.Match;
import shared.model.Player;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultFrm  {

    private ClientCtr mySocket = ClientCtr.getInstance();
    private Stage stage = mySocket.getStage();

    public ResultFrm() {
    }

    public void openScene() {
        // Khởi tạo âm thanh trước
//        initializeBackgroundMusic();

        // Sau đó xử lý UI trong Platform.runLater
        Platform.runLater(() -> {
            try {

                System.out.println("In result form: ");
                mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_RESULT));

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Result.fxml"));
                Scene scene = new Scene(loader.load());
                mySocket.setResultScene(scene);

                stage.setScene(scene);
                stage.setTitle("Result");
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void receivedDataProcessing(ObjectWrapper data) {
        Platform.runLater(() -> {
            Scene scene = mySocket.getMainScene();

            switch (data.getPerformative()) {
                case ObjectWrapper.SERVER_SEND_RESULT:

                    String[] resultAndUserNameEnemy = ((String) data.getData()).split("\\|\\|");

                    String result = resultAndUserNameEnemy[0];
                    String usernameEnemy = resultAndUserNameEnemy[1];

                    if (result.equals("win")) {
                        FXMLLoader win = new FXMLLoader(getClass().getResource("/Fxml/Client/Win.fxml"));
                        try {
                            Scene winScene = win.load();
                            Button btn = (Button) win.getNamespace().get("btnPlayAgain");
                            btn.setOnAction(e -> {
                                mySocket.sendData(new ObjectWrapper(ObjectWrapper.BACK_TO_MAIN_FORM));
                                stage.setScene(mySocket.getMainScene());
                            });

                            stage.setScene(winScene);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    } else if (result.equals("loss")) {
                        FXMLLoader loss = new FXMLLoader(getClass().getResource("/Fxml/Client/Lose.fxml"));
                        try {
                            Scene lossScene = loss.load();
                            Button btn = (Button) loss.getNamespace().get("btnPlayAgain");
                            btn.setOnAction(e -> {
                                mySocket.sendData(new ObjectWrapper(ObjectWrapper.BACK_TO_MAIN_FORM));
                                stage.setScene(mySocket.getMainScene());
                            });

                            stage.setScene(lossScene);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        FXMLLoader win = new FXMLLoader(getClass().getResource("/Fxml/Client/Win.fxml"));
                        try {
                            Scene winScene = win.load();
                            Button btn = (Button) win.getNamespace().get("btnPlayAgain");
                            Label lblPoint = (Label) win.getNamespace().get("lblPoint");
                            lblPoint.setText("+0 POINT");

                            btn.setOnAction(e -> {
                                mySocket.sendData(new ObjectWrapper(ObjectWrapper.BACK_TO_MAIN_FORM));
                                stage.setScene(mySocket.getMainScene());
                            });

                            stage.setScene(winScene);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;

            }
        });

    }


    public void initializeBackgroundMusic() {
    }
}
