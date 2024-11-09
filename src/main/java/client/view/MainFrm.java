package client.view;

import client.controller.ClientCtr;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import shared.dto.ObjectWrapper;
import javafx.application.Platform;
import shared.model.Player;

import java.util.ArrayList;
import java.util.Collections;


public class MainFrm {

    private ClientCtr mySocket = ClientCtr.getInstance();
    private Stage stage = mySocket.getStage();

    public MainFrm() {
    }

    public void openScene() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Main.fxml"));
                Scene scene = new Scene(loader.load());
                mySocket.setMainScene(scene);
                stage.setScene(scene);
                stage.setTitle("Main");
                stage.show();

                //set username main lblUserName01
                Label lblUserName = (Label) loader.getNamespace().get("usernameMain");
                lblUserName.setText(mySocket.getUsername());

                //client send request to server to get all user
                mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_ALL_USER, null));


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void receivedDataProcessing(ObjectWrapper data) {
        Platform.runLater(() -> {
            Scene scene = mySocket.getMainScene();

            switch (data.getPerformative()) {
                case ObjectWrapper.SERVER_SEND_ALL_USER:

                    ArrayList<Player> listUser = (ArrayList<Player>) data.getData();
                    listUser.sort((o1, o2) -> o1.getUsername().compareTo(o2.getUsername()));

                    int numberUser = listUser.size();
                    System.out.println("numberUser: " + numberUser);

                    // cap nhat giao dien hien thi danh sach user
                    VBox screnListUser = (VBox) scene.lookup("#listUerVbox");
                    if(screnListUser == null) {
                        System.out.println("screnListUser is null");
                    } else {
                        System.out.println("screnListUser is not null");
                    }
                    screnListUser.setPrefHeight(41 * (numberUser + 1));
                    screnListUser.getChildren().clear();

                    for (Player player : listUser) {
                        if (player.getUsername().equals(mySocket.getUsername())) {
                            continue;
                        }

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/ItemUser.fxml"));
                        try {
                            HBox itemUser = loader.load();
                            Label lblUserName = (Label) loader.getNamespace().get("usernameItem");
                            Label lblStatus = (Label) loader.getNamespace().get("statusItem");
                            Circle circleStatus = (Circle) loader.getNamespace().get("circleStatus");
                            Button btnInvite = (Button) itemUser.lookup("#buttonInvite");

                            lblUserName.setText(player.getUsername());
                            lblStatus.setText("Offline");
                            circleStatus.setStyle("-fx-stroke: #262825");
                            btnInvite.setVisible(false);

                            screnListUser.getChildren().add(itemUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mySocket.sendData(new ObjectWrapper(ObjectWrapper.UPDATE_WAITING_LIST_REQUEST, null));
                    break;

                case ObjectWrapper.SERVER_INFORM_CLIENT_WAITING:
                // cap nhat status cua user
                    ArrayList<Player> listUserWaiting = (ArrayList<Player>) data.getData();
                    System.out.println("listUserWaiting: " + listUserWaiting.size());

                    ArrayList<HBox> listUserItem = new ArrayList<>();
                    VBox screnListUserWaiting = (VBox) scene.lookup("#listUerVbox");

                    for (int i = 0; i < screnListUserWaiting.getChildren().size(); i++) {
                        // lay ra item user
                        HBox itemUser = (HBox) screnListUserWaiting.getChildren().get(i);
                        AnchorPane anchorPane = (AnchorPane) itemUser.getChildren().getFirst();
                        Label lblUserName = (Label) anchorPane.lookup("#usernameItem");
                        Label lblStatus = (Label) anchorPane.lookup("#statusItem");
                        Circle circleStatus = (Circle) anchorPane.lookup("#circleStatus");
                        Button btnInvite = (Button) itemUser.lookup("#buttonInvite");


                        if(lblUserName == null) {
                            System.out.println("lblUserName is null");
                        } else {
                            // dat mac dinh la offline
                            lblStatus.setText("Offline");
                            circleStatus.setStyle("-fx-stroke: #262825");
                            listUserItem.add(itemUser);
                            btnInvite.setVisible(false);

                            // Cat nhat lai online hoac in game
                            for (Player player : listUserWaiting) {
                                if (lblUserName.getText().equals(player.getUsername())) {
                                    circleStatus.setStyle("-fx-stroke: #00ff00");
                                    if(player.getStatus().equals("Online")) {
                                        btnInvite.setVisible(true);
                                        lblStatus.setText("Online");
                                    }
                                    if (player.getStatus().equals("In Game")) lblStatus.setText("In Game");
                                    break;

                                }
                            }
                        }
                    }
                    break;
                case ObjectWrapper.SERVER_SEND_HISTORY:

            }
        });
    }
}
