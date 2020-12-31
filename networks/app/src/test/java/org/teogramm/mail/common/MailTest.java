package org.teogramm.mail.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MailTest {

    @Test
    void reject_empty_sender(){
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            Mail mail = new Mail("","test@test.com","test","test");
        });
    }

    @Test
    void reject_empty_receiver(){
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            Mail mail = new Mail("test@test.com","","test","test");
        });
    }

    @Test
    void accept_empty_subject(){
        Assertions.assertDoesNotThrow(()->{
            Mail mail = new Mail("test@test.com","test@test.com","","test");
        });
    }

    @Test
    void accept_empty_body(){
        Assertions.assertDoesNotThrow(()->{
            Mail mail = new Mail("test@test.com","test@test.com","test","");
        });
    }

    @Test
    void reject_null_subject(){
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            Mail mail = new Mail("test@test.com","test@test.com",null,"");
        });
    }

    @Test
    void reject_null_body(){
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            Mail mail = new Mail("test@test.com","test@test.com","",null);
        });
    }
}
