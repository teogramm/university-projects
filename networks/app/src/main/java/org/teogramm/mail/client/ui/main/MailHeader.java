package org.teogramm.mail.client.ui.main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.teogramm.mail.common.Mail;

import java.util.UUID;

/**
 * Helper class that contains information about an email. Is used when
 * displaying an email in the email list.
 */
public class MailHeader {
    private final StringProperty from;
    private final StringProperty subject;
    private final StringProperty time;
    private final UUID uuid;
    private final BooleanProperty isNew;

    public MailHeader(Mail mail){
        this.from = new SimpleStringProperty(mail.getSender());
        this.subject = new SimpleStringProperty(mail.getSubject());
        this.time = new SimpleStringProperty(mail.getTime().toString().replace('T',' '));
        this.uuid = mail.getUuid();
        this.isNew = new SimpleBooleanProperty(mail.isNew());
    }

    public String getFrom() {
        return from.get();
    }

    public StringProperty fromProperty(){
        return from;
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public StringProperty timeProperty() {
        return time;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BooleanProperty isNewProperty(){
        return isNew;
    }
}
