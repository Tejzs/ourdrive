package printer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ServletPrinter {
    private static final String LOG_FILE = "/mnt/wwn-0x5000c500c67d4454-part1/Server/apache-tomcat-9.0.98/webapps/FileStorage/Log/log.log";

    File file;
    String className;

    public ServletPrinter() {
        this("noClass");
    }   
    public ServletPrinter(String className) {
        file = new File(LOG_FILE);
        this.className = className;
    }

    public synchronized void println() {
        println("");
    }
    public synchronized void println(Object obj) {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, true))) {
            String printer = (className.isEmpty()) ? "" : className + ": ";
            out.write((printer + obj.toString() + "\n").getBytes());
            out.flush();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized String read() {
        StringBuilder printedText = new StringBuilder();
        int byt;

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            while ((byt = in.read()) != -1) {
                printedText.append((char) byt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        clearFile();
        return printedText.toString();
    }

    private void clearFile() {
        try {
            new FileOutputStream(file).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
