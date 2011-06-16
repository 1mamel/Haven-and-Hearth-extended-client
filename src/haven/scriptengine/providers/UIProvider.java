package haven.scriptengine.providers;

import haven.Callback;
import haven.MessageBox;
import haven.TextInputBox;
import haven.UI;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 * Date: 17.06.11
 * Time: 2:41
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class UIProvider {

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
