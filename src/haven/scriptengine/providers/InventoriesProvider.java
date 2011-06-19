package haven.scriptengine.providers;

import haven.Coord;
import haven.Item;
import haven.UI;
import haven.Widget;
import haven.scriptengine.InventoryExt;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 19.06.11
 * Time: 22:48
 *
 * @author Vlad.Rassokhin@gmail.com
 */
@SuppressWarnings({"UnusedDeclaration"})
public class InventoriesProvider {

    public static InventoryExt.ItemsIterator inventoryIter;
    // виджет текущего инвентаря с которым работаем
    public static InventoryExt currentInventory = null;
    // режим получения вещи
    public static UIProvider.ItemMode currentItemMode = UIProvider.ItemMode.DEFAULT; // 0 - по индексу в инвентаре, 1 - драг вещь, 2 - эквип
    public static final Set<String> actions;

    static {
        actions = new HashSet<String>();
        actions.add("take");
        actions.add("transfer");
        actions.add("drop");
        actions.add("iact");
        actions.add("itemact");
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

    // сбросить итератор итемов в инвентаре
    public static void resetInventoryIter() {
        if (currentInventory == null) {
            return; // TODO throw something
        }

        currentItemMode = UIProvider.ItemMode.INVENTORY;
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
        currentItemMode = UIProvider.ItemMode.INVENTORY;
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
        currentItemMode = UIProvider.ItemMode.INVENTORY;
//        current_item_index = index;
//        currentItemMode = 0;
    }

    public static void useDraggingItem() {
        currentItemMode = UIProvider.ItemMode.DRAGGING;
    }

    public static void useEquipItem(int index) {
        currentItemMode = UIProvider.ItemMode.EQUIPORY;
        UIProvider.current_equip_index = index;
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
                    return UI.equipory.get().equed.get(UIProvider.current_equip_index);
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
}
