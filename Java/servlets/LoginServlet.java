package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import init.Factory;
import org.json.JSONObject;

import auth.Authentication;
import utility.Utils;


public class LoginServlet extends HttpServlet {
    private static final String className = "LoginServlet";
    private String storeDir = "";

    @Override
    public void init() {
        storeDir = Utils.getServerHomeInServer(getServletContext()) + "Files" ;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        JSONObject output = new JSONObject();
        try {
            String mail = request.getParameter("mail");
            String pass = request.getParameter("pass");

            Authentication authenticator = Authentication.getInstance();
            boolean created = authenticator.addNewUser(mail, pass);
            if (created) {
                Utils.sendSuccessResp(out, output);
                new File(storeDir + File.separatorChar + mail).mkdir();
                Factory.getClient().mkdir("/Files", mail);
            } else {
                Utils.sendFailureResp(out, output, "User already exists");
            }
        } catch (Exception e) {
            Utils.sendFailureResp(out, output, "Internal server error.");
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        JSONObject output = new JSONObject();
        try {
            JSONObject input = new JSONObject(Utils.readFromStreamAsString(request.getInputStream()));

            String mail = input.getString("email");
            String pass = input.getString("pass");

            Authentication authenticator = Authentication.getInstance();
            String ticket = authenticator.verifyPasswordAndCreateTicket(mail, pass);

            if (Utils.stringIsEmpty(ticket)) {
                Utils.sendFailureResp(out, output, "Incorrect password");
            } else {
                Cookie mailCookie = new Cookie("usermail", mail);
                Cookie ticketCookie = new Cookie("ticket", ticket);
                response.addCookie(mailCookie);
                response.addCookie(ticketCookie);

                Utils.sendSuccessResp(out, output);
            }
        } catch (Exception e) {
            Utils.sendFailureResp(out, output, "Internal server error.");
            e.printStackTrace();
        }
    }
}