package client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

import shared.dto.ObjectWrapper;
import client.controller.ClientCtr;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import shared.model.Player;

public class PlayFrm {
    private Timeline TimeCD;
    private ClientCtr mySocket = ClientCtr.getInstance();
    private Stage stage = mySocket.getStage();
    private HashSet<Pane> buttonEnemyShooted = new HashSet<>();
    private boolean playerTurn = false;
    private ArrayList<String> shipsLocation = new ArrayList<>();
    public PlayFrm(ArrayList<String> shipsLocation, boolean playerTurn ) {
        this.playerTurn = playerTurn;
        this.shipsLocation = shipsLocation;
    }

    public void openScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Play.fxml"));
            Scene scene = new Scene(loader.load());
            mySocket.setPlayScene(scene);
            
            // draw grid
            GridPane enemyGrid = (GridPane) scene.lookup("#enemygrid");
            GridPane myGrid = (GridPane) scene.lookup("#mygrid");
            
            createGrid(enemyGrid);
            createGrid(myGrid);
            
            drawMyShips();
            
            stage.setScene(scene);
            stage.setTitle("Set Ship");
            stage.show();
            
            if (playerTurn) {
                startYourTurn();
            } else {
                startEnemyTurn();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createGrid(GridPane grid) {
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
    
        grid.setPrefSize(350, 350);
        for (int i = 0; i < 10; i++) {
            ColumnConstraints colConst = new ColumnConstraints(35);
            grid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < 10; i++) {
            RowConstraints rowConst = new RowConstraints(35);
            grid.getRowConstraints().add(rowConst);
        }
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                Pane cellPane = new Pane();
                cellPane.setPrefSize(35, 35); 
                cellPane.setId(x + "" + y); 
                cellPane.setStyle("-fx-border-color: black; -fx-background-color: lightblue;");
                if(grid.getId().compareToIgnoreCase("enemygrid") == 0){
                    cellPane.setOnMouseClicked(event -> handleGridClick(event, cellPane));
                }
                grid.add(cellPane, x, y);
            }
        }
    }

    private void drawMyShips() {
        ArrayList<String> currentShip = new ArrayList<>();
        GridPane myGrid = (GridPane) mySocket.getPlayScene().lookup("#mygrid");
        for (String loc : shipsLocation) {
            if (loc.equals("/")) {
                if (!currentShip.isEmpty()) {
                    drawShip(currentShip, "#mygrid");
                    currentShip = new ArrayList<>();
                }
                currentShip = new ArrayList<>();
            } else {
                currentShip.add(loc);
            }
        }
        // Vẽ con tàu cuối cùng nếu có
        if (!currentShip.isEmpty()) {
            drawShip(currentShip,"#mygrid");
        }
    }
    
    
    private void drawShip(ArrayList<String> currentShip, String idGrid){
        int startRow = currentShip.get(0).charAt(1) - '0';
        int startCol = currentShip.get(0).charAt(0) - '0';
        int endRow = currentShip.get(currentShip.size() - 1).charAt(1) - '0';
        boolean isHorizontal = (startRow == endRow);

        GridPane gridPane = (GridPane) mySocket.getPlayScene().lookup(idGrid);
        String shipImagePath = "/Images/ship" + currentShip.size() + ".png";
        
        if (!isHorizontal) {
            shipImagePath= "/Images/ship" + currentShip.size()+"-ver" + ".png";
        }
        Image shipImage = new Image(shipImagePath);
        ImageView shipImageView = new ImageView(shipImage);
        if(isHorizontal){
            shipImageView.setFitWidth(35 * currentShip.size());
            shipImageView.setFitHeight(32);
        }else{
            shipImageView.setFitWidth(32);
            shipImageView.setFitHeight(35 * currentShip.size());
        }
        Pane shipPane = new Pane();
        shipPane.getChildren().add(shipImageView);
        
        gridPane.add(shipPane, startCol, startRow);

        if (isHorizontal) {
            GridPane.setColumnSpan(shipPane, currentShip.size()); 
        } else {
            GridPane.setRowSpan(shipPane, currentShip.size()); 
        }
    }
    private void handleGridClick(MouseEvent e, Pane cell) {
        System.out.println("clicked  "+ cell.getId());
<<<<<<< HEAD

=======
>>>>>>> main
        cell.setDisable(true);
        String location = cell.getId();
        buttonEnemyShooted.add(cell);
        System.out.println("Ban gui di---"+ swapLocation(location));
        TimeCD.stop();
<<<<<<< HEAD
        mySocket.sendData(new ObjectWrapper(ObjectWrapper.SHOOT_REQUEST, swapLocation(location)));
=======
        mySocket.sendData(new ObjectWrapper(ObjectWrapper.SHOOT_REQUEST, location));
>>>>>>> main
    }
    
    private void drawHit(String position, String idGrid){
        int row = position.charAt(1) - '0';
        int col = position.charAt(0) - '0';
        String shipImagePath = "/Images/hit.png";
        Image shipImage = new Image(shipImagePath);
        ImageView shipImageView = new ImageView(shipImage);
        shipImageView.setFitHeight(35);
        shipImageView.setFitWidth(35);
        GridPane gridPane = (GridPane) mySocket.getPlayScene().lookup(idGrid);
        
        Pane shipPane = new Pane();
        
        shipPane.getChildren().add(shipImageView);
        
        gridPane.add(shipPane, col, row);    
        shipPane.setDisable(true);
    }
    private void drawMiss(String position, String idGrid){
        int row = position.charAt(1) - '0';
        int col = position.charAt(0) - '0';
        String shipImagePath = "/Images/failure.png";
        Image shipImage = new Image(shipImagePath);
        ImageView shipImageView = new ImageView(shipImage);
        shipImageView.setFitHeight(35);
        shipImageView.setFitWidth(35);
        GridPane gridPane = (GridPane) mySocket.getPlayScene().lookup(idGrid);
        
        Pane shipPane = new Pane();
        
        shipPane.getChildren().add(shipImageView);
        
        gridPane.add(shipPane, col, row);    
        shipPane.setDisable(true);
    }
    private void drawDestroyedShip(String[] positions, String idGrid){
        ArrayList<String> ships = new ArrayList<>(Arrays.asList(positions));
        ships = swapshipsLocation(ships);
        drawShip(ships,idGrid);
        for(String ship : ships){
            drawHit(ship, idGrid);
        }

    }
    private void startYourTurn(){
        GridPane enemyGrid = (GridPane) mySocket.getPlayScene().lookup("#enemygrid");
        enemyGrid.setDisable(false);
        Label enemyMsg = (Label) mySocket.getPlayScene().lookup("#enemyMsg");
        enemyMsg.setVisible(false);
        Label yourMsg = (Label) mySocket.getPlayScene().lookup("#myMsg");
        yourMsg.setVisible(true);
        yourMsg.setText("Your Turn!");
        TimeCD = setCountDownTime(16);
        playerTurn = true;
    }
    private void startEnemyTurn(){
        GridPane enemyGrid = (GridPane) mySocket.getPlayScene().lookup("#enemygrid");
        enemyGrid.setDisable(true);
        Label enemyMsg = (Label) mySocket.getPlayScene().lookup("#enemyMsg");
        enemyMsg.setVisible(true);
        enemyMsg.setText("Your Enemy Turn!");
        Label yourMsg = (Label) mySocket.getPlayScene().lookup("#myMsg");
        yourMsg.setVisible(false);
        TimeCD = setCountDownTime(16);
        playerTurn = false;
    }
    private Timeline  setCountDownTime(int time) {
       final int[] timeRemaining = {time};
       final Timeline[] timelineWrapper = new Timeline[1]; // Sử dụng mảng để bọc Timeline

       timelineWrapper[0] = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
           timeRemaining[0]--;

           Label lblTime = (Label) mySocket.getPlayScene().lookup("#clock");
           lblTime.setText(String.valueOf(timeRemaining[0]));

           // Kiểm tra khi hết giờ và dừng Timeline
           if (timeRemaining[0] <= 0) {
               timelineWrapper[0].stop(); // Dừng Timeline khi hết thời gian
           }
       }));
       timelineWrapper[0].setCycleCount(Timeline.INDEFINITE); // Đặt để lặp vô hạn
       timelineWrapper[0].play(); // Bắt đầu bộ đếm thời gian

       return timelineWrapper[0]; // Trả về Timeline để có thể điều khiển từ bên ngoài
   }
    
    
    public void receivedDataProcessing(ObjectWrapper data) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            switch (data.getPerformative()) {
                case ObjectWrapper.SERVER_TRANSFER_SHOOT_FAILTURE:
                     playSound("missing.wav");
                     System.out.println("DU LIEU BAN HUT   "+ swapLocation((String) data.getData()));
                    if (playerTurn) {
                        System.out.println("client nhan "+ swapLocation((String) data.getData()));
                        drawMiss(swapLocation((String) data.getData()) , "#enemygrid");
                        startEnemyTurn();
                    } else {
                        drawMiss(swapLocation((String) data.getData()), "#mygrid");
                        startYourTurn();
                    }
                    break;
                case ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_POINT:
                    playSound("score.wav");
                    System.out.println("DU LIEU BAN Trung--   "+ swapLocation((String) data.getData()));
                    if (playerTurn) {
                        drawHit(swapLocation((String) data.getData()), "#enemygrid");
                        startYourTurn();
                    } else {
                        drawHit(swapLocation((String) data.getData()), "#mygrid");
                        startEnemyTurn();
                    }
                    break;
                case ObjectWrapper.SERVER_TRANSFER_SHOOT_HIT_SHIP:
                    String[] ship = (String[]) data.getData();
                    playSound("fall.wav");
                    if (playerTurn) {
                        drawDestroyedShip(ship, "#enemygrid");
                        startYourTurn();
                    } else {
                        drawDestroyedShip(ship, "#mygrid");
                        startEnemyTurn();
                    }
                    break;
                case ObjectWrapper.SERVER_TRANSFER_SHOOT_MISS_TURN:
                    if (playerTurn) {
                        startEnemyTurn();
                    } else {
                        startYourTurn();
                    }
                    break;
                case ObjectWrapper.SERVER_TRANSFER_END_GAME:
                    if (playerTurn) {
                        playSound("victory.wav");
                        drawDestroyedShip((String[]) data.getData(), "#enemygrid");
                        TimeCD.stop();
                    } else {
                        playSound("defeat.wav");
                        drawDestroyedShip((String[]) data.getData(), "#mygrid");
                        TimeCD.stop();
                    }
                    alert.setTitle("Kết thúc trận đấu");
                    alert.setHeaderText(null);
                    alert.setContentText("Trận đấu đã kết thúc, nhấn OK để xem kết quả");
                    alert.showAndWait();
//                    ResultFrm resultFrm = new ResultFrm(mySocket);
//                    mySocket.setResultFrm(resultFrm);
//
//                    mySocket.getResultFrm().setVisible(true);
//                    this.dispose();
                    break;
                case ObjectWrapper.SERVER_TRANSFER_END_GAME_DRAW:
                    playSound("draw.wav");
                    TimeCD.stop();
                    alert.setTitle("Kết thúc trận đấu");
                    alert.setHeaderText(null);
                    alert.setContentText("Trận đấu đã kết thúc, nhấn OK để xem kết quả");
                    alert.showAndWait();
//                    ResultFrm resultFrm2 = new ResultFrm(mySocket);
//                    mySocket.setResultFrm(resultFrm2);
//
//                    mySocket.getResultFrm().setVisible(true);
//                    this.dispose();
                    break;
                case ObjectWrapper.SERVER_TRANSFER_QUIT_WHEN_PLAY:
                    TimeCD.stop();
                    alert.setTitle("Kết thúc trận đấu");
                    alert.setHeaderText(null);
                    alert.setContentText("Đối thủ đã rời đi, nhấn OK để xem kết quả");
                    alert.showAndWait();
//                    ResultFrm resultFrm1 = new ResultFrm(mySocket);
//                    mySocket.setResultFrm(resultFrm1);
//
//                    mySocket.getResultFrm().setVisible(true);
//                    this.dispose();
                    break;
            }
        });
    }
    private ArrayList<String> swapshipsLocation(ArrayList<String> locationArr){
        ArrayList<String> tmp = new ArrayList<>();
        for(String pos : locationArr){
            if(pos.compareTo("/") == 0){
                tmp.add(pos);
            }else{
                tmp.add(pos.charAt(1) +"" + pos.charAt(0));
            }
        }
        return tmp;
    }
    private String swapLocation(String loc){
       return loc.charAt(1) +"" + loc.charAt(0);
    }
    private void playSound(String soundFileName) {
        try {
            // Sử dụng ClassLoader để nạp file âm thanh từ thư mục resources
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream("Sounds/" + soundFileName);
            if (audioSrc == null) {
                System.out.println("File không tồn tại: " + soundFileName);
                return;
            }

            // Đọc audio từ InputStream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
