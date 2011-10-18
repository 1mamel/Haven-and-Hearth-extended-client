package haven.scriptengine;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 21:41
 */

import haven.CustomConfig;
import haven.IMeter;
import haven.ProgressBar;
import haven.scriptengine.providers.Config;
import haven.scriptengine.providers.Player;
import haven.scriptengine.providers.UIProvider;
import haven.scriptengine.providers.Util;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptsManager {
    static PythonInterpreter interpreter;
    static Logger logger;

    public static void exec(final String line) {
        try {
            final Thread thread = new Thread(tg, new Runnable() {
                @Override
                public void run() {
                    interpreter.exec(line);
                    CustomConfig.setRender(true);
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (PyException e) {
            System.err.println("Executing line\"" + line + "\" failed");
            e.printStackTrace();
            logger.error("Executing line\"" + line + "\" failed", e);
        }
    }

    static final HashMap<String, Class<? extends Bot>> botsMap = new HashMap<String, Class<? extends Bot>>();

    private static HashMap<String, String> commands = new HashMap<String, String>();

    @SuppressWarnings({"UnusedDeclaration"})
    public static boolean registerBot(@NotNull final String name, @NotNull final Class<? extends Bot> clazz) {
        botsMap.put(name, clazz);
        return true;
    }

    private static void relinkInterpreterScope() {
        interpreter.setOut(java.lang.System.out);
        interpreter.setErr(java.lang.System.err);
        interpreter.exec("import includes");
        interpreter.set("ui", UIProvider.class);
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
            final PySystemState pySysState = new PySystemState();
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
        try {
            scanDirectory();
        } catch (PyException e) {
            logger.error("Preloading scripts failed", e);
        }
    }

    @Nullable
    private static Class<? extends Bot> getBotClass(@NotNull final String name) {
        return botsMap.get(name);
    }

    static final Map<Bot, Thread> runningBots = new ConcurrentHashMap<Bot, Thread>();
    static final ThreadGroup tg = new ThreadGroup("Scripts thread groop");

    static boolean runWait(@NotNull final String name) {
        final Bot bot = createBot(name);
        if (bot == null) {
            return false;
        }

        runningBots.put(bot, Thread.currentThread());
        bot.run();
        return true;
    }

    public static void alias(@NotNull final String command, @NotNull final String botName) {
        commands.put(command, botName);
        interpreter.exec("def " + command + "(): manager.run(" + botName + ");");
    }

    public static boolean runBot(@NotNull final String name) {
        final Bot bot = createBot(name);
        if (bot == null) return false;

        final Thread thread = new Thread(tg, bot);
        thread.setDaemon(true);
        runningBots.put(bot, thread);

        thread.start();
        return true;
    }

    public static boolean run(@NotNull final String name) {
        return runBot(name);
    }

    public static void kill(@NotNull final Bot bot) {
        killBot(bot);
    }

    public static void killBot(@NotNull final Bot bot) {
        if (!runningBots.containsKey(bot)) return;
        runningBots.get(bot).interrupt();
        CustomConfig.setRender(true);
    }

    @Nullable
    static Bot createBot(@NotNull final String name) {
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

    public static boolean containsBot(@NotNull final String name) {
        return botsMap.containsKey(name);
    }

    public static boolean containsBot(@NotNull final Class<? extends Bot> clazz) {
        return botsMap.containsValue(clazz);
    }

    public static void registerOut(@NotNull final Writer w) {
        interpreter.setOut(w);
    }

    public static void registerErr(@NotNull final Writer w) {
        interpreter.setErr(w);
    }

    public static void scanDirectory() {
        final File dir = new File("scripts");
        if (!dir.isDirectory()) return;
        for (final String file : dir.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith("bot.py");
            }
        })) {
            exec("import " + file);
        }
    }

    public static void initSystem() {
        final Player player = new Player();
        IMeter.subscribe(player);
        ProgressBar.subscribe(player);
    }
}
