package org.teogramm.mail.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Response to a MailRequest from the server
 * <p>
 * Each response contains a successful key and a Set of strings containing different
 * data depending on the original request type and whether it was successful.
 * <p>
 * If the request  is successful the data set contains the data returned by the server. The format depends on the
 * original request type.
 * <p>
 * If the request is not successful the data set contains only
 * @see org.teogramm.mail.common.requests.RequestTypes
 */
public class MailResponse {
    private final boolean successful;
    /**
     * If request was successful contains the
     * requested data (for example the user's inbox).
     * If request was unsuccessful contains the relevant
     * error message.
     */
    private final Set<String> data;

    public MailResponse(boolean successful){
        this.successful = successful;
        data = new HashSet<>();
    }

    public MailResponse(boolean successful,Set<String> providedData){
        this.successful = successful;
        this.data = new HashSet<>();
        this.data.addAll(providedData);
    }

    public void addData(String data){
        this.data.add(data);
    }

    public Set<String> getData() {
        return data;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
