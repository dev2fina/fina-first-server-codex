package net.fina.server.util;


import org.jboss.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Singleton
@Startup
@DependsOn({"FsopImportAutoStartService"})
public class ServerStartFinishService {

    private Logger log = Logger.getLogger(getClass());

    @PostConstruct
    public void info() {
        log.info("\n .----------------.  .----------------.  .-----------------. .----------------. \n" +
                "| .--------------. || .--------------. || .--------------. || .--------------. |\n" +
                "| |  _________   | || |     _____    | || | ____  _____  | || |      __      | |\n" +
                "| | |_   ___  |  | || |    |_   _|   | || ||_   \\|_   _| | || |     /  \\     | |\n" +
                "| |   | |_  \\_|  | || |      | |     | || |  |   \\ | |   | || |    / /\\ \\    | |\n" +
                "| |   |  _|      | || |      | |     | || |  | |\\ \\| |   | || |   / ____ \\   | |\n" +
                "| |  _| |_       | || |     _| |_    | || | _| |_\\   |_  | || | _/ /    \\ \\_ | |\n" +
                "| | |_____|      | || |    |_____|   | || ||_____|\\____| | || ||____|  |____|| |\n" +
                "| |              | || |              | || |              | || |              | |\n" +
                "| '--------------' || '--------------' || '--------------' || '--------------' |\n" +
                " '----------------'  '----------------'  '----------------'  '----------------' ");
    }
}
