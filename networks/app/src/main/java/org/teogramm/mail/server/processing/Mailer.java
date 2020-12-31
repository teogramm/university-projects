package org.teogramm.mail.server.processing;

import com.google.gson.Gson;
import org.teogramm.mail.common.Mail;
import org.teogramm.mail.common.serialization.CustomSerializerFactory;
import org.teogramm.mail.server.accounts.Account;
import org.teogramm.mail.server.accounts.AccountManager;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Class responsible for interacting with users' mailboxes
 */
public class Mailer {
    private final AccountManager am;

    /**
     * @param am AccountManager used to fetch users and mailboxes
     */
    Mailer(AccountManager am){
        this.am = am;
    }

    public void sendMail(String sender,String receiver,String subject,String content){
        // Check that the receiver account exists
        Account receiverAccount = am.getAccount(receiver);
        if(receiverAccount == null){
            sendFailedEmail(sender, receiver, subject, content);
            return;
        }
        // Create the mail object and deliver it
        Mail mail = new Mail(sender,receiver,subject,content);
        mail.setTime(LocalDateTime.now());
        receiverAccount.deliverMail(mail);
    }

    /**
     * @return A set of JSON serialized Mail objects that are in the user's mailbox
     */
    public Set<String> getUserMailboxSerialized(String username){
        Set<String> mailbox = new HashSet<>();
        // User has been authenticated, so username exists
        Account user = am.getAccount(username);
        // Need to use custom serializer because Mail contains a LocalDateTime object
        Gson gson = CustomSerializerFactory.createWithAll();
        for(Mail m:user.getMailbox()){
            mailbox.add(gson.toJson(m));
        }
        return mailbox;
    }

    /**
     * Searches the given user's mailbox for an email with the given UUID
     * @return Set with JSON serialized mail object
     */
    public Set<String> getMailByUuidSerialized(String username,String givenUuid){
        Account user = am.getAccount(username);
        UUID uuid = UUID.fromString(givenUuid);
        for (Mail m : user.getMailbox()) {
            if(m.getUuid().equals(uuid)){
                Gson gson = CustomSerializerFactory.createWithAll();
                Set<String> data = new HashSet<>();
                data.add(gson.toJson(m));
                // Mark the email as read since we will send it to the user
                m.read();
                return data;
            }
        }
        return null;
    }

    public boolean deleteMail(String username,String givenUuid){
        Account user = am.getAccount(username);
        UUID uuid = UUID.fromString(givenUuid);
        // Removeif takes a filter and removes elements matching it
        // It returns true if an element was found and removed
        return user.getMailbox().removeIf(mail -> mail.getUuid().equals(uuid));
    }

    /**
     * Sends a notification to sender that the
     * given email failed to be sent
     */
    private void sendFailedEmail(String sender,String receiver,String subject,String content){
        String messageText = "Dear " + sender + '\n' +
                "Your email to " + receiver + " was not delivered. Please ensure the " +
                "address you provided is correct.\n" +
                "Email subject: " + subject + '\n' +
                "Enail content:\n" + content;
        Mail notification = new Mail("postmaster",sender,"Failed delivery notification", messageText);
        notification.setTime(LocalDateTime.now());

        Account senderAccount = am.getAccount(sender);
        senderAccount.deliverMail(notification);
    }
}
