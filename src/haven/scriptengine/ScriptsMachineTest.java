package haven.scriptengine;

import junit.framework.TestCase;
import sun.font.Script;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 23:20
 */
public class ScriptsMachineTest extends TestCase {
    public void testOne() throws Exception {
        //engine.put("player", UserInfo.getInstance());

//         // JavaScript code in a String
//        String script = "var obj = new Object(); obj.run = function() { println('run method called'); }";
//
//        // evaluate script
//        engine.eval(script);
//
//        // get script object on which we want to implement the interface with
//        Object obj = engine.get("obj");
//
//        Invocable inv = (Invocable) engine;
//
//        // get Runnable interface object from engine. This interface methods
//        // are implemented by script methods of object 'obj'
//        Runnable r = inv.getInterface(obj, Runnable.class);
//
//        // start a new thread that runs the script implemented
//        // runnable interface
//        Thread th = new Thread(r);
//        th.start();
        // evaluate JavaScript code from String

    }

    public void testConfigProvider() throws Exception {
//        ScriptsMachine.executeScript("println(config);");
        ScriptsMachine.executeScript("config.setNightvision(true);");
        ScriptsMachine.executeScript("println(config.getNightvision())");
        ScriptsMachine.executeScript("config.setIrc(true);");
        ScriptsMachine.executeScript("println(config.getIrc())");
//        ScriptsMachine.executeScript("println(eprintln)");
//        ScriptsMachine.executeScript("eprintln(println)");
        //ScriptsMachine.executeScript("config.setScreenSize(1024,768);");

    }
}
