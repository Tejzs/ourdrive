package init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import config.Properties;
import operations.FileOperationsProcessor;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new Thread(new FileOperationsProcessor()).start();
        Properties.loadConfigurations();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}