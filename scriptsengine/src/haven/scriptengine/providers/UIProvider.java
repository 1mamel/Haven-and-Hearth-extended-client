package haven.scriptengine.providers;

import haven.*;

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
@SuppressWarnings({"UnusedDeclaration"})
public class UIProvider {

    public static TextInputBox ask(final String question, final Callback<String> callback) {
        return ask("Question", question, callback);
    }

    public static TextInputBox ask(final String capture, final String question, final Callback<String> callback) {
        return new TextInputBox(MessageBox.DC, MessageBox.DS, UI.instance.root, capture, question, callback);
    }

    public static String askWait(final String question) {
        return askWait("Question", question);
    }

    public static String askWait(final String capture, final String question) {
        final AtomicReference<String> ars = new AtomicReference<String>();
        final AtomicBoolean ab = new AtomicBoolean(false);
        ars.set(null);
        final Callback<String> callback = new Callback<String>() {
            @Override
            public void result(final String result) {
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

    public static MessageBox dialog(final String message, final int buttons, final Callback<Integer> callback) {
        return dialog("Dialog", message, buttons, callback);
    }

    public static MessageBox dialog(final String capture, final String message, final int buttons, final Callback<Integer> callback) {
        return new MessageBox(MessageBox.DC, MessageBox.DS, UI.instance.root, capture, message, buttons, callback);
    }

    public static int dialog(final String message, final int buttons) {
        return dialog("Dialog", message, buttons);
    }

    public static int dialog(final String capture, final String message, final int buttons) {
        final AtomicInteger ai = new AtomicInteger(-1);
        final Callback<Integer> callback = new Callback<Integer>() {
            @Override
            public void result(final Integer result) {
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


    // есть ли на экране контекстное меню действий
    public static boolean haveFlowerMenu() {
        return (UI.flowerMenu.get() != null);
    }

//----------------------------------------------------------------------------------------------------------------------

    // готово ли контекстное меню к приему команды
    public static boolean isFlowerMenuReady() {
        return (UI.flowerMenu.get() != null);// && (ui.flower_menu.isReady());
    }

    // выбрать пункт в контекстном меню действий
    public static void selectFlowerMenuOpt(final String name) {
        if (!haveFlowerMenu()) {
//            LogPrint("ERROR: flower menu does not exist!");
            return;
        }
        if (!isFlowerMenuReady()) {
//            LogPrint("ERROR: flower menu not ready!");
            return;
        }
//        LogPrint("select flower menu option: "+name);
        UI.flowerMenu.get().select(name);
    }

    // послать действие на сервер из меню действий внизу справа
    public static void sendAction(final String name) {
        if (UI.menuGrid.get() == null) {
            return;
        }
        if (name.equals("laystone")) {
            UI.menuGrid.get().wdgmsg("act", "stoneroad", "stone");
        } else {
            UI.menuGrid.get().wdgmsg("act", name);
        }
    }

    // послать действие на сервер из меню действий внизу справа
    public static void sendAction(final String name1, final String name2) {
        if (name2.isEmpty()) {
            sendAction(name1);
            return;
        }
        if (UI.menuGrid.get() == null) {
            return;
        }
        UI.menuGrid.get().wdgmsg("act", name1, name2);
    }


    // сказать пользователю выбрать объект мышью
    public static int inputObject(final String msg) {
        final MapView mapview = MapProvider.getMV();
        if (mapview == null) {
            return 0;
        }
        final MessageBox inform = inform("Select map object", msg);
        mapview.modeSelectObject.set(true);
        synchronized (mapview.modeSelectObject) {
            mapview.modeSelectObject.notifyAll();
        }

        synchronized (mapview.modeSelectObject) {
            while (mapview.modeSelectObject.get()) {
                try {
                    mapview.modeSelectObject.wait();
                } catch (InterruptedException e) {
                    throw new Error(e);
                }
            }
        }
        inform.close();
        if (mapview.onmouse != null) {
            return mapview.onmouse.id;
        }

        return 0;
    }


    // текущий список итемов
//    public static List<Item> inventory_list = null;
    // текущая позиция в списке итемов
//    public static int current_item_index = 0;
    // индекс в эквипе для получения вещи
    public static int current_equip_index = 0;

    enum ItemMode {
        INVENTORY(0),
        DRAGGING(1),
        EQUIPORY(2);
        private int type;
        static ItemMode DEFAULT = INVENTORY;

        ItemMode(final int type) {
            this.type = type;
        }

        static ItemMode fromType(final int type) {
            for (final ItemMode mode : values()) {
                if (mode.type == type) {
                    return mode;
                }
            }
            return DEFAULT;
        }
    }

    public static void equipAction(final int slot, final String action) {
        if (UI.equipory.get() == null || !InventoriesProvider.actions.contains(action)) {
            return;
        }
        if (action.equals("itemact")) {
            UI.equipory.get().wdgmsg("itemact", slot);
        } else {
            UI.equipory.get().wdgmsg(action, slot, new Coord(10, 10));
        }
    }


    public static void setRenderMode(final boolean enabled) {
        CustomConfig.setRender(enabled);
    }


    // Building

    public static void buildClick() {
        final ISBox b = UI.instance.root.findchild(ISBox.class);
        if (b != null) {
            final Widget w = b.parent;
            final Button btn = w.findchild(Button.class);
            if (btn != null) {
                btn.click();
            }
        }
    }

    public static boolean haveBuildWindow() {
        final ISBox b = UI.instance.root.findchild(ISBox.class);
        return (b != null);
    }

}
