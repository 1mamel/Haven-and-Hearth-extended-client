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

    private int attention;
    private int attentionLimit;
    private final Label maxLabel;
    private final Label usedLabel;

    public StudyWidget(final Coord c, final Coord sz, final Widget parent) {
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

    public void setAttention(final int used) {
        this.attention = used;
        usedLabel.settext(Integer.toString(used));
    }

    public void setAttentionLimit(final int limit) {
        this.attentionLimit = limit;
        maxLabel.settext(Integer.toString(limit));
    }

    public int getAttentionLimit() {
        return attentionLimit;
    }

    public int getAttention() {
        return attention;
    }
}
