package org.teogramm.mail.server.accounts;

import org.teogramm.mail.server.ServerError;

import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Singleton class responsible for all operations regarding users
 */
public class AccountManager {

    private final HashMap<String,Account> accounts;

    public AccountManager() {
        accounts = new HashMap<>();
        // Add reserved username postmaster
        try {
            accounts.put("postmaster", new Account("postmaster", "test", "Postmaster"));
            // Add some testing accounts

        }catch(NoSuchAlgorithmException ignored){}
    }

    /**
     * Creates a new account
     * @throws ServerError If account could not be created
     */
    public synchronized void addAccount(String username,String password,String fullName) throws ServerError {
        if(accounts.containsKey(username)){
            throw new ServerError("An account with this username already exists!");
        }
        try {
            Account temp = new Account(username, password, fullName);
            accounts.put(username,temp);
        }catch (NoSuchAlgorithmException e){
            throw new ServerError(e.getMessage());
        }
    }

    /**
     * Authenticates using the given credentials
     * @return True if an account with the given username and password combination exists, false otherwise
     * @throws ServerError If there is an error during authentication
     */
    public boolean authenticate(String username,String password) throws ServerError{
        if(!accounts.containsKey(username)){
            return false;
        }
        try{
            return accounts.get(username).authenticate(password);
        }catch (DigestException e){
            throw new ServerError(e.getMessage());
        }
    }

    public Account getAccount(String username){
        return accounts.get(username);
    }
}
