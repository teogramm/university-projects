package org.teogramm.mail.server.accounts;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import org.teogramm.mail.common.Mail;

public class Account {
    private final String username;
    private final String saltedPw;
    private final String salt;
    private final String fullName;
    private final List<Mail> mailbox = new LinkedList<>();

    /**
     * Creates a new account
     * @param username A non-empty string
     * @param password A non-empty password
     */
    public Account(String username,String password,String fullName) throws NoSuchAlgorithmException {
        if(username.isBlank() || password.isBlank()){
            throw new IllegalArgumentException("Username and password can't be blank");
        }
        this.username = username;
        // Calculate the salted password hash and store it
        this.salt = createSalt(6);
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");
        md.update(password.concat(salt).getBytes());
        this.saltedPw = new String(md.digest());

        this.fullName = fullName;

    }

    public boolean authenticate(String givenPassword) throws DigestException{
        MessageDigest md;
        try{
            // Calculate salted digest for given password
            md = MessageDigest.getInstance("SHA-256");
            md.update(givenPassword.concat(salt).getBytes());
            String givenPasswordDigest = new String(md.digest());

            // Compare with stored password
            return givenPasswordDigest.equals(this.saltedPw);
        }catch(NoSuchAlgorithmException e){
            throw new DigestException("Could not validate password.");
        }
    }

    public List<Mail> getMailbox() {
        return mailbox;
    }

    /**
     * Adds the Mail to the user's mailbox.
     */
    public synchronized void deliverMail(Mail mail){
        mailbox.add(mail);
    }

    /**
     * Creates a password salt with the give number of characters
     * @param size Number of characters
     */
    private String createSalt(int size){
        SecureRandom random = new SecureRandom();
        byte[] buffer = new byte[size];
        random.nextBytes(buffer);
        return new String(buffer);
    }

}
