package net.fina.server.processing.script;

import net.fina.server.processing.script.js.JSTreeBase;

public interface ScriptEngineBase {
    String FINA_FUNCTION_NAME = "fina2_mdt_node";

    String call(JSTreeBase tree, String source);

    Object callObject(JSTreeBase tree, String source);

    Object compile(String equation, String nodeCode);

    Object find(String script);

    static String createFunction(String equation) {
        return "function " + FINA_FUNCTION_NAME + "() {\n" + equation + "\n}";
    }
}
