package filters;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import auth.Authentication;
import config.Properties;
import utility.*;

public class AuthenticationFilter extends HttpFilter {
    private static final String MAIL_COOKIE_KEY = "usermail";
    private static final String TICKET_COOKIE_KEY = "ticket";
    private static final int PRODNAME_PREFIX_LEN = (Properties.getAppSubPath()).length() - 1;

    @Override
    public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) {
        String mail = "";
        String ticket = "";

        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            for (Cookie ck : cookies) {
                if (ck.getName().equals(MAIL_COOKIE_KEY)) {
                    mail = ck.getValue();
                }
                if (ck.getName().equals(TICKET_COOKIE_KEY)) {
                    ticket = ck.getValue();
                }
            }
        }

        try {
            Authentication authenticator = Authentication.getInstance();
            boolean properSession = authenticator.isVaildTicket(mail, ticket);
            String requestURI = Utils.getRequestURI(servletRequest).substring(PRODNAME_PREFIX_LEN);

            boolean proceed = false;

            if (properSession) {
                ThreadLocalHandler.setDetails(new SessionData(mail));
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            switch (requestURI) {
                case "/Pages/SignUp.html":
                case "/Pages/SignIn.html":
                case "/Pages/Style/styles.css":
                case "/Pages/js/script.js":
                case "/login":
                    proceed = true;
                    break;
                default:
                    break;
            }

            if (!proceed) {
                servletResponse.sendRedirect("Pages/SignIn.html");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Utils.sendFailureResp(servletResponse.getWriter(), new JSONObject(), "Internal server errror");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
