package printer;

import config.Properties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ServletPrinter {
    private static final String LOG_FILE = Properties.getLogPath();

    File file;
    String className;

    public ServletPrinter() {
        this("");
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
