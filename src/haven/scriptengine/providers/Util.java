package haven.scriptengine.providers;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 22:41
 */
@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
public class Util {

    private static final Util ourInstance = new Util();

    public static Util getInstance() {
        return ourInstance;
    }

    public void delay(int millis) {
        long reqStart = System.currentTimeMillis();
        long reqNow = 0;
        boolean isTimeout = false;
        do {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new Error(e);
            }
            reqNow = System.currentTimeMillis();
            isTimeout = ((reqNow - reqStart) > millis);
        } while (!isTimeout);
    }

}
