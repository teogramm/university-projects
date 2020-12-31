package org.teogramm.mail.client.ui.main;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.teogramm.mail.client.ClientError;
import org.teogramm.mail.client.ClientWorker;
import org.teogramm.mail.client.ui.MailUI;
import org.teogramm.mail.client.ui.utils.AlertsFactory;
import org.teogramm.mail.common.Mail;

/**
 * Class for the main interface that allows the user to see their emails
 * and perform actions.
 */
public class MainScene {
    private final ClientWorker clientWorker;
    private final String username;
    private final String password;
    private TableView<MailHeader> mailbox;

    public MainScene(ClientWorker clientWorker, String username,String password){
        this.clientWorker = clientWorker;
        this.username = username;
        this.password = password;
    }


    /**
     * Creates the scene displaying the user's emails and all options.
     * @param app The MailUI object that created the main scene. Used for logout operation
     */
    public Scene createMainScene(MailUI app){
        // We have an HBox for the available actions
        // and a VBox to manage the action bar and the email display
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // Create the action bar
        HBox actions = new HBox();
        actions.setSpacing(10);
        Button send = new Button("Send Message");
        Button delete = new Button("Delete message");
        Button refresh = new Button("Refresh");
        Button logout = new Button("Logout");
        actions.getChildren().addAll(send,delete,refresh,logout);

        send.setOnAction(event -> NewMessageFactory.createNewMessageStage(clientWorker,username,password).show());

        // Logout button calls the reset method on the parent application
        logout.setOnAction(event -> app.reset((Stage) vBox.getScene().getWindow()));

        // If the user hits refresh just recreate tableview
        refresh.setOnAction(event -> {
            mailbox = createMailTableView();
            vBox.getChildren().set(1,mailbox);
        });

        // Create the table listing all the mails
        vBox.getChildren().add(actions);
        mailbox = createMailTableView();
        vBox.getChildren().add(mailbox);
        VBox.setVgrow(mailbox, Priority.ALWAYS);

        // When the user clicks delete get the current selected email
        // and make a deletion request
        delete.setOnAction(event -> {
            if(mailbox.getSelectionModel().getSelectedItem() != null){
                MailHeader header = mailbox.getSelectionModel().getSelectedItem();
                try{
                    boolean success = clientWorker.performDeleteMail(username,password,header.getUuid());
                    if(success){
                        AlertsFactory.createInfo("Delete successful.");
                        // Refresh the table
                        mailbox = createMailTableView();
                        vBox.getChildren().set(1,mailbox);
                    }else{
                        AlertsFactory.createError("Message could not be deleted");
                    }
                } catch (ClientError clientError) {
                    AlertsFactory.createError(clientError.getMessage()).showAndWait();
                }
            }else{
                AlertsFactory.createError("You must select a message").showAndWait();
            }
        });

        return new Scene(vBox);
    }

    /**
     * Creates the TableView that shows the mails in the user's mailbox
     * @return A TableView containing all emails
     */
    public TableView<MailHeader> createMailTableView(){
        TableView<MailHeader> mailsTable = new TableView<>();
        ObservableList<MailHeader> mailList =FXCollections.emptyObservableList();
        try {
            mailList = clientWorker.performFetchMail(username, password);
        } catch (ClientError clientError) {
            AlertsFactory.createError(clientError.getMessage()).showAndWait();
        }
        mailsTable.setItems(mailList);

        // Add double click action
        mailsTable.setRowFactory(tv->{
            // Make row bold if message is unread
            final TableRow<MailHeader> row = new TableRow<>() {
                @Override
                protected void updateItem(MailHeader row, boolean empty) {
                    super.updateItem(row, empty);
                    if (!empty) {
                        styleProperty().bind(Bindings.when(row.isNewProperty())
                                .then("-fx-font-weight: bold;")
                                .otherwise(""));

                    }
                }
            };

            // Open showMail window on doubleclick
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !(row.isEmpty())){
                    // Get the mail from the server
                    MailHeader mailHeader = row.getItem();
                    Mail m;
                    try{
                        m = clientWorker.performShowMail(username,password,mailHeader.getUuid());
                    } catch (ClientError clientError) {
                        AlertsFactory.createError("Error getting mail. Maybe the mail has been deleted.").showAndWait();
                        return;
                    }
                    // Create and show the mail window
                    Stage mailWindow = ShowMessageFactory.createMailStage(m);
                    mailWindow.initOwner(row.getScene().getWindow());
                    mailWindow.initModality(Modality.NONE);
                    mailWindow.show();
                }
            });
            return row;
        });

        TableColumn<MailHeader,String> from = new TableColumn<>("From");
        from.setCellValueFactory(new PropertyValueFactory<>("from"));

        TableColumn<MailHeader,String> subject = new TableColumn<>("Subject");
        subject.setCellValueFactory(new PropertyValueFactory<>("subject"));

        TableColumn<MailHeader,String> time = new TableColumn<>("Time");
        time.setCellValueFactory(new PropertyValueFactory<>("time"));

        mailsTable.getColumns().add(from);
        mailsTable.getColumns().add(subject);
        mailsTable.getColumns().add(time);

        mailsTable.autosize();


        return mailsTable;
    }
}
