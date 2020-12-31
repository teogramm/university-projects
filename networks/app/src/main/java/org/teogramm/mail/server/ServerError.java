package org.teogramm.mail.server;

public class ServerError extends Exception{
    public ServerError(String message) {
        super(message);
    }
}
