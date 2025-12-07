package net.fina.server.aoo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.util.AOOServiceManagerUtil;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeConnectionProtocol;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.artofsolving.jodconverter.process.ProcessManager;
import org.hyperic.sigar.ProcUtil;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.jboss.logging.Logger;

import java.io.File;
import java.util.*;

/**
 * User: nikoloz
 * Date: 8/29/13
 * Time: 9:07 AM
 */
@Singleton
@Startup
public class AOOServiceManager {
    private final Logger log = Logger.getLogger(getClass());

    private OfficeManager officeManager;

    @PostConstruct
    public void start() {

        try {
            ProcessManager processManager = OfficeConfigurationUtil.getProcessManager();
            AOOServiceManagerUtil.killOfficeProcess(processManager);

            String portsString = ConfigurationUtil.get().get("OFFICE_PORT");
            OfficeConnectionProtocol officeConnectionProtocol = OfficeConfigurationUtil.getOfficeConnectionProtocol();

            log.info("OpenOffice ports String:" + portsString);

            String[] portsStringArray = portsString.split("[,|;]");

            DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration()
                    .setConnectionProtocol(officeConnectionProtocol)
                    .setOfficeHome(ConfigurationUtil.get().get("OFFICE_HOME"))
                    .setTaskExecutionTimeout(108_000_000L)
                    .setMaxTasksPerProcess(OfficeConfigurationUtil.getMaxTaskPerProcess())
                    .setProcessManager(processManager)
                    .setWorkDir(new File(ConfigurationUtil.get().get("OFFICE_WORK_DIR")))
                    .setTemplateProfileDir(new File(ConfigurationUtil.get().get("OFFICE_TEMPLATE_DIR")));

            switch (officeConnectionProtocol) {
                case PIPE:
                    configuration.setPipeNames(portsStringArray);
                    break;
                case SOCKET:
                    int[] ports = new int[portsStringArray.length];
                    for (int i = 0; i < portsStringArray.length; i++) {
                        ports[i] = Integer.parseInt(portsStringArray[i]);
                    }
                    log.info("OpenOffice ports:" + Arrays.toString(ports));

                    configuration.setPortNumbers(ports);
                    break;
            }

            officeManager = configuration.buildOfficeManager();

            officeManager.start();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }


    @PreDestroy
    public void stop() {
        if (officeManager != null) {
            officeManager.stop();
        }
    }

    public OfficeManager getOfficeManager() {
        return officeManager;
    }

    public Map<Long, Long> getOfficeProcessMemoryMap() throws SigarException {
        Map<Long, Long> officeProcessesMap = new HashMap<>();
        final Sigar sigar = new Sigar();
        final long[] processes = sigar.getProcList();
        for (final long processId : processes) {
            final String processDescription = ProcUtil.getDescription(sigar, processId);
            if (processDescription.contains("soffice")) {
                officeProcessesMap.put(processId, (sigar.getProcMem(processId).getResident() / (1024 * 1024)));
            }
        }
        return officeProcessesMap;
    }

}
