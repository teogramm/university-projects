package org.teogramm.mail.common;


import java.time.LocalDateTime;
import java.util.UUID;

public class Mail {
    private boolean isNew = true;
    private final String sender;
    private final String receiver;
    private final String subject;
    private final String mailBody;
    private LocalDateTime time;
    private final UUID uuid;

    /**
     * Creates a new unread mail object with a random UUID
     * @param sender A not empty email address
     * @param receiver A not empty email address
     * @param subject Can be empty
     * @param mailBody Can be empty
     */
    public Mail(String sender,String receiver,String subject,String mailBody){
        this(sender,receiver,subject,mailBody, UUID.randomUUID());
    }

    public Mail(String sender,String receiver,String subject,String mailBody,UUID uuid){
        if(sender.isBlank() || receiver.isBlank()){
            throw new IllegalArgumentException("Sender and receiver cannot be blank.");
        }

        if(subject == null || mailBody == null){
            throw new IllegalArgumentException("Subject and mailBody must not be null");
        }

        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.mailBody = mailBody;

        this.time = null;
        this.uuid = uuid;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public String getMailBody() {
        return mailBody;
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Sets the time the email was processed only
     * if time has not been set (is null).
     */
    public void setTime(LocalDateTime time){
        if(this.time == null) {
            this.time = time;
        }
    }

    /**
     * Marks the email as read.
     */
    public void read(){
        isNew = false;
    }

    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Returns true if this mail has not been read
     * @return true if this mail has not been read
     */
    public boolean isNew() {
        return isNew;
    }
}