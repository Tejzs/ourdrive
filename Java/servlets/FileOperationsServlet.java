package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import meta.FileOperationsMeta;
import operations.FileOperations;
import org.json.JSONObject;

import utility.SessionData;
import utility.Logger;
import utility.Utils;

public class FileOperationsServlet extends HttpServlet {
    private static final String CLASS_NAME = "FileOperations";
    private String baseDir = "";

    @Override
    public void init() {
        baseDir = Utils.getServerHomeInServer(getServletContext()) + "Files" + File.separatorChar;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        String mail = SessionData.getThreadLocalSessionData().getMail();
        String method = request.getParameter("method");

        String zipname;
        String parentDir;
        String folderName;
        String[] files;

        JSONObject output = new JSONObject();

        switch (method) {
            case "mkdir":
                parentDir = baseDir + File.separatorChar + mail + File.separatorChar + request.getParameter("parent");
                folderName = request.getParameter("folder");

                new File(parentDir, Utils.getNonDuplicateFileName(parentDir, folderName)).mkdir();
                Utils.sendSuccessResp(out, output);
                break;

            case "mkdirf":
                parentDir = baseDir + File.separatorChar + mail + File.separatorChar + request.getParameter("parent");
                folderName = request.getParameter("folder");
                boolean exists = Utils.checkExists(parentDir, folderName);
                if (!exists) {
                    new File(parentDir, Utils.getNonDuplicateFileName(parentDir, folderName)).mkdir();
                }
                Utils.sendSuccessResp(out, output.put("exists", exists));
                break;


            case "delete":
                parentDir = baseDir + File.separatorChar + mail + File.separatorChar + request.getParameter("parent");
                files = request.getParameter("files").split("\"");

                for (String fileName : files) {
                    if (fileName.equals("/")) {
                        continue;
                    }
                    File file = new File(parentDir, fileName);

                    if (!file.exists()) {
                        output.put(fileName, "Not exists");
                        continue;
                    }

                    if (file.isFile()) {
                        file.delete();
                    } else {
                        Utils.deleteFolderRecursive(file);
                    }

                }
                Utils.sendSuccessResp(out, output);
                break;

            case "zip":
                String filesStr = request.getParameter("files");
                zipname = request.getParameter("name");
                int compression = Integer.parseInt(request.getParameter("compression"));
                FileOperations.startZippingFiles(baseDir + File.separatorChar + mail + "\"" + zipname, filesStr, compression);
                Utils.sendSuccessResp(out, output);
                break;

            case "download":
                break;

            case "share":
                break;
        
            default:
                Utils.sendFailureResp(out, new JSONObject(), "Method not implemented: " + method);
                break;
        }
    }
}
