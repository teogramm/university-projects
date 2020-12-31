package org.teogramm.mail.server;

import org.teogramm.mail.common.requests.MailRequest;
import org.teogramm.mail.common.requests.MailRequestFactory;
import org.teogramm.mail.server.accounts.AccountManager;
import org.teogramm.mail.server.processing.RequestProcessor;

import java.io.IOException;


public class Server {

    private final AccountManager accountManager = new AccountManager();
    private final RequestProcessor requestProcessor = new RequestProcessor(accountManager);

    public RequestProcessor getRequestProcessor(){
        return requestProcessor;
    }

    public static void main(String[] args){
        Server server = new Server();
        NetworkService ns = null;
        int port = 26611;
        try{
            port = Integer.parseInt(args[0]);
        }catch (Throwable e){
            System.err.println("Could not parse given port, using default.");
        }
        try {
            ns = new NetworkService(port, server.getRequestProcessor());
        }catch (IOException e){
            System.err.println("Could not create a listening socket! (Maybe something else is using the port)");
            System.exit(1);
        }
        Thread nsThread = new Thread(ns);
        nsThread.start();
        System.out.println("Server listening on port " + port);
        server.addExampleAccounts();
        Runtime.getRuntime().addShutdownHook(new Thread(ns::stop));
    }

    /**
     * Adds 2 example accounts to the server
     */
    private void addExampleAccounts(){
        try {
            accountManager.addAccount("theodore", "test","Theodore");
            accountManager.addAccount("george", "test2", "George Russell");
        }
        catch (ServerError e){
            System.err.println("Error when adding sample accounts: " + e.getMessage());
        }

        // Create 3 emails for each account
        MailRequest email1 = MailRequestFactory.createSendMailRequest("theodore","test",
                "george","Test","This is a test!");
        requestProcessor.processRequest(email1);
        MailRequest email2 = MailRequestFactory.createSendMailRequest("theodore","test",
                "george","Hello there","Hi!");
        requestProcessor.processRequest(email2);
        MailRequest email3 = MailRequestFactory.createSendMailRequest("theodore","test",
                "george","Important","This is something important");
        requestProcessor.processRequest(email3);

        MailRequest email4 = MailRequestFactory.createSendMailRequest("george","test2",
                "theodore","Test1","This is the first test.");
        requestProcessor.processRequest(email4);
        MailRequest email5 = MailRequestFactory.createSendMailRequest("george","test2",
                "theodore","Hello world!","Hello world!");
        requestProcessor.processRequest(email5);
        MailRequest email6 = MailRequestFactory.createSendMailRequest("george","test2",
                "theodore","Test2","This is the second test.");
        requestProcessor.processRequest(email6);
    }
}
