package org.teogramm.mail.common.requests;

/**
 * This class creates MailRequest objects. Due to the different
 * data included with each request type this Factory helps ensure
 * that valid requests are crafted.
 */
public class MailRequestFactory {
    /**
     * Creates a new login request with the specified credentials.
     * @param username Username to authenticate as
     * @param password Password to authenticate with
     * @return A new MailRequest object with the given parameters
     */
    public static MailRequest createLoginRequest(String username,String password){
        // Login request does not need additional data
        return new MailRequest(username,password,RequestTypes.LOGIN_REQUEST);
    }

    /**
     * Creates a new registration request.
     * @param username Username of the registered user
     * @param password Password of the registered user
     * @param fullName Full name of the user. Must not be blank or empty.
     * @return A new registration request with the given parameters
     */
    public static MailRequest createRegisterRequest(String username,String password,String fullName){
        if(fullName.isBlank()){
            throw new IllegalArgumentException("Full Name must not be blank!");
        }

        MailRequest request = new MailRequest(username,password,RequestTypes.REGISTER_REQUEST);
        request.addData("fullName",fullName);
        return request;
    }

    /**
     * Creates a request that fetches all the mails in the specified user's mailbox.
     * @param username Username of the user whose emails will be fetched
     * @param password Password of the user whose emails will be fetched
     * @return A new MailRequest object with the given parameters
     */
    public static MailRequest createFetchMailboxRequest(String username,String password){
        return new MailRequest(username,password,RequestTypes.FETCH_MAILBOX);
    }

    /**
     * Creates a new MailRequest to show the contents of the mail with the given UUID
     * @param username Username of the user that has this email in their mailbox
     * @param password Password of the user that has this email in their mailbox
     * @param mailUuid The UUID of the mail
     * @return A new MailRequest object to show the given mail.
     */
    public static MailRequest createShowMailRequest(String username,String password,String mailUuid){
        MailRequest request = new MailRequest(username,password,RequestTypes.SHOW_MAIL);
        request.addData("uuid",mailUuid);
        return request;
    }

    /**
     * Creates a request to send a new message
     * @param username Username of sender
     * @param password Password of sender
     * @param recipient Non-blank string with the mail address of the recipient
     * @param subject Mail subject, can be blank or empty.
     * @param content Mail content, can be blank or empty
     * @return A new MailRequest object to send the mail
     */
    public static MailRequest createSendMailRequest(String username,String password,String recipient,String subject,
                                                    String content){
        if(recipient.isBlank()){
            throw new IllegalArgumentException("Recipient can't be empty");
        }
        MailRequest request = new MailRequest(username, password, RequestTypes.SEND_MAIL);
        // Sender is the user that will be authenticated
        request.addData("sender",username);
        request.addData("receiver",recipient);
        request.addData("subject",subject);
        request.addData("content",content);
        return request;
    }

    /**
     * Creates a request to delete a message
     * @param username Username of the user that has this email in their mailbox
     * @param password Password of the user that has this email in their mailbox
     * @param mailUuid UUID of the mail to be deleted
     * @return A new MailRequest object to delete the given mail
     */
    public static MailRequest createDeleteMailRequest(String username,String password,String mailUuid){
        MailRequest request = new MailRequest(username,password,RequestTypes.DELETE_MAIL);
        request.addData("uuid",mailUuid);
        return request;
    }
}
