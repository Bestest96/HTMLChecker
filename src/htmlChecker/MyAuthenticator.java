package htmlChecker;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;

public class MyAuthenticator extends Authenticator {

    String username;
    char[] password;

    public MyAuthenticator(String user, char[] pass) {
        username=user;
        password=pass;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        String prompt=getRequestingPrompt();
        String hostname=getRequestingHost();
        InetAddress ipaddr=getRequestingSite();
        int port=getRequestingPort();
        return new PasswordAuthentication(username, password);
    }

}
