package haven.scriptengine.providers;

import haven.CharWnd;
import haven.StudyWidget;
import haven.UI;

/**
 * Created by IntelliJ IDEA.
 * Date: 19.06.11
 * Time: 23:10
 *
 * @author Vlad.Rassokhin@gmail.com
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CharStatsProvider {

//    public static int getFreeLP() {
//    }

    public static boolean isCharStatsOpened() {
        return CharWnd.instance.get() != null;
    }

    public static void toggleCharStats() {
        UI.instance.slen.wdgmsg("chr");
    }

    public static int getAttentionLimit() {
        final StudyWidget sw = StudyWidget.instance.get();
        if (sw == null) {
            return -1;
        }
        return sw.getAttentionLimit();
    }

    public static int getAttention() {
        final StudyWidget sw = StudyWidget.instance.get();
        if (sw == null) {
            return -1;
        }
        return sw.getAttention();
    }
}
