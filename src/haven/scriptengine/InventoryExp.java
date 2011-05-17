package haven.scriptengine;

import haven.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 12.01.11
 * Time: 13:53
 */
@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
public class InventoryExp extends Inventory {

    public class InvItem extends Item {

        public InvItem(Coord c, Indir<Resource> res, int q, Widget parent, Coord drag, int num) {
            super(c, res, q, parent, drag, num);
            Coord locInInv = c.add(new Coord(-1, -1)).div(3);
            InventoryExp.this.addItem(this, locInInv, getSize());
        }

        public InvItem(Coord c, int res, int q, Widget parent, Coord drag, int num) {
            this(c, parent.ui.sess.getres(res), q, parent, drag, num);
        }

        public InvItem(Coord c, Indir<Resource> res, int q, Widget parent, Coord drag) {
            this(c, res, q, parent, drag, -1);
        }

        public InvItem(Coord c, int res, int q, Widget parent, Coord drag) {
            this(c, parent.ui.sess.getres(res), q, parent, drag);
        }

        @Override
        public void uimsg(String name, Object... args) {
            super.uimsg(name, args);
            if (name.equals("num")) {
            } else if (name.equals("chres")) {
            } else if (name.equals("color")) {
            } else if (name.equals("tt")) {
                tooltipProcess();
            } else if (name.equals("meter")) {
            }
        }

        public boolean isCompleted() {
            return meter == 0;
        }

        public Coord getSizeInCells() {
            return sz.div(30);
        }

        private void tooltipProcess() {
            if (tooltip == null) return;
            try {
                final Pattern water = Pattern.compile("(\\d+\\.?\\d*)/(\\d+\\.?\\d*) l of quality (\\d+) water");
                Matcher nm = water.matcher(tooltip);
                if (nm.find() && nm.groupCount() == 3) {
                    int nowCount = Integer.parseInt(nm.group(1));
                    int maxCount = Integer.parseInt(nm.group(2));
                    int q = Integer.parseInt(nm.group(3));
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    Map<Coord, InvItem> items = new HashMap<Coord, InvItem>();

    public void deleteItem(Coord position) {
        if (items.containsKey(position))
            items.remove(position);
    }

    public void deleteItem(InvItem item) {
        for (Map.Entry<Coord, InvItem> itempair : items.entrySet())
            if (itempair.getValue() == item) {
                items.remove(itempair.getKey());
                return;
            }
    }

    public InvItem getItem(Coord position) {
        return items.get(position);
    }

//    public static Coord getItemScreenPosition(Coord position) {
//        return position.add(new Coord(position.x, position.y).mul(31).add(1, 1));
//    }

    public Coord getSize() {
        return sz;
    }

//    public int getId() {
//        return id;
//    }

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

//    public static Map<Integer, InventoryExp> openedInventories = new HashMap<Integer, InventoryExp>();

    public InventoryExp(Coord c, Coord sz, Widget parent) {
        super(c, sz, parent);
        registerInventory(this, sz);
    }

    @Override
    public void destroy() {
        unregisterInventory(this);
        super.destroy();
    }

    @Override
    public void uimsg(String msg, Object... args) {
        if (msg.equals("sz")) {
            changeSize((Coord) args[0]);
        }
        super.uimsg(msg, args);
    }

    public static void registerInventory(InventoryExp inv, Coord size) {
    }

    public static void unregisterInventory(InventoryExp inv) {
    }

    private void changeSize(Coord newSize) {
    }

    private void addItem(InvItem invItem, Coord loc, Coord itemSize) {
        items.put(loc, invItem);
    }

    private static void updateItemQuantity(Item item, int num) {

    }

    public boolean isFull() {
        return false; // TODO for items bigger than 1x1
    }

    static List<InventoryExp> openedInventories = new ArrayList<InventoryExp>();


    private static class CellGrid {
        Map<Integer, InvItem> myItemsMap = new HashMap<Integer, InvItem>();
        int[][] myCells;

        void setSize(int w, int h) {
            int[][] newCells = new int[h][w];
            int i = 0;
            for (int[] myCell : myCells) {
                System.arraycopy(myCell, 0, newCells[i], 0, min(myCell.length, w));
                i++;
            }
            myCells = newCells;
            updateIndexes();
        }

        private CellGrid(int w, int h) {
            myCells = new int[h][w];
        }

        private void updateIndexes() {
            //To change body of created methods use File | Settings | File Templates.
        }

    }

}
