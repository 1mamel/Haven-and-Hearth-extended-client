package haven.scriptengine.providers.providers;

import haven.Buff;
import haven.UI;

/**
 * Created by IntelliJ IDEA.
 * Date: 19.06.11
 * Time: 23:05
 *
 * @author Vlad.Rassokhin@gmail.com
 */
@SuppressWarnings({"UnusedDeclaration"})
public class BuffsProvider {
    // текущий индекс в бафф листе
    public static int current_buff_index = -1;

    public static void resetBuffsIterator() {
        current_buff_index = -1;
    }

    public static int nextBuff() {
        current_buff_index++;
        synchronized (UI.instance.sess.glob.buffs) {
            return (current_buff_index < UI.instance.sess.glob.buffs.values().size()) ? 1 : 0;
        }
    }

    public static int getBuffMeter() {
        synchronized (UI.instance.sess.glob.buffs) {
            if (current_buff_index < UI.instance.sess.glob.buffs.values().size() && current_buff_index >= 0) {
                int i = 0;
                for (final Buff b : UI.instance.sess.glob.buffs.values()) {
                    if (i == current_buff_index) {
                        return b.ameter;
                    }
                    i++;
                }
            }
        }
        return 0;
    }

    public static int getBuffTimeMeter() {
        synchronized (UI.instance.sess.glob.buffs) {
            if (current_buff_index < UI.instance.sess.glob.buffs.values().size() && current_buff_index >= 0) {
                int i = 0;
                for (final Buff b : UI.instance.sess.glob.buffs.values()) {
                    if (i == current_buff_index) {
                        return b.getTimeLeft();
                    }
                    i++;
                }
            }
        }
        return 0;
    }

    public static boolean isBuffNameContains(final String str) {
        synchronized (UI.instance.sess.glob.buffs) {
            if (current_buff_index < UI.instance.sess.glob.buffs.values().size()) {
                int i = 0;
                for (final Buff b : UI.instance.sess.glob.buffs.values()) {
                    if (i == current_buff_index) {
                        return (b.getName().contains(str));
                    }
                    i++;
                }

            }
        }
        return false;
    }
}
