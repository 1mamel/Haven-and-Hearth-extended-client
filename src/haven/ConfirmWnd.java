package haven;

public class ConfirmWnd extends Window {

    private Callback clbk;

    public ConfirmWnd(final Coord c, final Widget parent, final String msg, final Callback callback) {
        super(c, Coord.z, parent, "Confirmation");
        clbk = callback;
        new Label(Coord.z, this, msg);
        new Button(new Coord(0, 20), 90, this, "Ok") {
            public void click() {
                clbk.result(true);
                close();
            }
        };
        new Button(new Coord(100, 20), 90, this, "Cancel") {
            public void click() {
                clbk.result(false);
                close();
            }
        };
        pack();
    }

    public void close() {
        ui.destroy(this);
    }

    public void destroy() {
        clbk = null;
        super.destroy();
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (checkIsCloseButton(sender)) {
            clbk.result(false);
            close();
            return;
        }
        super.wdgmsg(sender, msg, args);
    }

    public boolean type(final char key, final java.awt.event.KeyEvent ev) {
        if (key == 27) {
            clbk.result(false);
            close();
        }
        return (super.type(key, ev));
    }

    public static interface Callback {
        public void result(Boolean res);
    }
}
