package org.teogramm.mail.server.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.teogramm.mail.common.requests.MailRequest;
import org.teogramm.mail.common.MailResponse;
import org.teogramm.mail.common.requests.RequestTypes;
import org.teogramm.mail.server.ServerError;
import org.teogramm.mail.server.accounts.AccountManager;

import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.*;


public class RequestProcessorTest {

    AccountManager accountManagerMock;
    RequestProcessor requestProcessor;

    private static final String CORRECT_USERNAME = "test";
    private static final String CORRECT_PASSWORD = "test";

    private static final String NON_EXISTENT_USERNAME = "wronguser";
    private static final String WRONG_PASSWORD = "wrongpass";

    @BeforeEach
    void beforeEach(){
        // Define AccountManager mock behaviour
        accountManagerMock = mock(AccountManager.class);
        try{
            when(accountManagerMock.authenticate(CORRECT_USERNAME,CORRECT_PASSWORD)).thenReturn(true);
            when(accountManagerMock.authenticate(NON_EXISTENT_USERNAME,WRONG_PASSWORD)).thenReturn(false);
            when(accountManagerMock.authenticate(CORRECT_USERNAME,WRONG_PASSWORD)).thenReturn(false);
        }catch (ServerError ignored){}
        requestProcessor = new RequestProcessor(accountManagerMock);
    }

    @Test
    void register_error_on_no_fullName(){
        MailRequest wrong = new MailRequest("whatever","whatever", RequestTypes.REGISTER_REQUEST);
        MailResponse response = requestProcessor.processRequest(wrong);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void register_successful(){
        MailRequest correct = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.REGISTER_REQUEST);
        correct.addData("fullName","test");

        MailResponse response = requestProcessor.processRequest(correct);
        Assertions.assertTrue(response.isSuccessful());
    }

    @Test
    void register_failed(){
        // Make accountManager throw an error when adding an account
        try {
            doThrow(new ServerError("Registration failed")).when(accountManagerMock).addAccount(anyString(),anyString(),anyString());
        } catch (ServerError ignored) {}
        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.REGISTER_REQUEST);
        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void login_accept_valid_credentials(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.LOGIN_REQUEST);
        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertTrue(response.isSuccessful());
    }

    @Test
    void login_reject_invalid_password(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,WRONG_PASSWORD,RequestTypes.LOGIN_REQUEST);
        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void login_reject_invalid_username(){
        MailRequest request = new MailRequest(NON_EXISTENT_USERNAME,WRONG_PASSWORD,RequestTypes.LOGIN_REQUEST);
        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }


    @Test
    void send_reject_invalid_credentials(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,WRONG_PASSWORD,RequestTypes.SEND_MAIL);
        // Add necessary data for send
        request.addData("sender","whatever");
        request.addData("receiver","whatever");
        request.addData("subject","whatever");
        request.addData("content","whatever");

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    /**
     * Sender name must match username
     */
    @Test
    void send_matching_sender_username(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.SEND_MAIL);
        // Add necessary data for send
        request.addData("sender",CORRECT_USERNAME + "dummy");
        request.addData("receiver","whatever");
        request.addData("subject","whatever");
        request.addData("content","whatever");

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void send_sender_missing(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.SEND_MAIL);
        // Add necessary data for send
        request.addData("receiver","whatever");
        request.addData("subject","whatever");
        request.addData("content","whatever");

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void send_receiver_missing(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.SEND_MAIL);
        // Add necessary data for send
        request.addData("sender",CORRECT_USERNAME);
        request.addData("subject","whatever");
        request.addData("content","whatever");

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void send_subject_missing(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.SEND_MAIL);
        // Add necessary data for send
        request.addData("sender",CORRECT_USERNAME);
        request.addData("receiver","whatever");
        request.addData("content","whatever");

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void send_content_missing(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.SEND_MAIL);
        // Add necessary data for send
        request.addData("sender",CORRECT_USERNAME);
        request.addData("receiver","whatever");
        request.addData("subject","whatever");

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void send_successful(){
        // We need to mock mailer for this test
        Mailer mailerMock = mock(Mailer.class);
        requestProcessor = new RequestProcessor(accountManagerMock,mailerMock);

        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.SEND_MAIL);
        // Add necessary data for send
        request.addData("sender",CORRECT_USERNAME);
        request.addData("receiver","whatever");
        request.addData("subject","whatever");
        request.addData("content","whatever");

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertTrue(response.isSuccessful());
    }

    @Test
    void fetch_wrong_credentials(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,WRONG_PASSWORD,RequestTypes.FETCH_MAILBOX);

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void fetch_successful(){
        // We need to mock mailer again
        Mailer mockMailer = mock(Mailer.class);
        requestProcessor = new RequestProcessor(accountManagerMock,mockMailer);

        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.FETCH_MAILBOX);

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertTrue(response.isSuccessful());
    }

    @Test
    void show_successful(){
        // We need to mock mailer
        Mailer mockMailer = mock(Mailer.class);
        String[] message = {"dummy"};
        // Make mailer return a dummy email message
        when(mockMailer.getMailByUuidSerialized(CORRECT_USERNAME,"test")).thenReturn(new HashSet<>(Arrays.asList(message)));

        requestProcessor = new RequestProcessor(accountManagerMock,mockMailer);

        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.SHOW_MAIL);
        // Add show data
        request.addData("uuid","test");

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertTrue(response.isSuccessful());
    }

    @Test
    void show_wrong_credentials(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,WRONG_PASSWORD,RequestTypes.SHOW_MAIL);

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void show_no_uuid(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.SHOW_MAIL);

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void delete_wrong_credentials(){
        MailRequest request = new MailRequest(CORRECT_USERNAME,WRONG_PASSWORD,RequestTypes.DELETE_MAIL);

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertFalse(response.isSuccessful());
    }

    @Test
    void delete_successful(){
        final String mailUuid = "test";
        // We need to mock mailer
        Mailer mailerMock = mock(Mailer.class);
        when(mailerMock.deleteMail(CORRECT_USERNAME,mailUuid)).thenReturn(true);

        requestProcessor = new RequestProcessor(accountManagerMock,mailerMock);

        MailRequest request = new MailRequest(CORRECT_USERNAME,CORRECT_PASSWORD,RequestTypes.DELETE_MAIL);
        request.addData("uuid",mailUuid);

        MailResponse response = requestProcessor.processRequest(request);
        Assertions.assertTrue(response.isSuccessful());
    }
}
