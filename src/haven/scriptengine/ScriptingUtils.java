package haven.scriptengine;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 22:41
 */
@SuppressWarnings({"MethodMayBeStatic"})
class ScriptingUtils {

    private static final ScriptingUtils ourInstance = new ScriptingUtils();

    static ScriptingUtils getInstance() {
        return ourInstance;
    }

    public void delay (int millis) {
        long reqStart = System.currentTimeMillis();
        long reqNow = 0;
        boolean isTimeout = false;
        do {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException ignored) {
            }
            reqNow = System.currentTimeMillis();
            isTimeout = ((reqNow - reqStart) > millis);
        } while (!isTimeout);
    }

}
