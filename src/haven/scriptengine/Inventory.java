package haven.scriptengine;

import haven.Coord;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 12.01.11
 * Time: 13:53
 */
public class Inventory {
    public static class InvItem {
        int type;
        int quality;
        int id;

        public InvItem(int type, int quality, int id) {
            this.type = type;
            this.quality = quality;
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public int getQuality() {
            return quality;
        }

        public int getId() {
            return id;
        }

    }

    Map<Coord, InvItem> items;
    Coord size;
    String name;
    int id;
    Coord screenCoordinates;

    public Inventory(int id, String name, Coord size, Coord position) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.screenCoordinates = position;
        this.items = new HashMap<Coord, InvItem>();
    }

    public void setItem(Coord position, InvItem item) {
        items.put(position, item);
    }

    public void deleteItem(Coord position) {
        if (items.containsKey(position))
            items.remove(position);
    }

    public void deleteItem(int id) {
        for (Map.Entry<Coord, InvItem> itempair : items.entrySet())
            if (itempair.getValue().getId() == id) {
                items.remove(itempair.getKey());
                return;
            }

    }

    public InvItem getItem(Coord position) {
        return items.get(position);
    }

    public static Coord getItemScreenPosition(Coord position) {
        return position.add(new Coord(position.x, position.y).mul(31).add(1, 1));
    }

    public Coord getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    //    public static void openInventory(int id, String name, Coord size, Coord pos) {
//        openedInventories.put(id, new OpenedInv(id, name, size, pos));
//    }
//
//    public static void closeInventory(int id) {
//        if (openedInventories.containsKey(id))
//            openedInventories.remove(id);
//    }

//    public static void closeWidget(int id) {
//        try {
//            closeInventory(id);
//            //TODO widget type maybe Item
//            if ((atMouseItem != null) && (id == atMouseItem.getId())) atMouseItem = null;
//            Widget wtd = ui.getWidgetById(id);
//            int parent = ui.getIdByWidget(wtd.parent);
//            if (openedInventories.containsKey(parent)) {
//                openedInventories.get(parent).deleteItem(id);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//    }

//    public static void newItem(int parent, int id, int itype, int iquality, Coord position) {
//        try {
//            if (parent == 0) {
//                atMouseItem = new OpenedInv.InvItem(itype, iquality, id);
//            }
//
//            if (!openedInventories.containsKey(parent)) return;
//            OpenedInv.InvItem item = new OpenedInv.InvItem(itype, iquality, id);
//            OpenedInv inventory = openedInventories.get(parent);
//            inventory.setItem(position.add(new Coord(-1, -1).div(new Coord(31, 31))), item);
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//    }
    //    public static OpenedInv.InvItem atMouseItem = null;

//    public static Map<Integer, haven.scriptengine.Inventory> openedInventories = new HashMap<Integer, Inventory>();
}
