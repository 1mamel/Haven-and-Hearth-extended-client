package haven.scriptengine;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 13.06.11
 * Time: 23:58
 *
 * @author Vlad.Rassokhin@gmail.com
 */

/**
 * Main script engine class.
 * Must be overrided in scripts.
 */
@SuppressWarnings({"UnusedDeclaration", "MethodMayBeStatic"})
public abstract class Bot implements Runnable {
    public String about() {
        return "No information provided";
    }

    public String author() {
        return "No information about author provided";
    }

    public abstract void run();

    public void onExit() {
    }

    public final void kill() {
        onExit();
        ScriptsManager.kill(this);
    }
}
