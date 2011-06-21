package haven.scriptengine;

import junit.framework.TestCase;

import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 23:20
 */
public class ScriptsManagerTest extends TestCase {
    public void testOne() throws Exception {
        //engine.put("player", Player.getInstance());

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
        ScriptsManager.exec("config.nightvision = True");
        ScriptsManager.exec("print config.nightvision");
        ScriptsManager.exec("config.irc = True");
        ScriptsManager.exec("print config.irc");
        ScriptsManager.exec("config.nightvision = False");
        ScriptsManager.exec("print config.nightvision");
        ScriptsManager.exec("config.irc = False");
        ScriptsManager.exec("print config.irc");

    }

    public void testJythonBasics() throws Exception {
        ScriptsManager.exec("print util");
        ScriptsManager.exec("print player");
        ScriptsManager.exec("print config");
        ScriptsManager.exec("print manager");
        ScriptsManager.exec("manager");
    }

    public void testSimpleBot() throws Exception {
        ScriptsManager.exec("import simplebot");
        assertTrue(ScriptsManager.containsBot("simplebot"));
        Bot bot = ScriptsManager.createBot("simplebot");
        System.out.println("About:" + bot.about());
        System.out.println("Author:" + bot.author());
        System.out.println("\tstarting bot...\n");
        bot.run();
        System.out.println("\n\tbot stopped");
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String line;
        while (!(line = in.nextLine()).equals("exit")) {
            ScriptsManager.exec(line);
        }

    }

    public void testSoveBot() throws Exception {
        ScriptsManager.registerOut(new PrintWriter(System.out));
        ScriptsManager.registerErr(new PrintWriter(System.err));
        ScriptsManager.exec("import sovebot");
//        assertTrue(ScriptsManager.containsBot("sovebot"));
        ScriptsManager.runWait("sovebot");
    }

    public void testFiller() throws Exception {
        ScriptsManager.registerOut(new PrintWriter(System.out));
        ScriptsManager.registerErr(new PrintWriter(System.err));
        ScriptsManager.exec("import barrelfiller");
//        assertTrue(ScriptsManager.containsBot("sovebot"));
        ScriptsManager.runWait("filler");
    }
}
