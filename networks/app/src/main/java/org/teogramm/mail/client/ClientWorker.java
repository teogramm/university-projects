package org.teogramm.mail.client;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.teogramm.mail.client.ui.main.MailHeader;
import org.teogramm.mail.common.Mail;
import org.teogramm.mail.common.MailResponse;
import org.teogramm.mail.common.requests.MailRequest;
import org.teogramm.mail.common.requests.MailRequestFactory;
import org.teogramm.mail.common.serialization.CustomSerializerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * Class designed to facilitate communications between the User Interface
 * and the Network class. It abstracts the MailRequest and MailResponse objects.
 */
public class ClientWorker {
    private Network network;

    /**
     * Creates object and tries to connect to the given address
     * @throws IOException If connection fails
     */
    public ClientWorker(String ip,String port) throws IOException{
        try {
            InetAddress serverIP = InetAddress.getByName(ip);
            int serverPort = Integer.parseInt(port);
            network = new Network(serverIP,serverPort);
        }catch (UnknownHostException ignored){}
    }

    /**
     * Makes a login request with the given credentials.
     * @param username Username to authenticate as
     * @param password Password to authenticate with
     * @return true if login was successful, false otherwise
     * @throws ClientError When an error occurs during the login process
     */
    public boolean performLogin(String username, String password) throws ClientError {
        MailRequest request = MailRequestFactory.createLoginRequest(username,password);
        try {
            MailResponse response = network.makeRequest(request);
            return response.isSuccessful();
        }catch (IOException e){
            throw new ClientError(e.getMessage());
        }
    }

    /**
     * Makes a register request with the given information
     * @param username Username of the new user
     * @param password Password of the new user
     * @param fullName Full name of the new user
     * @return true if request is successful
     * @throws ClientError If request is unsuccessful
     */
    public boolean performRegister(String username,String password,String fullName) throws ClientError{
        MailRequest request = MailRequestFactory.createRegisterRequest(username, password, fullName);
        try{
            MailResponse response = network.makeRequest(request);
            if(response.isSuccessful()){
                return true;
            }else{
                // Return a ClientError with error passed by the server
                String errorMessage = response.getData().iterator().next();
                throw new ClientError(errorMessage);
            }
        }catch (IOException e){
            throw new ClientError(e.getMessage());
        }
    }

    /**
     * Fetches all emails in the given user's mailbox.
     * @param username User to authenticate as
     * @param password Password of given user
     * @return List of MailHeaders if request is successful
     * @throws ClientError If request is unsuccessful
     */
    public ObservableList<MailHeader> performFetchMail(String username,String password) throws ClientError{
        MailRequest request = MailRequestFactory.createFetchMailboxRequest(username, password);
        ObservableList<MailHeader> headers = FXCollections.observableArrayList();
        try{
            MailResponse response = network.makeRequest(request);
            // Response is set of serialized mail objects
            for (String serializedMail : response.getData()) {
                Gson gson = CustomSerializerFactory.createWithAll();
                Mail mail = gson.fromJson(serializedMail,Mail.class);
                headers.add(new MailHeader(mail));
            }
        } catch (IOException e) {
            throw new ClientError(e.getMessage());
        }
        return headers;
    }

    /**
     * Fetches a single email from a user's mailbox
     * @param username User to authenticate as
     * @param password Password of given user
     * @param uuid The UUID of the email to show
     * @return Mail object with the given email information, if request is successful
     * @throws ClientError If request is unsuccessful
     */
    public Mail performShowMail(String username, String password, UUID uuid) throws ClientError {
        MailRequest request = MailRequestFactory.createShowMailRequest(username,password,uuid.toString());
        MailResponse response;
        try{
            response = network.makeRequest(request);
        }catch (IOException e){
            throw new ClientError(e.getMessage());
        }
        if(!response.isSuccessful()){
            throw new ClientError(response.getData().iterator().next());
        }
        // Showmail response is a single serialized mail object
        Gson gson = CustomSerializerFactory.createWithAll();
        String serializedMail = response.getData().iterator().next();
        return gson.fromJson(serializedMail,Mail.class);
    }

    /**
     * Sends a new mail
     * @param username Username of the email sender
     * @param password Password of the email sender
     * @param recipient Recipient of the email
     * @param subject Email subject, can be blank
     * @param content Email content, can be blank
     * @return true or false depending on whether the request was successful
     * @throws ClientError If an error was encountered while making the request
     */
    public boolean performSendMail(String username,String password,String recipient,String subject,String content) throws ClientError{
        MailRequest request = MailRequestFactory.createSendMailRequest(username, password, recipient, subject, content);
        try{
            MailResponse response = network.makeRequest(request);
            return response.isSuccessful();
        } catch (IOException|IllegalArgumentException e) {
            throw new ClientError(e.getMessage());
        }
    }

    /**
     * Delete an email from the server
     * @param username User to authenticate as
     * @param password Password of given user
     * @param uuid UUID of the email to delete
     * @return true or false depending on whether deletion was successful
     * @throws ClientError If an error is encountered while making the request
     */
    public boolean performDeleteMail(String username,String password,UUID uuid) throws ClientError{
        MailRequest request = MailRequestFactory.createDeleteMailRequest(username,password,uuid.toString());
        try{
            MailResponse response = network.makeRequest(request);
            return response.isSuccessful();
        } catch (IOException e) {
            throw new ClientError(e.getMessage());
        }
    }
}
