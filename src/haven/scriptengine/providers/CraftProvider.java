package haven.scriptengine.providers;

import haven.Makewindow;
import haven.UI;

/**
 * Created by IntelliJ IDEA.
 * Date: 19.06.11
 * Time: 22:58
 *
 * @author Vlad.Rassokhin@gmail.com
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CraftProvider {

    // есть ли окно крафта с заданным заголовком
    public static boolean isOpened(String wnd) {
        Makewindow mw = UI.makeWindow.get();
        return mw != null && mw.receiptName.equals(wnd) && mw.isReady();
    }

    // готово ли окно крафта?
    public static boolean isReady() {
        return UI.makeWindow.get() != null && UI.makeWindow.get().isReady();
    }

    /**
     * Starts crafting
     * @param cont continue crafting if can
     */
    public static void craft(boolean cont) {
        if (UI.makeWindow.get() != null) {
            UI.instance.wdgmsg(UI.makeWindow.get(), "make", cont ? 1 : 0);
        }
    }
}
