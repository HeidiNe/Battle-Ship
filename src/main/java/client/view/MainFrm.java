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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import shared.dto.ObjectWrapper;
import javafx.application.Platform;
import shared.dto.PlayerHistory;
import shared.model.Match;
import shared.model.Player;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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

                // Lay button Home, History, Ranking va stackPane
                Button btnHome = (Button)  loader.getNamespace().get("btnHome");
                Button btnHistory = (Button)  loader.getNamespace().get("btnHistory");
                Button btnRanked = (Button)  loader.getNamespace().get("btnRanked");
                StackPane stackPane = (StackPane) loader.getNamespace().get("stackPane");

                FXMLLoader loaderHome = new FXMLLoader(getClass().getResource("/Fxml/Client/Home.fxml"));
                FXMLLoader loaderHistory = new FXMLLoader(getClass().getResource("/Fxml/Client/History.fxml"));
                FXMLLoader loaderRanked = new FXMLLoader(getClass().getResource("/Fxml/Client/Ranked.fxml"));

                AnchorPane home = loaderHome.load();
                AnchorPane history = loaderHistory.load();
                AnchorPane ranked = loaderRanked.load();

                stackPane.getChildren().add(home);
                stackPane.getChildren().add(history);
                stackPane.getChildren().add(ranked);

                home.setVisible(true);
                history.setVisible(false);
                ranked.setVisible(false);

                btnHome.setStyle("-fx-text-fill: rgba(29, 43, 35, 0.9);");
                btnHistory.setStyle("-fx-text-fill: rgba(44, 64, 53, 0.7);");
                btnRanked.setStyle("-fx-text-fill: rgba(44, 64, 53, 0.7);");

                // Xu ly su kien click vao button Home
                btnHome.setOnAction(event -> {
                    try {
                        home.setVisible(true);
                        history.setVisible(false);
                        ranked.setVisible(false);

                        btnHome.setStyle("-fx-text-fill: rgba(29, 43, 35, 0.9);");
                        btnHistory.setStyle("-fx-text-fill: rgba(44, 64, 53, 0.7);");
                        btnRanked.setStyle("-fx-text-fill: rgba(44, 64, 53, 0.7);");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                // Xu ly su kien click vao button History
                btnHistory.setOnAction(event -> {
                    try {
                        home.setVisible(false);
                        history.setVisible(true);
                        ranked.setVisible(false);

                        btnHome.setStyle("-fx-text-fill: rgba(44, 64, 53, 0.7);");
                        btnHistory.setStyle("-fx-text-fill: rgba(29, 43, 35, 0.9);");
                        btnRanked.setStyle("-fx-text-fill: rgba(44, 64, 53, 0.7);");

                        mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_HISTORY));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                // Xu ly su kien click vao button Ranking
                btnRanked.setOnAction(event -> {
                    try {
                        home.setVisible(false);
                        history.setVisible(false);
                        ranked.setVisible(true);

                        btnHome.setStyle("-fx-text-fill: rgba(44, 64, 53, 0.7);");
                        btnHistory.setStyle("-fx-text-fill: rgba(44, 64, 53, 0.7);");
                        btnRanked.setStyle("-fx-text-fill: rgba(29, 43, 35, 0.9);");

                        mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_RANKING));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

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
                                    lblStatus.setStyle("-fx-text-fill: #4a8f4a");
                                    if(player.getStatus().equals("Online")) {

                                        lblStatus.setText("Online");
                                        btnInvite.setVisible(true);

                                        btnInvite.setOnAction(event -> {
                                            mySocket.sendData(new ObjectWrapper(ObjectWrapper.SEND_PLAY_REQUEST, player.getUsername()));
                                        });
                                    }
                                    else lblStatus.setText("In Game");
                                    break;

                                }
                            }
                        }
                    }
                    break;
                case ObjectWrapper.RECEIVE_PLAY_REQUEST:
                    String username = (String) data.getData();

                    VBox receivePlayRequest = (VBox) scene.lookup("#RECEIVE_PLAY_REQUEST");

                    boolean isExist = false;

                    ArrayList<HBox> listUserItemReceive = new ArrayList<>();
                    for (int i = 0; i < receivePlayRequest.getChildren().size(); i++) {
                        HBox itemUser = (HBox) receivePlayRequest.getChildren().get(i);
                        AnchorPane anchorPane = (AnchorPane) itemUser.getChildren().getFirst();
                        Label lblUserName = (Label) anchorPane.lookup("#userNameInvite");
                        if(lblUserName.getText().equals(username)) {
                            isExist = true;
                            break;
                        }
                    }

                    if (isExist) {
                        break;
                    }

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/ItemHome.fxml"));
                    try {
                        HBox itemUser = loader.load();
                        Label lblUserNameInvite = (Label) itemUser.lookup("#userNameInvite");

                        lblUserNameInvite.setText(username);
                        receivePlayRequest.getChildren().add(itemUser);

                        // Xu ly su kien click vao button Accept: chap nhan loi moi choi
                        Button btnAccept = (Button) itemUser.lookup("#btnAccept");
                        btnAccept.setOnAction(event -> {
                            mySocket.sendData(new ObjectWrapper(ObjectWrapper.ACCEPTED_PLAY_REQUEST));
                            receivePlayRequest.getChildren().remove(itemUser);
                        });

                        // Xu ly su kien click vao button Reject: tu choi loi moi choi
                        Button btnReject = (Button) itemUser.lookup("#btnReject");
                        btnReject.setOnAction(event -> {
                            mySocket.sendData(new ObjectWrapper(ObjectWrapper.REJECTED_PLAY_REQUEST));
                            receivePlayRequest.getChildren().remove(itemUser);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case ObjectWrapper.SERVER_SEND_HISTORY:
                    PlayerHistory playerHistory = (PlayerHistory) data.getData();
                    List<Match> listMatch = playerHistory.getListMatch();

                    VBox vboxHistory = (VBox) scene.lookup("#VboxHistory");
                    vboxHistory.getChildren().clear();

                    // Tạo formatter theo định dạng dd/MM/yyyy HH:mm:ss
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                    int index = 1;
                    for(Match match : listMatch) {
                        FXMLLoader itemHistoryLoad = new FXMLLoader(getClass().getResource("/Fxml/Client/ItemHistory.fxml"));
                        try {
                            HBox itemHistory = itemHistoryLoad.load();
                            Label lblSTT = (Label) itemHistory.getChildren().getFirst();
                            Label lblOpponent = (Label) itemHistory.getChildren().get(1);
                            Label lblTime = (Label) itemHistory.getChildren().get(2);
                            Label lblResult = (Label) itemHistory.getChildren().get(3);
                            Label lblPointChange = (Label) itemHistory.getChildren().get(4);

                            lblSTT.setText(String.valueOf(index));
                            lblOpponent.setText(match.getUser2Username());
                            lblTime.setText(String.valueOf(match.getTimestamp().format(formatter)));
                            lblResult.setText(match.getResultUser1());
                            lblPointChange.setText(String.valueOf(match.getPointsChangeUser1()));

                            index++;
                            vboxHistory.getChildren().add(itemHistory);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case ObjectWrapper.SERVER_SEND_RANKING:
                    List<PlayerHistory> leaderboard = (List<PlayerHistory>) data.getData();

                    VBox vboxRanked = (VBox) scene.lookup("#VboxRanked");
                    vboxRanked.getChildren().clear();

                    int i = 1;
                    for(PlayerHistory playerRank : leaderboard) {
                        FXMLLoader itemRankedLoad = new FXMLLoader(getClass().getResource("/Fxml/Client/ItemRank.fxml"));
                        try {
                            HBox itemRanked = itemRankedLoad.load();
                            Label lblStt = (Label) itemRanked.getChildren().getFirst();
                            Label lblPlayers = (Label) itemRanked.getChildren().get(1);
                            Label lblWins = (Label) itemRanked.getChildren().get(2);
                            Label lblLosses = (Label) itemRanked.getChildren().get(3);
                            Label lblDraws = (Label) itemRanked.getChildren().get(4);
                            Label lblAFKs = (Label) itemRanked.getChildren().get(5);
                            Label lplPoints = (Label) itemRanked.getChildren().get(6);
                            Label lblRank = (Label) itemRanked.getChildren().get(7);

                            lblStt.setText(String.valueOf(i));
                            lblPlayers.setText(playerRank.getUsername());
                            lblWins.setText(String.valueOf(playerRank.getTotalWins()));
                            lblLosses.setText(String.valueOf(playerRank.getTotalLosses()));
                            lblDraws.setText(String.valueOf(playerRank.getTotalDraw()));
                            lblAFKs.setText(String.valueOf(playerRank.getTotalAfk()));
                            lplPoints.setText(String.valueOf(playerRank.getPoints()));
                            lblRank.setText(String.valueOf(playerRank.getRanking()));
                            i++;

                            vboxRanked.getChildren().add(itemRanked);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case ObjectWrapper.SERVER_SET_GAME_READY:
                    System.out.println("SERVER_SEND_PLAY_REQUEST_ERROR");
                    break;




            }
        });
    }
}
