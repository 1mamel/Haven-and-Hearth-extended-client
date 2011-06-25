package haven.scriptengine;

import haven.*;
import haven.scriptengine.providers.MapProvider;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 12.01.11
 * Time: 13:53
 */
@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
public class InventoryExt extends Inventory {

    private int freeCount;

    public String getName() {
        return (parent instanceof Window) ? ((Window) parent).getCaption() : "";
    }

    public ItemsIterator getItemsIterator() {
        return new ItemsIterator(this);
    }

    public class InvItem extends Item {

        public InvItem(Coord c, Indir<Resource> res, int q, Widget parent, Coord drag, int num) {
            super(c, res, q, parent, drag, num);
//            1,32,63,94,..  step == 31
//            Coord locInInv = new Coord(c.x / 31, c.y / 31);
            // 0, 1, 2, ... step = 1
            InventoryExt.this.addItem(this);
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

        public Coord getSizeInCells() {
            return sz.div(30);
        }

        public Coord getInInvLocation() {
            return c.div(31);
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

        @Override
        public void destroy() {
            InventoryExt.this.deleteItem(this);
            super.destroy();
        }

        public InventoryExt getInventory() {
            return InventoryExt.this;
        }

        public void take() {
            wdgmsg("take", MapProvider.getCenterR());
        }
    }

    protected TreeMap<Coord, InvItem> items = new TreeMap<Coord, InvItem>();

    private synchronized void addItem(InvItem item) {
        Coord loc = item.getInInvLocation();
        items.put(loc, item);
        Coord iSize = item.getSizeInCells();
        if (loc.x + iSize.x <= isz.x && loc.y + iSize.y <= isz.y) {
            for (int x = 0; x < iSize.x; ++x) {
                for (int y = 0; y < iSize.y; ++y) {
                    if (busy[loc.x + x][loc.y + y]) continue;
                    busy[loc.x + x][loc.y + y] = true;
                    freeCount--;
                }
            }
        }
//        System.err.println("Inv " + getName() + " new item loc = " + loc + " size = " + item.getSizeInCells() + " fc = " + getFreeCellsCount() + " meter="+ item.getCompletedPercent());
    }

    private synchronized void deleteItem(InvItem item) {
        items.values().remove(item);
        Coord loc = item.getInInvLocation();
        Coord iSize = item.getSizeInCells();
        if (loc.x + iSize.x <= isz.x && loc.y + iSize.y <= isz.y) {
            for (int x = 0; x < iSize.x; ++x) {
                for (int y = 0; y < iSize.y; ++y) {
                    if (!busy[loc.x + x][loc.y + y]) continue;
                    busy[loc.x + x][loc.y + y] = false;
                    freeCount++;
                }
            }
        }
//        System.err.println("Inv " + getName() + " remove item loc = " + item.getCoord().div(31) + " size = " + item.getSizeInCells() + " fc = " + getFreeCellsCount());
    }

    public InvItem getItem(Coord position) {
        return items.get(position);
    }

    public InventoryExt(Coord c, Coord sz, Widget parent) {
        super(c, sz, parent);
        this.busy = new boolean[isz.x][isz.y];
        updateBusy();
        registerInventory(this);
    }

    @Override
    public void destroy() {
        unregisterInventory(this);
        super.destroy();
    }

    @Override
    public void uimsg(String msg, Object... args) {
        if (msg.equals("sz")) {
            onSizeChange((Coord) args[0]);
        }
        super.uimsg(msg, args);
    }

    private static void registerInventory(InventoryExt inv) {
//        System.err.println("Registering Inventory. name = " + inv.getName() + " parent type is " + inv.parent.getClass().getSimpleName());
        if (inv instanceof CuriositiesInventory) {
            CuriositiesInventory.instance.set((CuriositiesInventory) inv);
        } else if (inv.parent instanceof Window) {
            openedInventories.add(inv);
        }
    }

    private static void unregisterInventory(InventoryExt inv) {
//        System.err.println("Unregistering Inventory. name = " + inv.getName() + " parent type is " + inv.parent.getClass().getSimpleName());
        if (inv instanceof CuriositiesInventory) {
            CuriositiesInventory.instance.compareAndSet((CuriositiesInventory) inv, null);
        }
        openedInventories.remove(inv);
    }


    public static boolean hasInventory(String str) {
        return getInventory(str) != null;
    }

    public static InventoryExt getInventory(String str) {
        for (InventoryExt inv : openedInventories) {
            if (inv.getName().equals(str)) {
                return inv;
            }
        }
        return null;
    }


    public static class ItemsIterator {
        private InventoryExt myInventory;
        private Iterator<InvItem> myItemsIterator;
        private InvItem myCurrentItem;

        public ItemsIterator(InventoryExt inventory) {
            this.myInventory = inventory;
            reset();
        }

        public boolean hasNext() {
            return myItemsIterator.hasNext();
        }

        public InvItem next() {
            return myCurrentItem = myItemsIterator.next();
        }

        public InvItem current() {
            return myCurrentItem;
        }

        public void reset() {
            myItemsIterator = myInventory.items.values().iterator();
            if (hasNext()) {
                next();
            }
        }

        public InventoryExt getInventory() {
            return myInventory;
        }

        public int count() {
            return myInventory.items.size();
        }

        public void set(int index) {
            reset();
            for (int i = 0; i <= index && hasNext(); ++i) {
                next();
            }
        }
    }

    private boolean[][] busy;

    private synchronized void onSizeChange(Coord newSize) {
        boolean[][] busy = new boolean[newSize.x][newSize.y];
        updateBusy();
    }

    private synchronized void updateBusy() {
        freeCount = isz.x * isz.y;
        for (Map.Entry<Coord, InvItem> entry : items.entrySet()) {
            Coord loc = entry.getKey();
            Coord iSize = entry.getValue().getSizeInCells();
            if (loc.x + iSize.x <= isz.x && loc.y + iSize.y <= isz.y) {
                for (int x = 0; x < iSize.x; ++x) {
                    for (int y = 0; y < iSize.y; ++y) {
                        if (busy[loc.x + x][loc.y + y]) continue;
                        busy[loc.x + x][loc.y + y] = true;
                        freeCount--;
                    }
                }
            }
        }
    }

    public boolean isFull() {
        return getFreeCellsCount() != 0;
    }

    private int getFreeCellsCount() {
        return freeCount;
    }

    private int getItemsCount() {
        return items.size();
    }

    static List<InventoryExt> openedInventories = new ArrayList<InventoryExt>();

    public InvItem getItemByName(String str) {
        for (InvItem item : items.values()) {
            if (item.getResName().contains(str)) {
                return item;
            }
        }
        return null;
    }

    public void drop(int x, int y) {
        wdgmsg("drop", new Coord(x, y));
    }

    public Coord getPositionForItem(int sizeX, int sizeY) {
        if (sizeX * sizeY > freeCount) return null;

        for (int i = 0; i < busy.length - (sizeX - 1); ++i) {
            for (int j = 0; j < busy[i].length - (sizeY - 1); ++j) {
                if (busy[i][j]) continue;
                int countF = 0;
                for (int x = 0; x < sizeX; ++x) {
                    for (int y = 0; y < sizeY; ++y) {
                        if (!busy[i + x][j + y]) countF++;
                    }
                }
                if (countF == sizeX * sizeY) {
                    return new Coord(i, j);
                }
            }
        }
        return null;
    }

    public Coord getPositionForItem(Coord size) {
        return getPositionForItem(size.x, size.y);
    }

}
