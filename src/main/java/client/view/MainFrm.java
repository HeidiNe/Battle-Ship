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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import kotlin.Pair;
import shared.dto.ObjectWrapper;
import javafx.application.Platform;
import shared.dto.PlayerHistory;
import shared.model.Match;
import shared.model.Player;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class MainFrm {

    private ClientCtr mySocket = ClientCtr.getInstance();
    private Stage stage = mySocket.getStage();
    MediaPlayer backgroundMusicPlayer;

    public MainFrm() {
    }

    public void openScene() {
        // Khởi tạo âm thanh trước
        initializeBackgroundMusic();

        // Sau đó xử lý UI trong Platform.runLater
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

                btnHome.setStyle("-fx-text-fill: rgba(234, 251, 2, 0.7);");
                btnHistory.setStyle("-fx-text-fill: rgba(217, 223, 165, 0.7);");
                btnRanked.setStyle("-fx-text-fill: rgba(217, 223, 165, 0.7);");

                // Xu ly su kien click vao button Home
                btnHome.setOnAction(event -> {
                    try {
                        home.setVisible(true);
                        history.setVisible(false);
                        ranked.setVisible(false);

                        btnHome.setStyle("-fx-text-fill: rgba(234, 251, 2, 0.7);");
                        btnHistory.setStyle("-fx-text-fill: rgba(217, 223, 165, 0.7);");
                        btnRanked.setStyle("-fx-text-fill: rgba(217, 223, 165, 0.7);");

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

                        btnHome.setStyle("-fx-text-fill: rgba(217, 223, 165, 0.7);");
                        btnHistory.setStyle("-fx-text-fill: rgba(234, 251, 2, 0.7);");
                        btnRanked.setStyle("-fx-text-fill: rgba(217, 223, 165, 0.7);");

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

                        btnHome.setStyle("-fx-text-fill: rgba(217, 223, 165, 0.7);");
                        btnHistory.setStyle("-fx-text-fill: rgba(217, 223, 165, 0.7);");
                        btnRanked.setStyle("-fx-text-fill: rgba(234, 251, 2, 0.7);");

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


                    HashMap<String, String> mapUserStatus = new HashMap<>();
                    ArrayList<String> listUserStatus = new ArrayList<>();

                    // dua cac user co o giao dien vao trong hashmap
                    for (int i = 0; i < screnListUserWaiting.getChildren().size(); i++) {
                        // lay ra item user
                        HBox itemUser = (HBox) screnListUserWaiting.getChildren().get(i);
                        AnchorPane anchorPane = (AnchorPane) itemUser.getChildren().getFirst();
                        Label lblUserName = (Label) anchorPane.lookup("#usernameItem");

                        // dua vao trong map va list
                        listUserStatus.add(lblUserName.getText());
                        mapUserStatus.put(lblUserName.getText(), "Offline");

                    }


                    // Cap nhat lai trang thai cua cua user
                    // Kiem tra xem co user moi nao khong (truong hop co user nao do moi tao tai khoan nen khong biet)
                    for (Player player : listUserWaiting) {
                        if (!mapUserStatus.containsKey(player.getUsername()) && !player.getUsername().equals(mySocket.getUsername())) listUserStatus.add(player.getUsername());

                        if(player.getStatus().equals("Online")) mapUserStatus.put(player.getUsername(), "Online");
                        else mapUserStatus.put(player.getUsername(), "In Game");
                    }

                    // sort user theo status, name
                    listUserStatus.sort((o1, o2) -> {
                        String status01 = mapUserStatus.get(o1);
                        String status02 = mapUserStatus.get(o2);
                        if (status01.equals(status02)) return o1.compareTo(o2);
                        if(status01.equals("Online")) return -1;
                        if(status01.equals("In Game") && status02.equals("Offline")) return -1;
                        return 1;
                    });

                    // cap nhat lai giao dien
                    screnListUserWaiting.getChildren().clear();
                    for (String namePlayer : listUserStatus){
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/ItemUser.fxml"));
                        try {
                            HBox itemUser = loader.load();
                            Label lblUserName = (Label) loader.getNamespace().get("usernameItem");
                            Label lblStatus = (Label) loader.getNamespace().get("statusItem");
                            Circle circleStatus = (Circle) loader.getNamespace().get("circleStatus");
                            Button btnInvite = (Button) itemUser.lookup("#buttonInvite");

                            lblUserName.setText(namePlayer);
                            lblStatus.setText(mapUserStatus.get(namePlayer));

                            if(mapUserStatus.get(namePlayer).equals("Online")) {
                                circleStatus.setStyle("-fx-stroke: #00ff00");
                                lblStatus.setStyle("-fx-text-fill: #4a8f4a");
                                btnInvite.setVisible(true);

                                btnInvite.setOnAction(event -> {
                                    mySocket.sendData(new ObjectWrapper(ObjectWrapper.SEND_PLAY_REQUEST, namePlayer));
                                });
                            } else if(mapUserStatus.get(namePlayer).equals("In Game")) {
                                circleStatus.setStyle("-fx-stroke: #584ee6");
                                lblStatus.setStyle("-fx-text-fill: #302a8a");
                                btnInvite.setVisible(false);
                            }
                            else {
                                circleStatus.setStyle("-fx-stroke: #4f4f50");
                                lblStatus.setStyle("-fx-text-fill: #a3a3a3");
                                btnInvite.setVisible(false);
                            }
                            screnListUserWaiting.getChildren().add(itemUser);
                        } catch (Exception e) {
                            e.printStackTrace();
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
                            VBox vBox = itemHistoryLoad.load();
                            HBox itemHistory = (HBox) vBox.getChildren().getFirst();
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
                    if (mySocket.getSetShipFrm() == null) {
                        SetShipFrm setShipFrm = new SetShipFrm();
                        mySocket.setSetShipFrm(setShipFrm);
                    }
                    mySocket.getSetShipFrm().openScene();
                    backgroundMusicPlayer.stop();
                    break;

            }
        });

    }

    public void initializeBackgroundMusic(){
        // tao am thanh nen
        String backgroundMusicFile = new File("src/main/resources/Sounds/backgroundMusic.mp3").toURI().toString();
        Media backgroundMusic = new Media(backgroundMusicFile);
        backgroundMusicPlayer = new MediaPlayer(backgroundMusic);

        backgroundMusicPlayer.setVolume(0.2);
        backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        // Thêm error handler
        backgroundMusicPlayer.setOnError(() -> {
            System.out.println("Media error occurred: " + backgroundMusicPlayer.getError());
        });

        // Thêm status listener để debug
        backgroundMusicPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Status changed from " + oldValue + " to " + newValue);
        });

        backgroundMusicPlayer.play();
    }
}
