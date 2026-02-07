package servlets;

import accessibility.AccessibilityHandler;

import org.json.JSONObject;

import utility.SessionData;
import utility.Utils;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class ShareServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String method = request.getParameter("method");
        String folder = request.getParameter("folder");
        String mail = SessionData.getThreadLocalSessionData().getMail();
        String time = String.valueOf(System.currentTimeMillis());
        String code = Utils.hashHex(folder + mail + time);

        try {
            switch (method) {
            case "create":
                if (!new File(Utils.getServerHomeInServer(getServletContext()) + "Files/" + mail + folder).exists()) {
                    throw new IllegalArgumentException("Folder does not exist");
                }
                AccessibilityHandler.getInstance().registerAccessCode(code, folder, mail);
                Utils.sendSuccessResp(out, new JSONObject().put("code", code));
                break;
            case "list":
                Utils.sendSuccessResp(out, new JSONObject().put("data",
                        AccessibilityHandler.getInstance().listAccessibleFolders(mail)));
                break;
            case "createdTokens":
                Utils.sendSuccessResp(out, new JSONObject().put("tokens", AccessibilityHandler.getInstance().getCreatedTokens(mail)));
                break;
            case "delete":
                code = request.getParameter("token");
                AccessibilityHandler.getInstance().deleteToken(mail, code);
                Utils.sendSuccessResp(out, new JSONObject());
                break;
            default:
                Utils.sendFailureResp(out, new JSONObject(), "Method not implemented: " + method);
                break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        JSONObject input = new JSONObject(Utils.readFromStreamAsString(request.getInputStream()));
        String code = input.getString("code");
        String mail = SessionData.getThreadLocalSessionData().getMail();

        try {
            AccessibilityHandler.getInstance().useAccessCode(code, mail);
            Utils.sendSuccessResp(out, new JSONObject());
        } catch (RuntimeException e) {
            Utils.sendFailureResp(out, new JSONObject(), e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
