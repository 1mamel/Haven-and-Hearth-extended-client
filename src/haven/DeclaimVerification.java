package haven;

public class DeclaimVerification extends Window {

    public DeclaimVerification(final Widget parent, final String[] ad) {
        super(new Coord(100, 100), Coord.z, parent, "Declaiming");
        new Label(Coord.z, this, "Are you sure you want to declaim your plot?");
        new Button(new Coord(0, 20), 90, this, "Declaim") {
            public void click() {
                ui.mnu.wdgmsg("act", (Object[]) ad);
            }
        };
        new Button(new Coord(100, 20), 90, this, "Cancel") {
            public void click() {
                close();
            }
        };
        pack();
    }

    public void close() {
        ui.destroy(this);
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (checkIsCloseButton(sender)) {
            close();
            return;
        }
        super.wdgmsg(sender, msg, args);
    }

}
