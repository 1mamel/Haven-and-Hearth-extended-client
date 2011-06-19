package haven;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by IntelliJ IDEA.
 * Date: 19.06.11
 * Time: 18:40
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class StudyWidget extends Widget {
    public static AtomicReference<StudyWidget> instance = new AtomicReference<StudyWidget>(null);

    private int used;
    private int max;
    private final Label maxLabel;
    private final Label usedLabel;

    public StudyWidget(Coord c, Coord sz, Widget parent) {
        super(c, sz, parent);
        this.canhastrash = false;
        new Label(new Coord(138, 210), this, "Used attention:");
        new Label(new Coord(138, 225), this, "Attention limit:");
        usedLabel = new Label(new Coord(240, 210), this, "");
        maxLabel = new Label(new Coord(240, 225), this, "");
        this.visible = false;
        instance.set(this);
    }

    @Override
    public void destroy() {
        instance.compareAndSet(this, null);
        super.destroy();
    }

    public void setUsed(int used) {
        this.used = used;
        usedLabel.settext(Integer.toString(used));
    }

    public void setMax(int max) {
        this.max = max;
        maxLabel.settext(Integer.toString(max));
    }

    public int getMax() {
        return max;
    }

    public int getUsed() {
        return used;
    }
}
