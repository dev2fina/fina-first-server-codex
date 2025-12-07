package net.fina.server.processing.script.graalvm;

import net.fina.server.processing.script.ScriptEngineBase;
import org.jboss.logging.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ScriptEngineGraalVMPool {


    public static final int POOL_SIZE = 50;
    private static final BlockingQueue<ScriptEngineBase> enginePool = new LinkedBlockingQueue<>(POOL_SIZE);
    private static final Logger log = Logger.getLogger(ScriptEngineGraalVMPool.class.getName());


    static {
        for (int i = 0; i < POOL_SIZE; i++) {
            enginePool.add(new ScriptEngineGraalVM("ScriptEngineGraalVMPool-" + i));
        }
    }

    public static ScriptEngineBase getEngine() {
        try {
            ScriptEngineBase eng = enginePool.poll();
            log.debug("script engine pool available size: " + enginePool.size());
            if (enginePool.size() <= 1) {
                log.warn("script engine pool available size: " + enginePool.size());
            }
            return eng == null ? new ScriptEngineGraalVM("Default GraalVM Script Engine") : eng;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        throw new RuntimeException("No script engine available");
    }

    public static void releaseEngine(ScriptEngineBase engine) {
        if (enginePool.size() < POOL_SIZE) {
            enginePool.offer(engine);
        }
    }


}