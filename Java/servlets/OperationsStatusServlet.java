package servlets;

import java.util.Map.Entry;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import meta.FileOperationsMeta;
import storage.DataStorage;
import utility.Utils;
import utility.SessionData;

public class OperationsStatusServlet extends HttpServlet {
    private static final String CLASS_NAME = "OperationsStatusServlet";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String mail = SessionData.getThreadLocalSessionData().getMail();

        String method = request.getParameter("method");

        JSONArray metas = new JSONArray();

        switch (method) {
            case "list":
                for (Entry<Object, Serializable> entry : DataStorage.getFileOperationsMetaStore().getEntrySet()) {
                    FileOperationsMeta meta = (FileOperationsMeta) entry.getValue();
                    Utils.getLogger(CLASS_NAME).log(meta.toJson());

                    if (!meta.getInitiator().equals(mail)) {
                        continue;
                    }

                    metas.put(meta.toJson());
                }
                Utils.sendSuccessResp(out, new JSONObject().put("data", metas));
                break;
        
            default:
                Utils.sendFailureResp(out, new JSONObject(), "Method not implemented: " + method);
                break;
        }   
        
    }
}