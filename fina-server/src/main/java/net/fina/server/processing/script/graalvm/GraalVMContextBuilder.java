package net.fina.server.processing.script.graalvm;

import org.graalvm.polyglot.Context;

public class GraalVMContextBuilder {
    public static Context create() {
        /*
        * JIT Compile is disabled in GraalVM JDK Community Edition
        * */
       return Context.newBuilder("js")
               .allowAllAccess(true)  // Allow Java interop
               // Disable interpreter warnings if graalvm jkd is used this should be true and JIT compiler should be configured
               .option("engine.WarnInterpreterOnly", "false")
               .build();
    }
}
