package haven;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 11.01.11
 * Time: 15:55
 */
public class ProgressBar extends Widget{
    final Text.Foundry textFoundry = new Text.Foundry(new Font("SansSerif", Font.BOLD, 18));
    final Img myImage;
    final Label myLabel;
    int myProgress;
    String myLastPrStr;

    private static final Collection<ProgressListener> ourListeners = new CopyOnWriteArraySet<ProgressListener>();

    static {
        Widget.addtype("progressbar", new WidgetFactory() {
            public Widget create(@NotNull final Coord c, @NotNull final Widget parent, final Object[] args) {
                // Parent id is always 0, then parent is always same ;)
                final Tex tex;
                if (args.length > 1) {
                    final Resource res = Resource.load((String) args[0], (Integer) args[1]);
                    res.loadwait();
                    tex = res.layer(Resource.imgc).tex();
                } else {
                    tex = Resource.loadtex((String) args[0]);
                }
                if (ourInstance == null) {
                    ourInstance = new ProgressBar(c, tex, parent);
                } else {
                    ourInstance.link();
                }
                if (args.length > 2)
                    ourInstance.myImage.hit = (Integer) args[2] != 0;
                emitStarted();
                ourInstance.setProgress((String) args[0]);
                return (ourInstance);
            }
        });
    }

    public void draw(final GOut g) {
        super.draw(g);
        myLabel.draw(g);
    }

    public ProgressBar(final Coord c, final Tex img, final Widget parent) {
        super(c, img.sz(), parent);
        myImage = new Img(c, img, this);
        myLabel = new Label(Coord.z, this, "", textFoundry);
        myLabel.setcolor(Color.ORANGE);
    }

    public void uimsg(@NotNull final String name, final Object... args) {
        if (name.equals("ch")) {
            setProgress((String) args[0]);
        }
    }

    static final Pattern intsOnly = Pattern.compile("[-]?\\d+");

    private void setProgress(final String progressStr) {
        if (progressStr.equals(myLastPrStr)) return;
        final Matcher m = intsOnly.matcher(progressStr);
        m.find();
        final int progress = Integer.parseInt(m.group());
        myProgress = progress * 5;
        myLabel.settext(String.valueOf(myProgress) + '%');
        emitChanged(myProgress);
    }

    @Override
    public void destroy() {
        emitFinished();
        super.destroy();    //TODO: implement
    }

    private static ProgressBar ourInstance;

    public static void delete() {
        ourInstance = null;
    }

    public static interface ProgressListener {
        void onChanged(int percents);

        void onFinished();

        void onStarted();
    }

    public static void subscribe(@NotNull final ProgressListener pl) {
        ourListeners.add(pl);
    }

    public static void unsubscribe(@NotNull final ProgressListener pl) {
        ourListeners.remove(pl);
    }

    private static void emitChanged(final int percents) {
        for (final ProgressListener listener : ourListeners) {
            try {
                listener.onChanged(percents);
            } catch (Exception ignored) {
            }
        }
    }

    private static void emitStarted() {
        for (final ProgressListener listener : ourListeners) {
            try {
                listener.onStarted();
            } catch (Exception ignored) {
            }
        }
    }

    private static void emitFinished() {
        for (final ProgressListener listener : ourListeners) {
            try {
                listener.onFinished();
            } catch (Exception ignored) {
            }
        }
    }

}
