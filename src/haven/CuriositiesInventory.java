package haven;

import haven.scriptengine.InventoryExt;

import java.util.concurrent.atomic.AtomicReference;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 19.06.11
 * Time: 20:06
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class CuriositiesInventory extends InventoryExt {
    public static AtomicReference<InventoryExt> instance = new AtomicReference<InventoryExt>(null);

    public CuriositiesInventory(Coord c, Coord sz, Widget parent) {
        super(c, sz, parent);
    }

    int timeToNextFree() {
//        for (InvItem item : items.values()) {
//            if (item.getCompletedPercent())
//        }
        return Integer.MAX_VALUE;
    }

}
