package org.teogramm.mail.server.accounts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTest {

    @Test
    void throw_on_empty_username(){
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            Account test = new Account("","test","test");
        });
    }

    @Test
    void throw_on_empty_password(){
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            Account test = new Account("test","","test");
        });
    }

    @Test
    void authentication_correct_credentials(){
        Assertions.assertDoesNotThrow(()->{
            final String USERNAME = "test";
            final String PASSWORD = "test";
            Account test = new Account(USERNAME,PASSWORD,"test");
            Assertions.assertTrue(test.authenticate(PASSWORD));
        });
    }

    @Test
    void authentication_wrong_credentials(){
        Assertions.assertDoesNotThrow(()->{
            final String USERNAME = "test";
            final String PASSWORD = "test";
            Account test = new Account(USERNAME,PASSWORD + "aaaaa","test");
            Assertions.assertFalse(test.authenticate(PASSWORD));
        });
    }
}
