package haven.scriptengine.providers.providers;

import haven.Coord;
import haven.Item;
import haven.UI;
import haven.Widget;
import haven.scriptengine.InventoryExt;

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
    public static boolean haveInventory(final String name) {
        return InventoryExt.hasInventory(name);
    }

    // вернуть инвентарь по имени
    public static InventoryExt getInventory(final String name) {
        return InventoryExt.getInventory(name);
    }

    /**
     * Open user Inventory window.
     * Sends message to server, you wants to wait some time for appear.
     */
    public static void toggleUserInventory() {
        UI.instance.slen.wdgmsg("inv");
    }

    // поставить текущий инвентарь, после автоматически сбрасывается итератор
    public static int setInventory(final String name) {
        currentInventory = InventoryExt.getInventory(name);
        if (currentInventory != null) {
            resetInventoryIter();
            return 1;
        } else {
            return 0;
        }
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
    }

    //вызывать итератор для установки итема
    public static boolean nextInventoryItem() { // ==next(), return hasNext()
        if (inventoryIter == null || !inventoryIter.hasNext()) {
            return false;
        }
        currentItemMode = UIProvider.ItemMode.INVENTORY;
        inventoryIter.next();
        return true;
    }

    // получить количество вещей в списке
    public static int getInventoryItemsCount() {
        if (inventoryIter == null) {
            return -1;
        }
        return inventoryIter.count();
    }

    // установить текущую вещь по индексу в списке
    public static void useInventoryItem(final int index) {
        if (inventoryIter == null) {
            return; // TODO: throw something
        }
        inventoryIter.set(index);
        currentItemMode = UIProvider.ItemMode.INVENTORY;
    }

    public static void useDraggingItem() {
        currentItemMode = UIProvider.ItemMode.DRAGGING;
    }

    public static void useEquipItem(final int index) {
        currentItemMode = UIProvider.ItemMode.EQUIPORY;
        UIProvider.current_equip_index = index;
    }

    public static Item getCurrentItem() {
        switch (currentItemMode) {
            case INVENTORY:
                return inventoryIter.current();
            case DRAGGING:
                return UI.draggingItem.get();
            case EQUIPORY:
                if (UI.equipory.get() != null) {
                    return UI.equipory.get().equed.get(UIProvider.current_equip_index);
                }
                break;
        }
        return null;
    }

    public static boolean isCurrentItemNameContains(final String name) {
        final Item i = getCurrentItem();
        return i != null && i.getResName().contains(name);
    }

    public static String getCurrentItemName() {
        final Item i = getCurrentItem();
        return i == null ? "" : i.getResName();
    }

    public static boolean isCurrentItemTooltipContains(final String tt) {
        final Item i = getCurrentItem();
        return i != null && i.getTooltip().contains(tt);
    }

    public static String getCurrentItemTooltip() {
        final Item i = getCurrentItem();
        return i == null ? "" : i.getTooltip();
    }

    /**
     * @return current item quality or -1 if no item
     */
    public static int getCurrentItemQuality() {
        final Item i = getCurrentItem();
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
    public static void clickItem(final String action, final int mod) {
        if (action.equals("itemact") && !haveDragItem()) {
            return;
        }
        final Item i = getCurrentItem();
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
        final Item i = getCurrentItem();
        if (i == null) {
            return new Coord(-1, -1);
        }
        return i.getCoord().div(31);
    }

    public static int getItemQuantity() {
        final Item i = getCurrentItem();
        if (i == null) {
            return -1;
        }
        return i.getQuantity();
    }

    public static int getItemMeter() {
        final Item i = getCurrentItem();
        if (i == null) {
            return -1;
        }
        return i.getCompletedPercent();
    }

    public static boolean isCursorNameContains(final String str) {
        return UI.cursorName != null && UI.cursorName.contains(str);
    }

    // дать команду вещи в инвентаре с указаныым именем. по указанным координатам вещи в этом инвентаре
    public static void doInventoryAction(final String name, final int x, final int y, final String action, final int mod) {
        if (!actions.contains(action)) {
            return; //TODO: say no such action
        }

        final InventoryExt inventory = InventoryExt.getInventory(name);
        if (inventory == null) {
            return; //TODO: say that no such inventory
        }
        final InventoryExt.InvItem item = inventory.getItem(new Coord(x, y));
        if (action.equals("itemact")) {
            item.wdgmsg("itemact", mod);
        } else {
            item.wdgmsg(action, MapProvider.getCenterR());
        }
    }

    // дропнуть вещь в текущий инвентарь
    public static void dropItemIntoCurrentInventory(final int x, final int y) {
        if (currentInventory == null) {
            return; //TODO: throw something, say that no such inventory
        }
        currentInventory.wdgmsg("drop", new Coord(x, y));
    }

    // дропнуть вещь в указанный инвентарь
    public static void dropItemIntoInventory(final String name, final int x, final int y) {
        final InventoryExt inventory = InventoryExt.getInventory(name);
        if (inventory == null) {
            return; //TODO: say that no such inventory
        }
        inventory.wdgmsg("drop", new Coord(x, y));
    }
}
