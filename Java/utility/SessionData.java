package utility;

import java.io.Writer;
import utility.ThreadLocalHandler.ThreadLocalDetails;

public class SessionData implements ThreadLocalDetails {

    private String mail;

    public static SessionData getThreadLocalSessionData() {
        return (SessionData) ThreadLocalHandler.getDetails();
    }

    public static void removeThreadLocalSessionData() {
        ThreadLocalHandler.removeThreadLocal();
    }

    public SessionData(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setUsername(String mail) {
        this.mail = mail;
    }

}