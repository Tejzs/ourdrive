package filters;

import utility.SessionData;
import utility.Utils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FileAccessFilter extends HttpFilter {
    private static final int PRODNAME_PREFIX_LEN = "/FileStorage/Files/".length();

    @Override
    public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = Utils.getRequestURI(servletRequest).substring(PRODNAME_PREFIX_LEN);
        String mail = SessionData.getThreadLocalSessionData().getMail();

        if (!requestURI.substring(0, requestURI.indexOf("/")).equals(mail)) {
            servletResponse.sendRedirect("Pages/unauthorized");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
