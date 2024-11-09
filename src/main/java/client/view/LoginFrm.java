package client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;

import shared.dto.ObjectWrapper;
import client.controller.ClientCtr;
import shared.model.Player;

public class LoginFrm extends Application {

    private FXMLLoader fxmlLoader;
    private ClientCtr mySocket = ClientCtr.getInstance();

    public LoginFrm() {
        fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/Login.fxml"));
    }


    @Override
    public void start(Stage stage) throws Exception {

        mySocket.setStage(stage);

        try {
            Scene scene = new Scene(fxmlLoader.load());
            mySocket.setLoginScreen(scene);

            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

            // JavaFX UI Controls
            TextField usernameLoginTextField = (TextField) fxmlLoader.getNamespace().get("usernameLoginTextField");
            PasswordField passwordLoginField = (PasswordField) fxmlLoader.getNamespace().get("passwordLoginField");
            Button loginButton = (Button) fxmlLoader.getNamespace().get("loginButton");
            Button signupButton = (Button) fxmlLoader.getNamespace().get("signupButton");

            // Set up click event handlers
            loginButton.setOnAction(event -> {
                // Handle login button click
                String username = usernameLoginTextField.getText();
                String password = passwordLoginField.getText();

                // Process login logic
                Player player = new Player(username, password);
                mySocket.sendData(new ObjectWrapper(ObjectWrapper.LOGIN_USER, player));
            });

            signupButton.setOnAction(event -> {
                if (mySocket.getRegisterFrm() == null) {
                    RegisterFrm registerFrm = new RegisterFrm();
                    mySocket.setRegisterFrm(registerFrm);
                }
                mySocket.getRegisterFrm().openScene();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void openScene (){
        launch();
    }

    public void receivedDataProcessing(ObjectWrapper data) {
        Platform.runLater(() -> {
            String result = (String) data.getData();
            Scene loginScreen = mySocket.getLoginScreen();
            if (result.equals("false")) {
                // txtResult.setText("Error: Incorrect username or password.");
            } else {
                TextField usernameLoginTextField = (TextField) loginScreen.lookup("#usernameLoginTextField");
                String username = usernameLoginTextField.getText();

                mySocket.setUsername(username);
                System.out.println(username);

                mySocket.sendData(new ObjectWrapper(ObjectWrapper.LOGIN_SUCCESSFUL, mySocket.getUsername()));

                if (mySocket.getMainFrm() == null) {
                    MainFrm mainFrm = new MainFrm();
                    mySocket.setMainFrm(mainFrm);
                }
                mySocket.getMainFrm().openScene();
            }
        });
    }
}
