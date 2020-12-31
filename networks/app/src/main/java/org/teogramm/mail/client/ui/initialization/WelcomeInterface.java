package org.teogramm.mail.client.ui.initialization;

/**
 * Interface to facilitate communication between welcome screen and the
 * primary window.
 */
public interface WelcomeInterface {
    void login(String username, String password);
    void register(String username, String password, String fullName);
}
