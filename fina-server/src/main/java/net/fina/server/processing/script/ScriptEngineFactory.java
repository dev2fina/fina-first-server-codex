package net.fina.server.processing.script;

import net.fina.common.client.property.PropertyKeys;
import net.fina.server.processing.script.graalvm.ScriptEngineGraalVMPool;
import org.jboss.logging.Logger;

public class ScriptEngineFactory {
    private static final Logger log = Logger.getLogger(ScriptEngineFactory.class);

    private ScriptEngineFactory() {
    }

    public static ScriptEngineBase get() {

        ScriptEngineProvider engineProvider = getScriptEngineProvider();

        if (engineProvider == ScriptEngineProvider.GRAALVM) {
            return ScriptEngineGraalVMPool.getEngine();
        } else {
            return ScriptEngineNashorn.get();
        }

    }


    private static ScriptEngineProvider getScriptEngineProvider() {
        ScriptEngineProvider provider = ScriptEngineProvider.NASHORN;
        try {
            String providerName = System.getProperty(PropertyKeys.SCRIPT_ENGINE_PROVIDER);
            providerName = providerName == null ? ScriptEngineProvider.NASHORN.name() : providerName;
            provider = ScriptEngineProvider.valueOf(providerName.toUpperCase());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return provider;
    }
}
