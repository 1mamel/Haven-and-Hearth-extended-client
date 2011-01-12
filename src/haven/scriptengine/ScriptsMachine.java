package haven.scriptengine;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 21:41
 */

import haven.CustomConsole;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.PrintStream;

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
        engine.setContext(engine.getContext());
        engine.put("util", ScriptingUtils.getInstance());
        engine.put("player", UserInfo.getInstance());
        engine.put("config", ConfigProvider.getInstance());
        engine.put("system", SystemInterface.getInstance());
        SystemInterface.getInstance().setOut(new PrintStream(CustomConsole.OutStream.getInstance()));
        SystemInterface.getInstance().setErr(new PrintStream(CustomConsole.OutStream.getInstance()));
//        engine.put("out", System.out);
        try {
            engine.eval("print = function (a) {system.print(a);}; println = function (a) {system.println(a);};");
            engine.eval("var eprint = function (a) {system.eprint(a);}; var eprintln = function (a) {system.eprintln(a);};");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
//        defaultContext.setWriter(new CustomConsole.OutStream());
//        defaultContext.setErrorWriter(new CustomConsole.OutStream());
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
