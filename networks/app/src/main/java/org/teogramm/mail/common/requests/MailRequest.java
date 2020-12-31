package org.teogramm.mail.common.requests;

import java.util.HashMap;

/**
 * Represents a request made from the client to the server
 * <p>
 * Each request contains the username and password of the user
 * making the request and a HashMap containing the additional request
 * data.
 */
public class MailRequest {
    private final String type;
    private final String username;
    private final String password;
    private final HashMap<String,String> data;

    /**
     * Creates a new MailRequest object with empty data.
     * @param username Username to authenticate as
     * @param password Password of the given username
     * @param type Request type
     */
    public MailRequest(String username, String password, RequestTypes type){
        if(username.isBlank() || password.isBlank()){
            throw new IllegalArgumentException("Username and password can't be blank");
        }

        this.type = type.getRequestTypeString();
        this.username = username;
        this.password = password;
        this.data = new HashMap<>();
    }

    /**
     * Adds a new key-value pair to the data of this request
     */
    public void addData(String key,String value){
        data.put(key,value);
    }

    public HashMap<String,String> getData(){
        return data;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
