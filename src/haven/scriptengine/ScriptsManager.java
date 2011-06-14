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
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        interpreter.setOut(java.lang.System.out);
        interpreter.setErr(java.lang.System.err);
        interpreter.exec("import includes");
        interpreter.set("util", Util.class);
        interpreter.set("player", Player.class);
        interpreter.set("config", Config.class);
        interpreter.set("manager", ScriptsManager.class);
//        PrintStream stream = new PrintStream(CustomConsole.CustomWriter.getInstance());
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
        } catch (PyException e) {
            logger.error("Jython engine initialization exception", e);
        }
        try {
            relinkInterpreterScope();
        } catch (PyException e) {
            logger.error("Cannot relink interpreter scope", e);
        }
    }

    private static Class<? extends Bot> getBotClass(String name) {
        return botsMap.get(name);
    }

    static final Map<Bot, Thread> runningBots = new ConcurrentHashMap<Bot, Thread>();
    static final ThreadGroup tg = new ThreadGroup("Scripts thread groop");

    public static boolean runBot(String name) {
        Bot bot = createBot(name);
        if (bot == null) return false;

        Thread thread = new Thread(tg, bot);
        thread.setDaemon(true);
        runningBots.put(bot, thread);

        thread.start();
        return true;
    }

    public static void killBot(Bot bot) {
        if (!runningBots.containsKey(bot)) return;
        runningBots.get(bot).interrupt();
    }

    static Bot createBot(String name) {
        if (!botsMap.containsKey(name)) return null;
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

    public static void registerOut(Writer w) {
        interpreter.setOut(w);
    }

    public static void registerErr(Writer w) {
        interpreter.setErr(w);
    }
}
