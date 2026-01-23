package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import utility.*;
import auth.Authentication;

public class UserDataServlet extends HttpServlet {
    private static final String CLASS_NAME = "UserDataServlet";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String method = request.getParameter("method");
        String mail = SessionData.getThreadLocalSessionData().getMail();

        try {
            switch (method) {
                case "currUser":
                    Utils.sendSuccessResp(out, new JSONObject().put("currUser", mail));
                    break;

                case "logout":
                    Authentication authenticator = Authentication.getInstance();
                    authenticator.removeSessionTicket(mail);
                    Utils.sendSuccessResp(out, new JSONObject());
                    break;
            
                default:
                    Utils.sendFailureResp(out, new JSONObject(), "Invalid method");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendFailureResp(out, new JSONObject(), "Internal server error");
        }
        
    }
}