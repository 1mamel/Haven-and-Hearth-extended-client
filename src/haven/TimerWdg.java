package haven;

import ender.timer.Callback;
import ender.timer.Timer;
import haven.resources.Resource;

public class TimerWdg extends Widget {

    private static final Tex bg = Resource.loadtex("gfx/hud/bosq");

    private final Timer timer;

    // UI components
    private final Label time;
    @SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
    private final Label name;
    private final Button start;
    private final Button stop;
    private final Button delete;

    public TimerWdg(Coord c, Widget parent, Timer timer) {
        super(c, bg.sz(), parent);

        this.timer = timer;
        timer.updcallback = new Callback() {

            @Override
            public void run(Timer timer) {
                synchronized (time) {
                    time.settext(timer.toString());
                    updbtns();
                }

            }
        };
        name = new Label(new Coord(5, 5), this, timer.getName());
        time = new Label(new Coord(5, 25), this, timer.toString());

        start = new Button(new Coord(90, 2), 50, this, "start");
        stop = new Button(new Coord(90, 2), 50, this, "stop");
        delete = new Button(new Coord(90, 21), 50, this, "delete");
        updbtns();
    }

    private void updbtns() {
        start.visible = !timer.isWorking();
        stop.visible = timer.isWorking();
    }

    @Override
    public void destroy() {
        unlink();
        if (parent instanceof TimerPanel) {
            ((TimerPanel) parent).pack();
        }
        timer.updcallback = null;
        super.destroy();
    }

    @Override
    public void draw(GOut g) {
        g.image(bg, Coord.z);
        super.draw(g);
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == start) {
            timer.start();
            updbtns();
        } else if (sender == stop) {
            timer.stop();
            updbtns();
        } else if (sender == delete) {
            timer.destroy();
            ui.destroy(this);
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }


}
