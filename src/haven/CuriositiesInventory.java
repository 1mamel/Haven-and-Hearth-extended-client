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

    public CuriositiesInventory(final Coord c, final Coord sz, final Widget parent) {
        super(c, sz, parent);
    }

    /**
     * Returns time to next free's in milliseconds.
     * @return time in millisenconds
     */
    long timeToNextFree() {
        long ret = Long.MAX_VALUE;
        for (final InvItem item : items.values()) {
            final long ttc = item.getTimeToComplete();
            if (ttc == 0){ // %) it cannot be true =)
                continue;
            }
            if (ttc < ret) {
                ret = ttc;
            }
        }
        return ret;
    }

}
