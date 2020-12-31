package org.teogramm.mail.client.ui;

import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.teogramm.mail.client.ClientError;
import org.teogramm.mail.client.ClientWorker;
import org.teogramm.mail.client.ui.initialization.WelcomeSceneFactory;
import org.teogramm.mail.client.ui.initialization.ServerDetailsFactory;
import org.teogramm.mail.client.ui.initialization.WelcomeInterface;
import org.teogramm.mail.client.ui.main.MainScene;
import org.teogramm.mail.client.ui.utils.AlertsFactory;


import java.io.IOException;
import java.util.ArrayList;


public class MailUI extends Application {
    private static ClientWorker worker;

    public void initiate(){
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        setUp(primaryStage);
    }

    private void setUp(Stage primaryStage){
        primaryStage.setTitle("Mail client");

        ArrayList<String> serverInfo = new ArrayList<>();
        Stage info = ServerDetailsFactory.createServerInfoStage(serverInfo);
        info.initOwner(primaryStage);
        info.initModality(Modality.APPLICATION_MODAL);


        // Continue asking for server info until correct credentials have been entered
        while(true) {
            info.showAndWait();

            try {
                worker = new ClientWorker(serverInfo.get(0), serverInfo.get(1));
                break;
            } catch (IOException e) {
                AlertsFactory.createError(e.getMessage());
            } catch (IndexOutOfBoundsException e){
                // If user exits via X button terminate the program
                System.exit(1);
            } finally {
                serverInfo.clear();
            }
        }

        primaryStage.setScene(WelcomeSceneFactory.createLoginScene(new WelcomeInterface() {
            @Override
            public void login(String username, String password) {
                try {
                    if(worker.performLogin(username, password)){
                        MainScene m = new MainScene(worker, username,password);
                        primaryStage.setScene(m.createMainScene(MailUI.this));
                    }else{
                        AlertsFactory.createError("Login failed!").showAndWait();
                    }
                } catch (ClientError|IllegalArgumentException clientError) {
                    AlertsFactory.createError(clientError.getMessage()).showAndWait();
                }
            }

            @Override
            public void register(String username, String password, String fullName) {
                try{
                    if(worker.performRegister(username, password, fullName)){
                        AlertsFactory.createInfo("Success registering. You can now log in.").showAndWait();
                    }
                } catch (ClientError clientError) {
                    AlertsFactory.createError(clientError.getMessage()).showAndWait();
                }
            }
        }));
        primaryStage.show();
    }

    /**
     * Resets the application using the given stage
     */
    public void reset(Stage stage){
        worker = null;
        setUp(stage);
    }
}