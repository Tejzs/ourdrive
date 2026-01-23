package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import utility.Utils;
import meta.FileUploadMeta;
import utility.SessionData;
import storage.InMemoryStoreHandler;

public class FileUploadStatus extends HttpServlet {
    private static final String CLASS_NAME = "FileUploadStatus";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        JSONObject res = new JSONObject();
        String method = request.getParameter("method");
        String mail = SessionData.getThreadLocalSessionData().getMail();

        switch (method) {
            case "list":
                Set<Entry<Object, Serializable>> entries = InMemoryStoreHandler.getFileUploadMetaStore().getEntrySet();
                JSONArray metaDatas = new JSONArray();

                for (Entry<Object, Serializable> entry : entries) {
                    FileUploadMeta fileUploadMeta = (FileUploadMeta) entry.getValue(); 
                    if (fileUploadMeta == null) {
                        continue;
                    }

                    if (fileUploadMeta.getUploader().equals(mail)) {
                        JSONObject metaData = fileUploadMeta.toJson();
                        metaDatas.put(metaData);
                    }
                }

                res.put("data", metaDatas);
                Utils.sendSuccessResp(out, res);
                break;
            case "delete":
                String uploadId = request.getParameter("uploadId");
                InMemoryStoreHandler.getFileUploadMetaStore().delete(uploadId);
                Utils.sendSuccessResp(out, new JSONObject());
                break;
            default:
                Utils.sendFailureResp(out, new JSONObject(), "Method not implemented: " + method);
                break;
        }
    }
}