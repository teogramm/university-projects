package org.teogramm.mail.common.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.teogramm.mail.common.requests.MailRequest;
import org.teogramm.mail.common.requests.RequestTypes;

public class MailRequestTest {

    @Test
    void reject_empty_username(){
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            MailRequest request = new MailRequest("","test", RequestTypes.LOGIN_REQUEST);
        });
    }

    @Test
    void reject_empty_password(){
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            MailRequest request = new MailRequest("test","",RequestTypes.LOGIN_REQUEST);
        });
    }
}
