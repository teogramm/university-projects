package org.teogramm.mail.server.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.teogramm.mail.common.Mail;
import org.teogramm.mail.server.accounts.Account;
import org.teogramm.mail.server.accounts.AccountManager;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class MailerTest {

    private AccountManager accountManagerMock;
    private Account mockAccount;
    private Mailer mailer;

    private final String EXISTENT_ACCOUNT = "exists";
    private final String NON_EXISTENT_ACCOUNT = "doesnotexist";

    @BeforeEach
    void beforeEach(){
        // Create a mock account to use for testing
        mockAccount = mock(Account.class);

        accountManagerMock = mock(AccountManager.class);
        // Make account manager return mockAccount that exists
        when(accountManagerMock.getAccount(EXISTENT_ACCOUNT)).thenReturn(mockAccount);
        when(accountManagerMock.getAccount(NON_EXISTENT_ACCOUNT)).thenReturn(null);


        mailer = new Mailer(accountManagerMock);
    }

    @Test
    void send_successful(){
        mailer.sendMail(EXISTENT_ACCOUNT,EXISTENT_ACCOUNT,"test","test");

        // Verify that email was delivered
        verify(mockAccount).deliverMail(any());
    }

    @Test
    void send_to_non_existent(){
        mailer.sendMail(EXISTENT_ACCOUNT,NON_EXISTENT_ACCOUNT,"test","test");

        // Make sure that a mail is delivered to the sender indicating failure sending
        verify(mockAccount).deliverMail(any());
    }

    @Test
    void search_successful(){
        final String mailUUID = "98d3beff-4a05-46e4-aafe-bdc6a25ec34e";
        // Create a sample mailbox with 2 mails
        final Mail[] userMailbox = {new Mail(EXISTENT_ACCOUNT,EXISTENT_ACCOUNT,"","", UUID.fromString(mailUUID)),
                                    new Mail(EXISTENT_ACCOUNT,EXISTENT_ACCOUNT,"",""),};
        when(mockAccount.getMailbox()).thenReturn(Arrays.asList(userMailbox));

        Set<String> result = mailer.getMailByUuidSerialized(EXISTENT_ACCOUNT,mailUUID);
        Assertions.assertNotNull(result);
    }
}
