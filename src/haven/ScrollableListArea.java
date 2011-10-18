package haven;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Scrollable container with list layout elements.
 * Every element represents with specific name and may store something.
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public abstract class ScrollableListArea<T> extends Widget {
    public final List<Option<T>> myOptions;
    public Option<T> myChosen;
    final Scrollbar myScrollBar;
    final int myHeight;

    public static class Option<T> {
        private final String myDisplayedText;
        private final T myStoredObject;

        public Option(final String displayText, final T storedObject) {
            myDisplayedText = displayText;
            myStoredObject = storedObject;
        }

        public T getStoredObject() {
            return myStoredObject;
        }

        public String getDisplayedText() {
            return myDisplayedText;
        }
    }

    public void draw(final GOut g) {
        for (int i = 0; i < myHeight && myScrollBar != null; i++) {
            final Color c;
            if (i + myScrollBar.val >= myOptions.size())
                continue;
            final Option b = myOptions.get(i + myScrollBar.val);
            if (b.equals(myChosen)) {
                c = FlowerMenu.pink;
            } else {
                c = Color.BLACK;
            }
            g.chcolor(c);
            g.text(b.getDisplayedText(), new Coord(0, i * 10));
        }
        g.chcolor();
        super.draw(g);
    }

    public ScrollableListArea(final Coord c, final Coord sz, final Widget parent, final List<Option<T>> options) {
        this(c, sz, parent);
        myOptions.addAll(options);
        myChosen = !myOptions.isEmpty() ? myOptions.get(0) : null;
    }

    public ScrollableListArea(final Coord c, final Coord sz, final Widget parent, final Map<String, T> options) {
        this(c, sz, parent);
        for (final Map.Entry<String, T> entry : options.entrySet()) {
            myOptions.add(new Option<T>(entry.getKey(), entry.getValue()));
        }
        myChosen = !myOptions.isEmpty() ? myOptions.get(0) : null;
    }

    public ScrollableListArea(final Coord c, final Coord sz, final Widget parent) {
        super(c, sz, parent);
        myOptions = new ArrayList<Option<T>>();
        myHeight = sz.y / 10;
        myScrollBar = new Scrollbar(Coord.z.add(sz.x, 0), sz.y, this, 0, 50);
        myChosen = null;
        setcanfocus(true);
    }

    public void check(@Nullable final Option<T> option) {
        if (myChosen != option) {
            myChosen = option;
            changed(option);
        }
    }

    public void checkByValue(@Nullable final T value) {
        for (final Option<T> option : myOptions) {
            if (value == null) {
                if (option.getStoredObject() == null)
                    check(option);
            } else if (value.equals(option.getStoredObject())) {
                check(option);
            }
        }
    }

    public boolean mousedown(final Coord c, final int button) {
        if (button == 1 && c.x < sz.x - 25) {
            int sel = (c.y / 10) + myScrollBar.val;
            if (sel >= myOptions.size()) {
                sel = -1;
            }
            if (sel < 0) {
                myChosen = null;
            } else {
                myChosen = myOptions.get(sel);
            }
            changed(myChosen);
            return (true);
        }
        return myScrollBar.mousedown(c, button);
    }

    public boolean mousewheel(final Coord c, final int amount) {
        return myScrollBar.mousewheel(c, amount);
    }

    public abstract void changed(@Nullable final Option<T> changed);
}
