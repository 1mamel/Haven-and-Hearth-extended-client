package haven;

import haven.scriptengine.UserInfo;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 11.01.11
 * Time: 15:55
 */
public class ProgressBar extends Widget {
    final Text.Foundry f = new Text.Foundry(new Font("SansSerif", Font.BOLD, 18));
    Img myImage;
    Label myLabel;
    int myProgress;
    String myLastPrStr;

    static {
        Widget.addtype("progressbar", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                Tex tex;
                if (args.length > 1) {
                    Resource res = Resource.load((String) args[0], (Integer) args[1]);
                    res.loadwait();
                    tex = res.layer(Resource.imgc).tex();
                } else {
                    tex = Resource.loadtex((String) args[0]);
                }
                ProgressBar ret = new ProgressBar(c, tex, parent);
                if (args.length > 2)
                    ret.myImage.hit = (Integer) args[2] != 0;
                ret.setProgress((String) args[0]);
                return (ret);
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
        myLabel = new Label(Coord.z, this, "", f);
        myLabel.setcolor(Color.ORANGE);
    }

    public void uimsg(String name, Object... args) {
        if (name.equals("ch")) {
            setProgress((String) args[0]);
        }
    }

    static Pattern intsOnly = Pattern.compile("[-]?\\d+");

    private void setProgress(String progressStr) {
        if (progressStr.equals(myLastPrStr)) return;
        Matcher makeMatch = intsOnly.matcher(progressStr);
        makeMatch.find();
        int progress = Integer.parseInt(makeMatch.group());
        myProgress = progress * 5;
        myLabel.settext(String.valueOf(myProgress) + '%');
        UserInfo.updateProgress(myProgress);
    }

    @Override
    protected void finalize() throws Throwable {
        UserInfo.updateProgress(-1);
        super.finalize();
    }
}
