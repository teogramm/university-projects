package org.teogramm.mail.client.ui.main;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.teogramm.mail.client.ClientError;
import org.teogramm.mail.client.ClientWorker;
import org.teogramm.mail.client.ui.utils.AlertsFactory;


public class NewMessageFactory {
    /**
     * @param clientWorker The clientworker that will be used to send the message
     * @param password,username Credentials used for authentication
     * @return A window that allows the user to compose and send a mail message
     */
    public static Stage createNewMessageStage(ClientWorker clientWorker,String username,String password){
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));

        // Header fields
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5,0,5,0));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        Text toLabel = new Text("To");
        gridPane.add(toLabel,0,0);
        TextField toField = new TextField();
        gridPane.add(toField,1,0);

        Text subjectLabel = new Text("Subject");
        gridPane.add(subjectLabel,0,1);
        TextField subjectField = new TextField();
        gridPane.add(subjectField,1,1);

        vBox.getChildren().add(gridPane);

        // Email content field
        TextField content = new TextField();
        // Allow content to grow and fill space
        VBox.setVgrow(content, Priority.ALWAYS);
        vBox.getChildren().add(content);

        // Send button
        Button send = new Button("Send");
        send.setOnAction(event -> {
            try{
                boolean sent = clientWorker.performSendMail(username,password,toField.getText(),subjectField.getText(),content.getText());
                if(sent){
                    AlertsFactory.createInfo("Message sent!").showAndWait();
                    vBox.getScene().getWindow().hide();
                }else{
                    AlertsFactory.createError("Message could not be sent.").showAndWait();
                }
            } catch (ClientError|IllegalArgumentException clientError) {
                AlertsFactory.createError(clientError.getMessage()).showAndWait();
            }
        });
        vBox.getChildren().add(send);

        // Create the window and set the scene
        Stage s = new Stage();
        s.setScene(new Scene(vBox));
        return s;
    }
}
