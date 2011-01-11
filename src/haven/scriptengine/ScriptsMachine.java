package haven.scriptengine;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 21:41
 */

import haven.CustomConsole;

import javax.script.*;

public class ScriptsMachine {
    static ScriptEngine engine;
    static ScriptContext defaultContext;

    public static void executeScript(String script) {
        try {
            engine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }


    private static void relinkEngineScope() {
//        Bindings engineScope = defaultContext.getBindings(ScriptContext.ENGINE_SCOPE);
        engine.put("utils", ScriptingUtils.getInstance());
        engine.put("player", UserInfo.getInstance());
        engine.put("config", ConfigProvider.getInstance());
        engine.getContext().setWriter(CustomConsole.OutWriter.getInstance());
        engine.getContext().setErrorWriter(CustomConsole.OutWriter.getInstance());
//        defaultContext.setWriter(new CustomConsole.OutWriter());
//        defaultContext.setErrorWriter(new CustomConsole.OutWriter());
//        engine.setBindings(engineScope, ScriptContext.ENGINE_SCOPE);
//        engine.setContext(defaultContext);
    }

    static {
        try {
            ScriptEngineManager factory = new ScriptEngineManager(); // create a script engine manager
            engine = factory.getEngineByName("JavaScript"); // create a JavaScript engine
            defaultContext = engine.getContext();
            relinkEngineScope();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
