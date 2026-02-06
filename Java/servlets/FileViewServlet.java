package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import utility.*;

public class FileViewServlet extends HttpServlet {
    private static final String CLASS_NAME = "FileViewServlet";

    private String serverRootDir = "";

    @Override
    public void init() {
        serverRootDir = Utils.getServerHomeInServer(getServletContext()) + "Files" + File.separatorChar;
    }

    public String getExtention(File file) {
        String extension = "";
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        int p = fileName.lastIndexOf(File.separatorChar);

        if (i > p) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String mail = SessionData.getThreadLocalSessionData().getMail();
        String method = request.getParameter("method");
        String dir = request.getParameter("dir");
        if (dir == null || dir.equals("/") || dir.isEmpty()) {
            dir = "";
        }

        String userDataPath = serverRootDir + mail;
        File userFileObj = new File(userDataPath + dir);

        switch (method) {
            case "listFiles":
                File requestedFile = new File(userDataPath + "/" + dir);
                if (requestedFile.exists()) {
                    JSONArray fileArray = new JSONArray();

                    for (File file : requestedFile.listFiles()) {
                        JSONObject obj = new JSONObject();
                        obj.put("name", file.getName());
                        obj.put("type", file.isDirectory() ? "folder" : getExtention(file));
                        obj.put("size", Utils.properSize(file.length()));
                        obj.put("lastMod", new Date(file.lastModified()));
                        obj.put("owner", mail);
                        fileArray.put(obj);
                    }

                    Utils.sendSuccessResp(out, new JSONObject().put("files", fileArray));
                } else {
                    Utils.sendFailureResp(out, new JSONObject(), "Folder not exists");
                }
                break;

            default:
                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
    }
}