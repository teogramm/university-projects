package org.teogramm.mail.server.processing;

import org.teogramm.mail.common.requests.MailRequest;
import org.teogramm.mail.common.MailResponse;
import org.teogramm.mail.common.requests.RequestTypes;
import org.teogramm.mail.server.ServerError;
import org.teogramm.mail.server.accounts.AccountManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
* RequestProcessor is responsible for producing the responses to
 * requests made to the program. It ensures users are properly authenticated
 * before performing any action.
 * <p>
 * Each RequestProcessor object is bound to the AccountManager object given to it in the constructor.
 */
public class RequestProcessor {
    private final AccountManager am;
    private final Mailer mailer;

    /**
     * @param am Account manager used to
     */
    public RequestProcessor(AccountManager am){
        this.am = am;
        mailer = new Mailer(am);
    }

    /**
     * Specify a custom mailer. This constructor is meant to be used for testing (passing a mock).
     */
    public RequestProcessor(AccountManager am, Mailer m){
        this.am = am;
        this.mailer = m;
    }

    public MailResponse processRequest(MailRequest request){
        if(request == null){
            return createErrorResponse("Unknown request type");
        }
        return switch (RequestTypes.get(request.getType())) {
            case LOGIN_REQUEST -> processLoginRequest(request);
            case REGISTER_REQUEST -> processRegisterRequest(request);
            case SEND_MAIL -> processSendMailRequest(request);
            case FETCH_MAILBOX -> processFetchMailboxRequest(request);
            case SHOW_MAIL -> processShowMailRequest(request);
            case DELETE_MAIL -> processDeleteMailRequest(request);
        };
    }

    private MailResponse processLoginRequest(MailRequest request){
        // Check if the credentials match any account
        try {
            boolean successful = am.authenticate(request.getUsername(), request.getPassword());
            return new MailResponse(successful);
        }catch (ServerError e){
            return createErrorResponse(e);
        }
    }

    private MailResponse processRegisterRequest(MailRequest request){
        // Try to create a new account
        HashMap<String,String> data = request.getData();
        // If registration request does not have a full name consider it invalid
        if(!data.containsKey("fullName")){
            MailResponse response = new MailResponse(false);
            response.addData("Registration request must include a full name!");
            return response;
        }
        try{
            am.addAccount(request.getUsername(), request.getPassword(), data.get("fullName"));
        }catch (ServerError e){
            return createErrorResponse(e);
        }
        // If addAccount does not throw an exception the account has been created
        return new MailResponse(true);
    }

    private MailResponse processSendMailRequest(MailRequest request){
        // Check if credentials are correct and match the email sender
        Map<String,String> data = request.getData();
        boolean authenticated;
        try {
            authenticated = am.authenticate(request.getUsername(), request.getPassword());
        }catch (ServerError e){
            return createErrorResponse(e);
        }
        // Check if the credentials included with the request are correct
        if(!authenticated){
            return createErrorResponse("Invalid credentials");
        }

        // Check if all required information is present
        String sender = data.get("sender");
        String receiver = data.get("receiver");
        String subject = data.get("subject");
        String content = data.get("content");
        if(sender == null || receiver == null || subject == null || content == null){
            return createErrorResponse("Invalid request format");
        }
        // Check if included username matches the sender address
        if(!request.getUsername().equals(data.get("sender"))){
            return createErrorResponse("Credentials must match sender");
        }
        // Since all checks have passed give the information to the mailer
        mailer.sendMail(sender,receiver,subject,content);
        return new MailResponse(true);
    }

    private MailResponse processFetchMailboxRequest(MailRequest request){
        // Check if the credentials included with the request are correct
        boolean authenticated;
        try {
            authenticated = am.authenticate(request.getUsername(), request.getPassword());
        }catch (ServerError e){
            return createErrorResponse(e);
        }
        if(!authenticated){
            return createErrorResponse("Invalid credentials");
        }
        Set<String> responseData = mailer.getUserMailboxSerialized(request.getUsername());
        return new MailResponse(true,responseData);
    }

    private MailResponse processShowMailRequest(MailRequest request){
        // Check if credentials are correct
        boolean authenticated;
        try {
            authenticated = am.authenticate(request.getUsername(), request.getPassword());
        }catch (ServerError e){
            return createErrorResponse(e);
        }
        if(!authenticated){
            return createErrorResponse("Invalid credentials");
        }
        // To show the message we need the message UUID
        Map<String,String> data = request.getData();
        if(!data.containsKey("uuid")){
            return createErrorResponse("UUID of message required");
        }
        Set<String> responseData = mailer.getMailByUuidSerialized(request.getUsername(), data.get("uuid"));
        if( responseData != null){
            return new MailResponse(true,responseData);
        }
        return createErrorResponse("Message with given UUID not found in user's mailbox.");
    }

    private MailResponse processDeleteMailRequest(MailRequest request){
        // Check if credentials are correct
        boolean authenticated;
        try {
            authenticated = am.authenticate(request.getUsername(), request.getPassword());
        }catch (ServerError e){
            return createErrorResponse(e);
        }
        if(!authenticated){
            return createErrorResponse("Invalid credentials");
        }
        // To show the message we need the message UUID
        Map<String,String> data = request.getData();
        if(!data.containsKey("uuid")){
            return createErrorResponse("UUID of message required");
        }
        boolean successful = mailer.deleteMail(request.getUsername(), data.get("uuid"));
        return new MailResponse(successful,new HashSet<>());
    }

    /**
     * Creates a MailResponse from a ServerError exception
     */
    private MailResponse createErrorResponse(ServerError e){
        MailResponse response = new MailResponse(false);
        response.addData(e.getMessage());
        return response;
    }

    /**
     * Creates an unsuccessful response with the given message
     */
    private MailResponse createErrorResponse(String s){
        MailResponse response = new MailResponse(false);
        response.addData(s);
        return response;
    }
}
