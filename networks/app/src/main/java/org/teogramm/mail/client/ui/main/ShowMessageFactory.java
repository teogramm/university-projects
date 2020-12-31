package org.teogramm.mail.client.ui.main;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.teogramm.mail.common.Mail;

public class ShowMessageFactory {

    /**
     * Creates a window that displays the contents of the given mail
     */
    public static Stage createMailStage(Mail m){
        // Create VBox for mail headers and mail content
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(15);

        // Create Grid layout for email headers
        GridPane headerGrid = new GridPane();
        headerGrid.setPadding(new Insets(10));
        headerGrid.setVgap(20);
        headerGrid.setHgap(10);

        Text fromLabel = new Text("From:");
        headerGrid.add(fromLabel,0,0);
        Text fromValue = new Text(m.getSender());
        headerGrid.add(fromValue,1,0);

        Text toLabel = new Text("To:");
        headerGrid.add(toLabel,0,1);
        Text toValue = new Text(m.getReceiver());
        headerGrid.add(toValue,1,1);

        Text subjectLabel = new Text("Subject");
        headerGrid.add(subjectLabel,0,2);
        Text subjectValue = new Text(m.getSubject());
        headerGrid.add(subjectValue,1,2);

        Text timeValue = new Text(m.getTime().toString().replace('T',' '));
        GridPane.setHalignment(timeValue, HPos.RIGHT);
        GridPane.setMargin(timeValue,new Insets(0,0,0,100));
        headerGrid.add(timeValue,2,0);

        // Add the header grid to the vBox
        vBox.getChildren().add(headerGrid);

        // Create the email content field
        Text content = new Text(m.getMailBody());
        VBox.setVgrow(content, Priority.ALWAYS);
        vBox.getChildren().add(content);

        // Create a new window and set the VBox as its contents
        Stage s = new Stage();
        s.setTitle(m.getSubject());
        s.setScene(new Scene(vBox));

        return s;
    }
}
