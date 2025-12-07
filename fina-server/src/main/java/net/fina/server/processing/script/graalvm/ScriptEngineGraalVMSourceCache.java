package net.fina.server.processing.script.graalvm;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.fina.server.processing.script.graalvm.ScriptEngineGraalVM.FINA_FUNCTION_NAME;

public class ScriptEngineGraalVMSourceCache {
    private static final Map<String, Source> scriptCache = new ConcurrentHashMap<>();
    private static final Context context = GraalVMContextBuilder.create();

    public static Source getOrCompileScript(String script) {
        return scriptCache.computeIfAbsent(script, key -> {
            context.eval("js", script);
            try {
                return Source.newBuilder("js", script, FINA_FUNCTION_NAME).build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Source get(String script) {
        return scriptCache.get(script);
    }

}
