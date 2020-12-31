package org.teogramm.mail.client.ui.initialization;

import com.google.common.net.InetAddresses;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;


public class ServerDetailsFactory {
    /**
     * Creates a stage that requests server IP and port from the user
     * @param serverInfo Arraylist to store server information. IP is stored at index 0, port at index 1.
     * @return Created window
     */
    public static Stage createServerInfoStage(ArrayList<String> serverInfo){
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(5);
        grid.setHgap(5);

        // Informational label
        final Text information = new Text("Enter mail server information");
        GridPane.setHalignment(information,HPos.CENTER);
        GridPane.setConstraints(information,0,0);
        grid.getChildren().add(information);

        // IP address input
        final TextField serverIPInputField = new TextField();
        serverIPInputField.setPromptText("Server IP");
        serverIPInputField.setPrefColumnCount(10);
        GridPane.setConstraints(serverIPInputField,0,1);
        grid.getChildren().add(serverIPInputField);

        // Checkmark that shows if entered IP is valid
        final Text serverIPCheckmark = new Text("❌");
        GridPane.setConstraints(serverIPCheckmark,1,1);
        grid.getChildren().add(serverIPCheckmark);
        // Add a listener to the IP text field that checks if an ip is valid
        // and updates the checkmark accordingly
        serverIPInputField.textProperty().addListener((observableValue,oldValue,newValue)->{
            if(InetAddresses.isInetAddress(newValue)){
                serverIPCheckmark.setText("✔");
            }else{
                serverIPCheckmark.setText("❌");
            }
        });

        // Port input field
        final TextField portInputField = new TextField();
        portInputField.setPromptText("Server Port");
        GridPane.setConstraints(portInputField,0,2);
        grid.getChildren().add(portInputField);

        // Text that changes depending on whether entered text is a valid port number
        final Text portCheckmark = new Text("❌");
        GridPane.setConstraints(portCheckmark,1,2);
        grid.getChildren().add(portCheckmark);
        // Add a listener that checks if port is valid and updates the checkmark accordingly
        portInputField.textProperty().addListener(((observable, oldValue, newValue) -> {
            try {
                int portNumber = Integer.parseInt(newValue);
                if (portNumber >= 0 && portNumber <= 65535) {
                    portCheckmark.setText("✔");
                }else{
                    portCheckmark.setText("❌");
                }
            }catch (NumberFormatException e){
                // If text is not a number a NumberFormatException is thrown
                portCheckmark.setText("❌");
            }
        }));

        final Button ok = new Button("OK");
        GridPane.setHalignment(ok, HPos.CENTER);
        GridPane.setConstraints(ok,0,3);
        // Only allow clicking OK if IP and port are valid
        ok.disableProperty().bind(new BooleanBinding() {
            {
                // Recheck button status each time one of the checkmarks
                // is updated. This means that one field was incorrect
                // and is now correct or the opposite, so we need to enable/disable
                // the button
                bind(serverIPCheckmark.textProperty());
                bind(portCheckmark.textProperty());
            }
            @Override
            protected boolean computeValue() {
                // Returns button disabled state
                // Button is disabled if either field is wrong
                return serverIPCheckmark.getText().equals("❌") || portCheckmark.getText().equals("❌");
            }
        });



        // Create the window
        Stage s = new Stage();
        s.setScene(new Scene(grid));
        s.setTitle("Server Information");
//        s.setOnCloseRequest(event -> {
//            if(serverIPCheckmark.getText().equals("❌") || portCheckmark.getText().equals("❌")){
//                event.consume();
//            }
//        });
        // When OK is clicked add the information to the ArrayList that was given as a parameter
        // and close the window
        ok.setOnAction(event -> {
            serverInfo.add(serverIPInputField.getText());
            serverInfo.add(portInputField.getText());
            s.close();
        });
        grid.getChildren().add(ok);
        return s;
    }
}