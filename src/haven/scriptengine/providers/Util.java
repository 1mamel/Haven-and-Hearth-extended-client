package haven.scriptengine.providers;

import haven.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 22:41
 */
@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
public class Util {

    /**
     * Finds object by type near player
     *
     * @param type   object type
     * @param radius searching radius
     * @return object id of nearest object of that type, 0 otherwise
     */
    static int findObjectByType(String type, int radius) {
        Coord my = Player.getPosition();
        double foundedDistance = radius * 11;
        Gob foundedObject = null;

        synchronized (CustomConfig.glob.oc) {
            for (Gob gob : CustomConfig.glob.oc) {
                boolean matched = false;
                if (type.equals("tree")) {
                    // searching for trees with growth stage 0
                    String resName = gob.getResName();
                    matched = resName.contains("trees") && resName.indexOf('0') >= 0;
                }

                if (matched) {
                    double len = gob.getc().dist(my);
                    if (len < foundedDistance) {
                        foundedDistance = len;
                        foundedObject = gob;
                    }
                }
            }
        }
        if (foundedObject != null) {
            return foundedObject.id;
        } else {
            return 0;
        }
    }

    public static TextInputBox ask(final String question, final Callback<String> callback) {
        return ask("Question", question, callback);
    }

    public static TextInputBox ask(final String capture, final String question, final Callback<String> callback) {
        return new TextInputBox(MessageBox.DC, MessageBox.DS, UI.instance.root, capture, question, callback);
    }

    public static String ask(final String question) {
        return ask("Question", question);
    }

    public static String ask(final String capture, final String question) {
        final AtomicReference<String> ars = new AtomicReference<String>();
        final AtomicBoolean ab = new AtomicBoolean(false);
        ars.set(null);
        final Callback<String> callback = new Callback<String>() {
            @Override
            public void result(String result) {
                synchronized (ab) {
                    ars.set(result);
                    ab.set(true);
                    ab.notifyAll();
                }
            }
        };
        new TextInputBox(MessageBox.DC, MessageBox.DS, UI.instance.root, capture, question, callback);
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (ars) {
            while (!ab.get()) try {
                ab.wait();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }

        return ars.get();
    }

    public static MessageBox dialog(final String message, int buttons, final Callback<Integer> callback) {
        return dialog("Dialog", message, buttons, callback);
    }

    public static MessageBox dialog(final String capture, final String message, int buttons, final Callback<Integer> callback) {
        return new MessageBox(MessageBox.DC, MessageBox.DS, UI.instance.root, capture, message, buttons, callback);
    }

    public static int dialog(final String message, int buttons) {
        return dialog("Dialog", message, buttons);
    }

    public static int dialog(final String capture, final String message, int buttons) {
        final AtomicInteger ai = new AtomicInteger(-1);
        final Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void result(Integer result) {
                synchronized (ai) {
                    ai.set(result);
                    ai.notifyAll();
                }
            }
        };
        new MessageBox(MessageBox.DC, MessageBox.DS, UI.instance.root, capture, message, buttons, callback);
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (ai) {
            while (ai.get() == -1) try {
                ai.wait();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
        }

        return ai.get();
    }

    public static MessageBox inform(final String message) {
        return new MessageBox(MessageBox.DC, MessageBox.DS, UI.instance.root, "Info", message);
    }

    public static MessageBox inform(final String capture, final String message) {
        return new MessageBox(MessageBox.DC, MessageBox.DS, UI.instance.root, capture, message);
    }

}
