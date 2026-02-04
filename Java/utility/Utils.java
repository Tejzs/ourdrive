package utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public class Utils {

    public static byte[] hash(String data) {
        return DigestUtils.sha256(data);
    }

    public static String hashHex(byte[] data) {
        return DigestUtils.sha256Hex(data);
    }

    public static String hashHex(String data) {
        return DigestUtils.sha256Hex(data);
    }

    public static boolean stringIsEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static Logger getLogger(String  className) {
        return new Logger(className);
    }

    public static void sendSuccessResp(PrintWriter writer, JSONObject respJson) {
        respJson.put("status", "success");
        writer.println(respJson.toString());
    }

    public static void sendFailureResp(PrintWriter writer, JSONObject respJson, String failMsg) {
        respJson.put("status", "failure");
        respJson.put("msg", failMsg);
        writer.println(respJson.toString());
    }

    public static byte[] readFromStream(InputStream is) throws IOException {
        try {
            return IOUtils.toByteArray(is);
        } finally {
            is.close();
        }
    }

    public static String readFromStreamAsString(InputStream is) throws IOException {
        return new String(readFromStream(is));
    }

    public static String getRequestURI(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return request.getContextPath() + request.getServletPath() + (pathInfo != null ? pathInfo : "");
    }

    public static String getServerHomeInServer(ServletContext servletContext) {
        return servletContext.getRealPath("/");
    }

    public static String properSize(long size) {
        double s = (double) size;

        if (s >= (1L << 40)) {
            return String.format("%.1f TB", s / (1L << 40));
        }
        if (s >= (1L << 30)) {
            return String.format("%.1f GB", s / (1L << 30));
        }
        if (s >= (1L << 20)) {
            return String.format("%.1f MB", s / (1L << 20));
        }
        if (s >= (1L << 10)) {
            return String.format("%.1f KB", s / (1L << 10));
        }
        return size + " B";
    }

    public static String getNonDuplicateFileName(String filepath, String filename) {
        File directory = new File(filepath);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path");
        }

        String name;
        String extension;

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            name = filename.substring(0, dotIndex);
            extension = filename.substring(dotIndex);
        } else {
            name = filename;
            extension = "";
        }

        File file = new File(directory, filename);
        int counter = 1;

        while (file.exists()) {
            String newFilename = name + "-" + counter + extension;
            file = new File(directory, newFilename);
            counter++;
        }

        return file.getName();
    }

    public static boolean checkExists(String filepath, String filename) {
        File directory = new File(filepath);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + filepath);
        }

        String name;
        String extension;

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            name = filename.substring(0, dotIndex);
            extension = filename.substring(dotIndex);
        } else {
            name = filename;
            extension = "";
        }

        File file = new File(directory, filename);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static void deleteFolderRecursive(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else {
                deleteFolderRecursive(file);
            }
        }
        folder.delete();
    }
}