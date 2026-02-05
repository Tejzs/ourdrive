package accessibility;

import mysql.SqlConnectionFactory;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessibilityHandler {
    private final Connection con;

    private AccessibilityHandler() throws Exception {
        this.con = SqlConnectionFactory.getConnection();
    }

    public static AccessibilityHandler getInstance() throws Exception {
        return new AccessibilityHandler();
    }

    public void registerAccessCode(String code, String folder, String mail) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO ShareRecords VALUES (?, ?, ?)");
        pstmt.setString(1, code);
        pstmt.setString(2, mail);
        pstmt.setString(3, folder);
        pstmt.executeUpdate();
    }

    public void useAccessCode(String code, String mail) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement("SELECT owner FROM ShareRecords WHERE shareid = ?");
        pstmt.setString(1, code);
        ResultSet rs = pstmt.executeQuery();

        String owner;

        if (rs.next()) {
            owner = rs.getString("owner");
        } else {
            throw new RuntimeException("Invalid code");
        }

        if (mail.equals(owner)) {
            throw new RuntimeException("Code activated by owner");
        }

        pstmt = con.prepareStatement("SELECT contents FROM AccessibleContent WHERE mail = ?");
        pstmt.setString(1, mail);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            String currCodes = rs.getString("contents");
            if (currCodes.contains(code)) {
                return;
            }
            currCodes += "," + code;
            pstmt = con.prepareStatement("UPDATE AccessibleContent SET contents = ? WHERE mail = ?");
            pstmt.setString(1, currCodes);
            pstmt.setString(2, mail);
            pstmt.executeUpdate();
        } else {
            pstmt = con.prepareStatement("INSERT INTO AccessibleContent VALUES (?, ?)");
            pstmt.setString(1, mail);
            pstmt.setString(2, code);
            pstmt.executeUpdate();
        }
    }

    public String getPathForCode(String code) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement("SELECT owner, folder FROM ShareRecords WHERE shareid = ?");
        pstmt.setString(1, code);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            String owner = rs.getString("owner");
            String folder = rs.getString("folder");
            return owner + folder;
        } else {
            return "";
        }
    }

    public JSONObject listAccessibleFolders(String mail) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement("SELECT contents FROM AccessibleContent WHERE mail = ?");
        pstmt.setString(1, mail);
        ResultSet rs = pstmt.executeQuery();

        JSONObject data = new JSONObject();
        if (rs.next()) {
            String currCodes = rs.getString("contents");
            String[] codes = currCodes.split(",");
            for (String code : codes) {
                data.put(code, getPathForCode(code));
            }
        }

        return data;
    }
}
