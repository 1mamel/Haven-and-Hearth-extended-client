package haven.scriptengine.providers;

import haven.UI;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 22:41
 */
@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
public class Util {


    public static void logout() {
        UI.instance.sess.close();
    }

}
