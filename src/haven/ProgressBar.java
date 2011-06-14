package haven;

import haven.scriptengine.providers.Player;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 11.01.11
 * Time: 15:55
 */
public class ProgressBar extends Widget {
    final Text.Foundry textFoundry = new Text.Foundry(new Font("SansSerif", Font.BOLD, 18));
    final Img myImage;
    final Label myLabel;
    int myProgress;
    String myLastPrStr;

    static {
        Widget.addtype("progressbar", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                // Parent id is always 0, then parent is always same ;)
                Tex tex;
                if (args.length > 1) {
                    Resource res = Resource.load((String) args[0], (Integer) args[1]);
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
                ourInstance.setProgress((String) args[0]);
                return (ourInstance);
            }
        });
    }

    public void draw(GOut g) {
        super.draw(g);
        myLabel.draw(g);
    }

    public ProgressBar(Coord c, Tex img, Widget parent) {
        super(c, img.sz(), parent);
        myImage = new Img(c, img, this);
        myLabel = new Label(Coord.z, this, "", textFoundry);
        myLabel.setcolor(Color.ORANGE);
    }

    public void uimsg(String name, Object... args) {
        if (name.equals("ch")) {
            setProgress((String) args[0]);
        }
    }

    static final Pattern intsOnly = Pattern.compile("[-]?\\d+");

    private void setProgress(String progressStr) {
        if (progressStr.equals(myLastPrStr)) return;
        Matcher m = intsOnly.matcher(progressStr);
        m.find();
        int progress = Integer.parseInt(m.group());
        myProgress = progress * 5;
        myLabel.settext(String.valueOf(myProgress) + '%');
        Player.updateProgress(myProgress);
    }

    @Override
    protected void finalize() throws Throwable {
        Player.updateProgress(-1);
        super.finalize();
    }

    @Override
    public void destroy() {
        Player.updateProgress(-1);
        super.destroy();    //TODO: implement
    }

    private static ProgressBar ourInstance;

    public static void delete() {
        ourInstance = null;
    }
}
