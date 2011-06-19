package haven;

import haven.scriptengine.InventoryExt;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 * Date: 19.06.11
 * Time: 18:40
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class StudyWidget extends Widget {
    public static AtomicReference<InventoryExt> curiositiesInventory = new AtomicReference<InventoryExt>(null);
    public static AtomicReference<StudyWidget> instance = new AtomicReference<StudyWidget>(null);

    public StudyWidget(Coord c, Coord sz, Widget parent) {
        super(c, sz, parent);
        this.canhastrash = false;
        new Label(new Coord(138, 210), this, "Used attention:");
        new Label(new Coord(138, 225), this, "Attention limit:");
        this.visible = false;
        instance.set(this);
    }

    @Override
    public void destroy() {
        instance.compareAndSet(this, null);
        super.destroy();
    }
}
