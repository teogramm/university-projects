package org.teogramm.mail.client.ui.utils;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Class used to create alerts
 */
public class AlertsFactory {

    /**
     * Creates an error dialog
     * @param message The message that appears inside the dialog
     * @return Alert object with the given error message
     */
    public static Alert createError(String message){
        return createGeneric(message,Alert.AlertType.ERROR);
    }

    /**
     * Creates an information dialog
     * @param message The message that appears inside the dialog
     * @return Alert object with the given information message
     */
    public static Alert createInfo(String message){
        return createGeneric(message,Alert.AlertType.INFORMATION);
    }

    /**
     * Create a dialog
     * @param message Message that appears inside the dialog
     * @param type Type of dialog.
     * @return An Alert object of the given type and with the given message
     */
    private static Alert createGeneric(String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        ((Stage) alert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        return alert;
    }
}
