package net.fina.server.mdt.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.script.ScriptEngineBase;
import net.fina.server.processing.script.ScriptEngineFactory;
import net.fina.server.processing.script.ScriptEngineProvider;
import net.fina.server.security.api.PropertyLocal;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * nika on 4/17/2014.
 */
@Startup
@Singleton
@DependsOn("MDTCacheManager")
public class ScriptEngineService {
    private final Logger log = Logger.getLogger(getClass());
    @EJB
    private MDTCacheManager cacheManager;
    @EJB
    private MDTNodeLocal mdtNodeLocal;
    @EJB
    private PropertyLocal propertyLocal;
    @Resource
    private TimerService timerService;

    @PostConstruct
    private void start() {
        System.setProperty(PropertyKeys.SCRIPT_ENGINE_PROVIDER, getScriptEngineProvider().name());
        timerService.createIntervalTimer(1000, 1000 * 60 * 30, new TimerConfig(null, false));
    }

    @Timeout
    public void automaticTimeout() {
        init();
        log.info("Compile equations...");
    }


    private void init() {


        try (StatisticsLogger statLogger = new StatisticsLogger("ScriptEngineService");) {
            statLogger.logStage("create engine pool instances");
            ScriptEngineBase scriptEngineBase = ScriptEngineFactory.get();
            statLogger.logStage("compile mdt node variables");

            cacheManager.getMdtNodes().stream().filter(mdtNode -> mdtNode.getType() == MDTNodeTypes.VARIABLE).forEach(mdtNode -> {
                if (mdtNode.getEquation() == null || !mdtNode.getEquation().isBlank()) {
                    String script = ScriptEngineBase.createFunction(mdtNode.getEquation());
                    compileVariables(scriptEngineBase, script, mdtNode);
                }
            });

            statLogger.logStage("compile comparisons");
            List<MDTComparison> comparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);
            statLogger.logMessage("Comparisons size : " + comparisons.size());
            compileComparisons(comparisons, scriptEngineBase);

        }
    }

    private void compileComparisons(List<MDTComparison> mdtComparisons, ScriptEngineBase scriptEngine) {
        for (MDTComparison c : mdtComparisons) {
            try {
                String equation = c.getLeftEquation();
                if (equation != null && !equation.isBlank()) {
                    String script = ScriptEngineBase.createFunction(equation);
                    Object cs = scriptEngine.find(script);

                    if (cs == null) {
                        scriptEngine.compile(script, "left equation - " + (c.getNode() != null ? c.getNode().getCode() : ""));
                    }
                }

                equation = c.getRightEquation();
                if (equation != null && !equation.isBlank()) {
                    String script = ScriptEngineBase.createFunction(equation);

                    Object cs = scriptEngine.find(script);

                    if (cs == null) {
                        scriptEngine.compile(script, "right equation - " + (c.getNode() != null ? c.getNode().getCode() : ""));
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
    }

    private void compileVariables(ScriptEngineBase scriptEngine, String script, MDTNode mdtNode) {
        Object cs = scriptEngine.find(script);
        if (cs == null) {
            Object tmp = scriptEngine.compile(script, mdtNode.getCode());
            if (tmp == null && !mdtNode.isDamagedEquation()) {
                mdtNode.setDamagedEquation(true);
                cacheManager.addCache(mdtNode);
            }
        }
    }


    private ScriptEngineProvider getScriptEngineProvider() {
        ScriptEngineProvider provider = ScriptEngineProvider.NASHORN;
        try {
            String scriptEngineProviderProperty = propertyLocal.getSystemProperty(PropertyKeys.SCRIPT_ENGINE_PROVIDER);
            provider = scriptEngineProviderProperty == null ? ScriptEngineProvider.NASHORN : ScriptEngineProvider.valueOf(scriptEngineProviderProperty.trim().toUpperCase());

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        log.info("Script Engine Provider : " + provider);
        return provider;
    }
}
