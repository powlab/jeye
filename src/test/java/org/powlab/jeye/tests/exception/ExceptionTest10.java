package org.powlab.jeye.tests.exception;

import java.io.File;
import java.io.IOException;


import java.io.IOException;

public class ExceptionTest10 {
    private Profile profileManager;
    private Logger LOGGER;


    public void ensureLoggedIn() {
        Profile selectedProfile = this.profileManager.getSelectedProfile();
        UserAuthentication auth = this.profileManager.getAuthDatabase().getByUUID(selectedProfile.getPlayerUUID());
        if (auth == null) {
            this.showLoginPrompt();
        } else if (!auth.isLoggedIn()) {
            if (auth.canLogIn()) {
                try {
                    auth.logIn();

                    try {
                        this.profileManager.saveProfiles();
                    } catch (IOException ex) {
                        LOGGER.error("Couldn\'t save profiles after refreshing auth!", ex);
                    }

                    this.profileManager.fireRefreshEvent();
                } catch (AuthenticationException ex) {
                    LOGGER.error("Exception whilst logging into profile", ex);
                    this.showLoginPrompt();
                }
            } else {
                this.showLoginPrompt();
            }
        } else if (!auth.canPlayOnline()) {
            try {
                LOGGER.info("Refreshing auth...");
                auth.logIn();

                try {
                    this.profileManager.saveProfiles();
                } catch (IOException ex) {
                    LOGGER.error("Couldn\'t save profiles after refreshing auth!", ex);
                }

                this.profileManager.fireRefreshEvent();
                this.profileManager.doThat();
            } catch (InvalidCredentialsException ex) {
                LOGGER.error("Exception whilst logging into profile", ex);
                this.showLoginPrompt();
            } catch (AuthenticationException ex) {
                LOGGER.error("Exception whilst logging into profile", ex);
            }
        }

    }

    private void showLoginPrompt() {

    }

    private class Profile {
        private String playerUUID;

        public Profile getSelectedProfile() {
            return null;
        }

        public UserAuthentication getAuthDatabase() {
            return new UserAuthentication();
        }

        public String getPlayerUUID() {
            return playerUUID;
        }

        public void saveProfiles() throws IOException {
            throw new IOException();
        }

        public void fireRefreshEvent() throws AuthenticationException {
            throw new AuthenticationException();
        }

        public void doThat() throws InvalidCredentialsException {
            throw new InvalidCredentialsException();
        }
    }

    private class UserAuthentication {
        private boolean loggedIn;

        public UserAuthentication getByUUID(String uuid) {
            return new UserAuthentication();
        }

        public boolean isLoggedIn() {
            return loggedIn;
        }

        public boolean canLogIn() {
            return false;
        }

        public void logIn() {

        }

        public boolean canPlayOnline() {
            return false;
        }
    }

    private class AuthenticationException extends java.lang.Exception {
    }

    private class InvalidCredentialsException extends java.lang.Exception {
    }

    private class Logger {
        public void error(String s, IOException ex) {

        }

        public void error(String s, Exception ex) {

        }

        public void info(String s) {

        }
    }
}
