package init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import config.Properties;
import operations.FileOperationsProcessor;
import utility.Utils;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new Thread(new FileOperationsProcessor()).start();
        Properties.loadConfigurations(Utils.getServerHomeInServer(sce.getServletContext()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}