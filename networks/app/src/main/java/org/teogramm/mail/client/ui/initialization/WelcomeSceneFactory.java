package org.teogramm.mail.client.ui.initialization;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;


/**
 * Creates scene that appears after a connection to a server
 * has been made. It allows the user to either login or register
 * to the server.
 */
public class WelcomeSceneFactory {
    /**
     * @param i WelcomeInterface object that will be called when user tries to
     *          perform an action (register/login)
     * @return The created scene
     * @see WelcomeInterface
     */
    public static Scene createLoginScene(WelcomeInterface i){
        GridPane grid = new GridPane();

        Text welcomeText = new Text("Welcome to mail");
        GridPane.setHalignment(welcomeText, HPos.CENTER);
        GridPane.setConstraints(welcomeText,0,0);
        grid.getChildren().add(welcomeText);

        GridPane register = createRegister(i);
        GridPane.setConstraints(register,0,1);
        grid.getChildren().add(register);

        GridPane login = createLogin(i);
        GridPane.setConstraints(login,1,1);
        grid.getChildren().add(login);

        return new Scene(grid);
    }

    /**
     * Creates a scene with the required fields for registration
     * @param i WelcomeInterface object that is called when the user tries to register
     */
    private static GridPane createRegister(WelcomeInterface i){
        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setPadding(new Insets(10));

        Text registerText = new Text("Register");
        GridPane.setHalignment(registerText,HPos.CENTER);
        GridPane.setConstraints(registerText,0,0);

        TextField fullName = new TextField();
        fullName.setPromptText("Full name");
        fullName.setPrefColumnCount(10);
        GridPane.setConstraints(fullName,0,1);

        TextField userName = new TextField();
        userName.setPromptText("Username");
        userName.setPrefColumnCount(10);
        GridPane.setConstraints(userName,0,2);

        TextField password = new PasswordField();
        password.setPromptText("Password");
        password.setPrefColumnCount(10);
        GridPane.setConstraints(password,0,3);

        Button registerButton = new Button("Register");
        GridPane.setHalignment(registerButton,HPos.CENTER);
        GridPane.setConstraints(registerButton,0,4);
        // When the user tries to register call the method in the WelcomeInterface
        // given as parameter.
        registerButton.setOnAction(event -> i.register(userName.getText(),password.getText(),fullName.getText()));

        grid.getChildren().addAll(registerText,fullName,userName,password,registerButton);

        return grid;
    }

    /**
     * Creates a scene with the required fields for login
     * @param i WelcomeInterface object that is called when the user tries to login
     */
    private static GridPane createLogin(WelcomeInterface i){
        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setPadding(new Insets(10));

        Text loginText = new Text("Login");
        GridPane.setHalignment(loginText,HPos.CENTER);
        GridPane.setConstraints(loginText,0,0);

        TextField userName = new TextField();
        userName.setPromptText("Username");
        userName.setPrefColumnCount(10);
        GridPane.setConstraints(userName,0,1);

        TextField password = new PasswordField();
        password.setPromptText("Password");
        password.setPrefColumnCount(10);
        GridPane.setConstraints(password,0,2);

        Button loginButton = new Button("Login");
        GridPane.setHalignment(loginButton,HPos.CENTER);
        GridPane.setConstraints(loginButton,0,3);
        // When the user tries to login call the methods in the WelcomeInterface
        // given as parameter.
        loginButton.setOnAction(event -> i.login(userName.getText(),password.getText()));

        grid.getChildren().addAll(loginText,userName,password,loginButton);

        return grid;
    }
}
