package org.teogramm.mail.common.requests;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of all possible request types that can be made to a MailServer
 */
public enum RequestTypes {
    /**
     * Login request. Only username and password is given. If successful field of response is true, login was successful.
     */
    LOGIN_REQUEST("login"),
    /**
     * Register request includes the username, password and fullName for the user. If successful field of response is
     * true, the registration was successful.
     */
    REGISTER_REQUEST("register"),
    /**
     * Send mail request includes username and password of the sender, the sender and recipient addresses, the subject
     * and the content of the email. If successful filed of response is true, the email was sent.
     */
    SEND_MAIL("send"),
    /**
     * Fetch mailbox request includes the username and password of the user. If the request is successful, the response
     * data contains serialized Mail objects.
     */
    FETCH_MAILBOX("fetch"),
    SHOW_MAIL("show"),
    DELETE_MAIL("delete");


    private final String requestType;

    RequestTypes(String type){
        this.requestType = type;
    }

    /**
     * Get string code of request type
     * @return string code of request type
     */
    public String getRequestTypeString() {
        return requestType;
    }

    // ** Reverse lookup - Enum value from string ** //
    /**
     * Maps type string of enum to enum value
     */
    private static final Map<String,RequestTypes> lookup = new HashMap<>();

    // Populate lookup table on runtime
    static{
        for(RequestTypes type: RequestTypes.values()){
            lookup.put(type.getRequestTypeString(),type);
        }
    }

    /**
     * Get the RequestTypes object for the given string
     * @param typeString String to match against a type
     * @return RequestTypes object if given string matches a type, null otherwise
     */
    public static RequestTypes get(String typeString){
        return lookup.get(typeString);
    }
}
