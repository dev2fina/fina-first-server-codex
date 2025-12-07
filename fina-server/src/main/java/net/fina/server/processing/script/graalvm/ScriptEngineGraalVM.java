package net.fina.server.processing.script.graalvm;

import net.fina.server.processing.script.ScriptEngineBase;
import net.fina.server.processing.script.js.JSTreeBase;
import net.fina.common.server.StatisticsLogger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.jboss.logging.Logger;

public class ScriptEngineGraalVM implements ScriptEngineBase {
    private final Logger log = Logger.getLogger(getClass());
    private final Context context;
    private final String instanceName;

    public ScriptEngineGraalVM(String instanceName) {
        try (StatisticsLogger statLog = new StatisticsLogger("Retrieve script engine [graalvm]");) {
            this.instanceName = instanceName;

            statLog.logMessage("Start Script engine load");
            statLog.logStage("Initialize graalvm context");

            context = GraalVMContextBuilder.create();
        }
    }


    @Override
    public String call(JSTreeBase tree, String source) {
        Object value = callObject(tree, source);
        if (value != null) {
            return value.toString();
        }

        return null;
    }

    @Override
    public Object callObject(JSTreeBase tree, String source) {
        try {
            Source compiledSource = getFunction(source);
            // Bind the tree object to the context so it can be used inside the script
            context.getBindings("js").putMember("tree", tree);
            context.eval(compiledSource);

            Value function = context.getBindings("js").getMember(FINA_FUNCTION_NAME);

            Value res = function.execute(tree);
            if (res.isNumber()) {
                return res.asDouble();
            }

            return res.asString();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            log.error("SCRIPT: " + source);
        } finally {
            ScriptEngineGraalVMPool.releaseEngine(this);
        }

        return null;
    }

    @Override
    public Source compile(String script, String nodeCode) {
        Source compiledSource = null;
        try {
            compiledSource = find(script);
            if (compiledSource == null) {
                return getFunction(script);
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            if (nodeCode != null) {
                log.error("Node Code:" + nodeCode);
            }
            log.error("SCRIPT:" + script);
        } finally {
            ScriptEngineGraalVMPool.releaseEngine(this);
        }
        return compiledSource;
    }

    @Override
    public Source find(String script) {
        try {
            return ScriptEngineGraalVMSourceCache.get(script);
        } finally {
            ScriptEngineGraalVMPool.releaseEngine(this);
        }
    }

    private Source getFunction(String script) {
        return ScriptEngineGraalVMSourceCache.getOrCompileScript(script);
    }

    @Override
    public String toString() {
        return "ScriptEngineGraalVM{" +
                "instanceName='" + instanceName + '\'' +
                '}';
    }
}
