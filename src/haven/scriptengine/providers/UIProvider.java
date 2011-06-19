package haven.scriptengine.providers;

import haven.*;
import haven.scriptengine.InventoryExt;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
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

    private static InventoryExt.ItemsIterator inventoryIter;

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
    public static void selectFlowerMenuOpt(String name) {
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
    public static void sendAction(String name) {
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
    public static void sendAction(String name1, String name2) {
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
    public static int inputObject(String msg) {
        MapView mapview = MapProvider.getMV();
        if (mapview == null) {
            return 0;
        }
        MessageBox inform = inform("Select map object", msg);
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


    // проверить наличие инвентаря по имени
    public static boolean haveInventory(String name) {
        return InventoryExt.hasInventory(name);
    }

    // вернуть инвентарь по имени
    public static InventoryExt getInventory(String name) {
        return InventoryExt.getInventory(name);
    }

    /**
     * Open user Inventory window.
     * Sends message to server, you wants to wait some time for appear.
     */
    public static void toggleUserInventory() {
        UI.instance.root.wdgmsg("gk", KeyEvent.VK_TAB);
    }

    // поставить текущий инвентарь, после автоматически сбрасывается итератор
    public static int setInventory(String name) {
        currentInventory = InventoryExt.getInventory(name);
        if (currentInventory != null) {
            resetInventoryIter();
            return 1;
        } else {
            return 0;
        }
//        Widget root = UI.instance.root;
//        for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
//            if (wdg instanceof Window && ((Window) wdg).getCaption() != null && ((Window) wdg).getCaption().equals(name))
//                for (Widget inv = wdg.child; inv != null; inv = inv.next)
//                    if (inv instanceof InventoryExt) {
//                        currentInventory = (InventoryExt) inv;
//                        resetInventoryIter();
//                        return 1;
//                    }
//        }
//        currentInventory = null;
//        return 0;
    }

    // виджет текущего инвентаря с которым работаем
    public static InventoryExt currentInventory = null;
    // текущий список итемов
//    public static List<Item> inventory_list = null;
    // текущая позиция в списке итемов
//    public static int current_item_index = 0;
    // индекс в эквипе для получения вещи
    public static int current_equip_index = 0;
    // режим получения вещи
    public static ItemMode currentItemMode = ItemMode.DEFAULT; // 0 - по индексу в инвентаре, 1 - драг вещь, 2 - эквип

    enum ItemMode {
        INVENTORY(0),
        DRAGGING(1),
        EQUIPORY(2);
        private int type;
        static ItemMode DEFAULT = INVENTORY;

        ItemMode(int type) {
            this.type = type;
        }

        static ItemMode fromType(int type) {
            for (ItemMode mode : values()) {
                if (mode.type == type) {
                    return mode;
                }
            }
            return DEFAULT;
        }
    }

    // текущий индекс в бафф листе
    public static int current_buff_index = -1;

    // сбросить итератор итемов в инвентаре
    public static void resetInventoryIter() {
        if (currentInventory == null) {
            return; // TODO throw something
        }

        currentItemMode = ItemMode.INVENTORY;
        if (inventoryIter == null || inventoryIter.getInventory() != currentInventory) {
            inventoryIter = currentInventory.getItemsIterator();
        } else {
            inventoryIter.reset();
        }
//        if (CurrentInventory == null) return;
//        List<Item> tmp = inventory_list;
//        inventory_list = null;
//        inventory_list = new ArrayList<Item>();
//        for (Widget i = CurrentInventory.child; i != null; i = i.next) {
//            inventory_list.add((Item) i);
//        }
//        current_item_index = -1;
//        currentItemMode = 0;
    }

    //вызывать итератор для установки итема
    public static boolean nextInventoryItem() { // ==next(), return hasNext()
        if (inventoryIter == null || !inventoryIter.hasNext()) {
            return false;
        }
        currentItemMode = ItemMode.INVENTORY;
        inventoryIter.next();
        return true;
//        if (inventory_list == null) {
//            return 0;
//        }
//        current_item_index++;
//        if (current_item_index >= inventory_list.size()) {
//            return 0;
//        }
//        return 1;
    }

    // получить количество вещей в списке
    public static int getInventoryItemsCount() {
        if (inventoryIter == null) {
            return -1;
        }
        return inventoryIter.count();
//        if (inventory_list == null) {
//            return -1;
//        }
//        return inventory_list.size();
    }

    // установить текущую вещь по индексу в списке
    public static void useInventoryItem(int index) {
        if (inventoryIter == null) {
            return; // TODO: throw something
        }
        inventoryIter.set(index);
        currentItemMode = ItemMode.INVENTORY;
//        current_item_index = index;
//        currentItemMode = 0;
    }

    public static void useDraggingItem() {
        currentItemMode = ItemMode.DRAGGING;
    }

    public static void useEquipItem(int index) {
        currentItemMode = ItemMode.EQUIPORY;
        current_equip_index = index;
    }

    public static Item getCurrentItem() {
        switch (currentItemMode) {
            case INVENTORY:
                return inventoryIter.current();
//                if (current_item_index >= 0 && current_item_index < getInventoryItemsCount()) {
//                    return inventory_list.get(current_item_index);
//                }
//                break;
            case DRAGGING:
                // Seaching for draging item
                return UI.draggingItem.get();
//                for (Widget wdg = UI.instance.root.child; wdg != null; wdg = wdg.next) {
//                    if (wdg instanceof Item && ((Item) wdg).dm) {
//                        return (Item) wdg;
//                    }
//                }
//                break;
            case EQUIPORY:
                if (UI.equipory.get() != null) {
                    return UI.equipory.get().equed.get(current_equip_index);
                }
                break;
        }
        return null;
    }

    public static boolean isCurrentItemNameContains(String name) {
        Item i = getCurrentItem();
        return i != null && i.getResName().contains(name);
    }

    public static String getCurrentItemName() {
        Item i = getCurrentItem();
        return i == null ? "" : i.getResName();
    }

    public static boolean isCurrentItemTooltipContains(String tt) {
        Item i = getCurrentItem();
        return i != null && i.getTooltip().contains(tt);
    }

    public static String getCurrentItemTooltip() {
        Item i = getCurrentItem();
        return i == null ? "" : i.getTooltip();
    }

    /**
     * @return current item quality or -1 if no item
     */
    public static int getCurrentItemQuality() {
        Item i = getCurrentItem();
        if (i == null) {
            return -1;
        }
        return i.getQuality();
    }


    // узнать есть ли вещь в руках
    public static boolean haveDragItem() {
        for (Widget wdg = UI.instance.root.child; wdg != null; wdg = wdg.next) {
            if ((wdg instanceof Item) && (((Item) wdg).dm)) {
                return true;
            }
        }
        return false;
    }

    public static final Set<String> actions;

    static {
        actions = new HashSet<String>();
        actions.add("take");
        actions.add("transfer");
        actions.add("drop");
        actions.add("iact");
        actions.add("itemact");
    }

    // кликнуть по вещи. с указанным типом действия
    public static void clickItem(String action, int mod) {
        if (action.equals("itemact") && !haveDragItem()) {
            return;
        }
        Item i = getCurrentItem();
        if (i == null || !actions.contains(action)) {
            return;
        }
        if (action.equals("itemact")) {
            i.wdgmsg("itemact", mod);
        } else {
            i.wdgmsg(action, MapProvider.getCenterR());
        }
    }

    public static Coord getInventoryItemCoord() {
        Item i = getCurrentItem();
        if (i == null) {
            return new Coord(-1, -1);
        }
        return i.getCoord().div(31);
    }


    public static int getItemQuantity() {
        Item i = getCurrentItem();
        if (i == null) {
            return -1;
        }
        return i.getQuantity();
    }

    public static int getItemMeter() {
        Item i = getCurrentItem();
        if (i == null) {
            return -1;
        }
        return i.getCompletedPercent();
    }

    public static boolean isCursorNameContains(String str) {
        return UI.cursorName != null && UI.cursorName.contains(str);
    }

    // дать команду вещи в инвентаре с указаныым именем. по указанным координатам вещи в этом инвентаре
    public static void doInventoryAction(String name, int x, int y, String action, int mod) {
        if (!actions.contains(action)) {
            return; //TODO: say no such action
        }

        InventoryExt inventory = InventoryExt.getInventory(name);
        if (inventory == null) {
            return; //TODO: say that no such inventory
        }
        InventoryExt.InvItem item = inventory.getItem(new Coord(x, y));
        if (action.equals("itemact")) {
            item.wdgmsg("itemact", mod);
        } else {
            item.wdgmsg(action, MapProvider.getCenterR());
        }
//        Widget root = UI.instance.root;
//        for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
//            if (wdg instanceof Window && ((Window) wdg).cap.text.equals(name)) {
//                for (Widget inv = wdg.child; inv != null; inv = inv.next) {
//                    if (inv instanceof Inventory) {
//                        for (Widget i = inv.child; i != null; i = i.next) {
//                            if (i instanceof Item) {
//                                Item it = (Item) i;
//                                if (it.getCoord().div(31).equals(x, y)) {
//                                    if (action.equals("itemact")) {
//                                        it.wdgmsg("itemact", mod);
//                                    } else {
//                                        it.wdgmsg(action, MapProvider.getCenterR());
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }


    // дропнуть вещь в текущий инвентарь
    public static void dropItemIntoCurrentInventory(int x, int y) {
        if (currentInventory == null) {
            return; //TODO: throw something, say that no such inventory
        }
        currentInventory.wdgmsg("drop", new Coord(x, y));
    }

    // дропнуть вещь в указанный инвентарь
    public static void dropItemIntoInventory(String name, int x, int y) {
        InventoryExt inventory = InventoryExt.getInventory(name);
        if (inventory == null) {
            return; //TODO: say that no such inventory
        }
        inventory.wdgmsg("drop", new Coord(x, y));
//        for (Widget wdg = UI.instance.root.child; wdg != null; wdg = wdg.next) {
//            if (wdg instanceof Window && ((Window) wdg).cap.text.equals(name)) {
//                for (Widget inv = wdg.child; inv != null; inv = inv.next) {
//                    if (inv instanceof Inventory) {
//                        Inventory invn = (Inventory) inv;
//                        invn.wdgmsg("drop", new Coord(x, y));
//                        return;
//                    }
//                }
//            }
//        }
    }

    // EO Inventory


    // Crafting

    // есть ли окно крафта с заданным заголовком
    public static boolean hasCraftWindow(String wnd) {
        Makewindow mw = UI.makeWindow.get();
        return mw != null && mw.receiptName.equals(wnd) && mw.isReady();
    }

    // готово ли окно крафта?
    public static boolean isCraftWindowReady() {
        return UI.makeWindow.get() != null && UI.makeWindow.get().isReady();
    }

    public static void craft(boolean all) {
        if (UI.makeWindow.get() != null) {
            UI.instance.wdgmsg(UI.makeWindow.get(), "make", all ? 1 : 0);
        }
    }

    public static void equipAction(int slot, String action) {
        if (UI.equipory.get() == null || !actions.contains(action)) {
            return;
        }
        if (action.equals("itemact")) {
            UI.equipory.get().wdgmsg("itemact", slot);
        } else {
            UI.equipory.get().wdgmsg(action, slot, new Coord(10, 10));
        }
    }


    public static void setRenderMode(boolean enabled) {
        CustomConfig.setRender(enabled);
    }


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
                for (Buff b : UI.instance.sess.glob.buffs.values()) {
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
                for (Buff b : UI.instance.sess.glob.buffs.values()) {
                    if (i == current_buff_index) {
                        return b.getTimeLeft();
                    }
                    i++;
                }
            }
        }
        return 0;
    }

    public static boolean isBuffNameContains(String str) {
        synchronized (UI.instance.sess.glob.buffs) {
            if (current_buff_index < UI.instance.sess.glob.buffs.values().size()) {
                int i = 0;
                for (Buff b : UI.instance.sess.glob.buffs.values()) {
                    if (i == current_buff_index) {
                        return (b.getName().contains(str));
                    }
                    i++;
                }

            }
        }
        return false;
    }


    // Building

    public static void buildClick() {
        ISBox b = UI.instance.root.findchild(ISBox.class);
        if (b != null) {
            Widget w = b.parent;
            Button btn = w.findchild(Button.class);
            if (btn != null) {
                btn.click();
            }
        }
    }

    public static boolean haveBuildWindow() {
        ISBox b = UI.instance.root.findchild(ISBox.class);
        return (b != null);
    }

}
