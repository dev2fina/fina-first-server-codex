package net.fina.server.processing.script;

import net.fina.server.processing.script.js.JSTreeBase;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;

import javax.script.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptEngineNashorn implements ScriptEngineBase {
    private static volatile ScriptEngineNashorn _instance;
    private final Logger log = Logger.getLogger(getClass());
    private final Map<String, CompiledScript> compiledScripts = new ConcurrentHashMap<>();
    private final ScriptEngine engine;

    private ScriptEngineNashorn() {

        try (StatisticsLogger statLog = new StatisticsLogger("Retrieve script engine");) {
            statLog.logMessage("Start Script engine load");
            statLog.logStage("Create script engine manager");
            ScriptEngineManager manager = new ScriptEngineManager();
            statLog.logStage("Find JavaScript engine...");
            engine = manager.getEngineByName("JavaScript");

            statLog.logMessage("Script engine Name:" + engine.getFactory().getEngineName());
            statLog.logMessage("Script engine Version:" + engine.getFactory().getEngineVersion());
        }
    }

    public static ScriptEngineNashorn get() {
        if (_instance == null) {
            synchronized (ScriptEngineNashorn.class) {
                if (_instance == null) {
                    _instance = new ScriptEngineNashorn();
                }
            }
        }
        return _instance;
    }


    @Override
    public synchronized String call(JSTreeBase tree, String source) {
        Object value = callObject(tree, source);
        if (value != null) {
            return value.toString();
        }

        return null;
    }

    @Override
    public synchronized Object callObject(JSTreeBase tree, String source) {
        try {

            CompiledScript cs = get(source);

            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("tree", tree);

            cs.eval(bindings);

            Invocable invocable = (Invocable) cs.getEngine();
            Object returnValue = invocable.invokeFunction("fina2_mdt_node");

            if (returnValue != null) {
                return returnValue;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("SCRIPT: " + source);
        }
        return null;
    }

    @Override
    public CompiledScript compile(String script, String nodeCode) {
        CompiledScript cs = null;
        try {
            Compilable eng = (Compilable) engine;
            cs = eng.compile(script);
            compiledScripts.put(script, cs);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            if (nodeCode != null) {
                log.error("Node Code:" + nodeCode);
            }
            log.error("SCRIPT:" + script);
        }
        return cs;
    }

    public CompiledScript get(String script) {
        CompiledScript cs = compiledScripts.get(script);
        if (cs == null) {
            cs = compile(script, null);
        }
        return cs;
    }

    @Override
    public CompiledScript find(String script) {
        return compiledScripts.get(script);
    }
}
