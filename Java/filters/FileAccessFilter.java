package filters;

import accessibility.AccessibilityHandler;
import config.Properties;
import utility.SessionData;
import utility.Utils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class FileAccessFilter extends HttpFilter {
    private static final int PRODNAME_PREFIX_LEN = (Properties.getAppSubPath() + "Files/").length();

    @Override
    public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = Utils.getRequestURI(servletRequest).substring(PRODNAME_PREFIX_LEN);
        String mail = SessionData.getThreadLocalSessionData().getMail();
        String main;
        if ((main = servletRequest.getParameter("sharedMain")) != null) {
            try {
                AccessibilityHandler.getInstance().checkAccessible(main, mail);
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (!requestURI.substring(0, requestURI.indexOf("/")).equals(mail)) {
            servletResponse.sendRedirect(Properties.getAppSubPath() + "Pages/unauthorized.html");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
