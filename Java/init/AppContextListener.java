package init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.LoggerFactory;

import config.Properties;
import operations.FileOperationsProcessor;
import utility.Utils;

public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LoggerFactory.getLogger(AppContextListener.class).info("App Started");
        new Thread(new FileOperationsProcessor()).start();
        Properties.loadConfigurations(Utils.getServerHomeInServer(sce.getServletContext()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}