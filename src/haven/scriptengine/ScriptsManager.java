package haven.scriptengine;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 21:41
 */

import haven.scriptengine.providers.Config;
import haven.scriptengine.providers.Player;
import haven.scriptengine.providers.Util;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.util.HashMap;

public class ScriptsManager {
    static PythonInterpreter interpreter;
    static Logger logger;

    public static void exec(String line) {
        try {
            interpreter.exec(line);
        } catch (PyException e) {
            logger.error("Executing line\"" + line + "\" failed", e);
        }
    }

    static final HashMap<String, Class<? extends Bot>> botsMap = new HashMap<String, Class<? extends Bot>>();

    @SuppressWarnings({"UnusedDeclaration"})
    public static boolean registerBot(String name, Class<? extends Bot> clazz) {
        return !botsMap.containsKey(name) && botsMap.put(name, clazz) == null;
    }

    private static void relinkInterpreterScope() {
        interpreter.set("util", Util.getInstance());
        interpreter.set("player", Player.getInstance());
        interpreter.set("config", Config.getInstance());
        interpreter.set("manager", ScriptsManager.class);
        interpreter.setOut(java.lang.System.out);
        interpreter.setErr(java.lang.System.err);
//        PrintStream stream = new PrintStream(CustomConsole.OutStream.getInstance());
//        interpreter.setOut(stream);
//        interpreter.setErr(stream);
    }

    static {
        logger = Logger.getLogger(ScriptsManager.class);
        try {
            logger.addAppender(new FileAppender(new SimpleLayout(), "scripts_manager.log"));
        } catch (IOException ignored) {
        }
        logger.addAppender(new ConsoleAppender(new SimpleLayout()));
        try {
            PySystemState pySysState = new PySystemState();
            pySysState.path.append(Py.newString("scripts"));
            pySysState.path.append(Py.newString("../scripts"));
            Py.setSystemState(pySysState);
            interpreter = new PythonInterpreter();
            relinkInterpreterScope();
        } catch (PyException e) {
            logger.error("Jython engine initialization exception", e);
        }
    }

    public static Class<? extends Bot> getBotClass(String name) {
        return botsMap.get(name);
    }

    public static Bot createBot(String name) {
        try {
            return botsMap.get(name).newInstance();
        } catch (InstantiationException e) {
            logger.error("Bot \"" + name + "\" instanciation error", e);
        } catch (IllegalAccessException e) {
            logger.error("Bot \"" + name + "\" instanciation error", e);
        }
        return null;
    }

    public static boolean containsBot(String name) {
        return botsMap.containsKey(name);
    }

    public static boolean containsBot(Class<? extends Bot> clazz) {
        return botsMap.containsValue(clazz);
    }
}
