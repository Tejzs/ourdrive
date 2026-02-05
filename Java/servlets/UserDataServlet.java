package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import utility.*;
import auth.Authentication;

public class UserDataServlet extends HttpServlet {
    private static final String CLASS_NAME = "UserDataServlet";

    private String baseDir = "";
    private final long gb = 1024 * 1024 * 1024;
    @Override
    public void init() {
        baseDir = Utils.getServerHomeInServer(getServletContext()) + "Files" + File.separatorChar;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String method = request.getParameter("method");
        String mail = SessionData.getThreadLocalSessionData().getMail();

        try {
            switch (method) {
                case "currUser":
                    Utils.sendSuccessResp(out, new JSONObject().put("currUser", mail));
                    break;

                case "freeSpace":
                    File drive = new File(baseDir + File.separatorChar + mail + File.separatorChar);
                    long totalSpace = drive.getTotalSpace();
                    long userUsed = FileUtils.sizeOfDirectory(drive);
                    long freeSpace = drive.getFreeSpace();
                    JSONObject fileArray = new JSONObject();
                    fileArray.put("currUser", mail);
                    fileArray.put("userUsed", Utils.properSize(userUsed));
                    fileArray.put("freeSpace", Utils.properSize(totalSpace - freeSpace));
                    fileArray.put("totalSpace", Utils.properSize(totalSpace));
                    Utils.sendSuccessResp(out, fileArray);
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