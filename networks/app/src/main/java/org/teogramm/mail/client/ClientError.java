package org.teogramm.mail.client;

public class ClientError extends Exception{
    public ClientError(String error){
        super(error);
    }
}
