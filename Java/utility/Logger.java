package utility;

import printer.ServletPrinter;

public class Logger {
    private String classname;
    ServletPrinter printer;

    Logger(String className) {
        this.classname = className;
        this.printer = new ServletPrinter(className);
    }

    public void log(Object... words) {
        StringBuilder log = new StringBuilder();
        for (Object word : words) {
            log.append(word).append(" ");
        }

        printer.println(log);
    }
}