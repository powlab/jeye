package org.powlab.jeye.tests.exception;



public abstract class ExceptionTest10c {


    public void ensureLoggedIn(boolean a, boolean b) {
        if (a == b) {
            this.showLoginPrompt();
        } else if (a) {
            this.showLoginPrompt();
        } else if (b) {
            try {
                test();
            } catch (IllegalArgumentException ex) {
                System.out.println("A");
                this.showLoginPrompt();
            } catch (Exception ex) {
                System.out.println("B");
            }
        }

    }

    private void showLoginPrompt() {
    }

    abstract void test();
}
