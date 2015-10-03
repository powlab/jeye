package org.powlab.jeye.tests.exception;

import java.io.IOException;



public class ExceptionTest10b {


    public void ensureLoggedIn(boolean a, boolean b) {
        if (a == b) {
            this.showLoginPrompt();
        } else if (a) {
            this.showLoginPrompt();
        } else if (b) {
            try {
                throw new AuthenticationException();
            } catch (AuthenticationException ex) {
                System.out.println("A");
                this.showLoginPrompt();
            } catch (Exception ex) {
                System.out.println("B");
            }
        }

    }

    private void showLoginPrompt() {
    }

    private class AuthenticationException extends Exception {
    }
}
