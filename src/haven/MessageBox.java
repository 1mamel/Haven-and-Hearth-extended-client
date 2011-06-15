package haven;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 15.06.11
 * Time: 22:25
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class MessageBox extends Window {

    public static final int NOBUTTONS = 0x00;
    public static final int BUTTON_OK = 0x01;
    public static final int BUTTON_CANCEL = 0x02;
    public static final int BUTTON_YES = 0x04;
    public static final int BUTTON_NO = 0x08;

    public static final int BUTTONS_DEFAULT = BUTTON_OK | BUTTON_CANCEL;
    public static final int BUTTONS_YESNO = BUTTON_YES | BUTTON_NO;

    public static final Coord DC = new Coord(150, 150);
    public static final Coord DS = new Coord(100, 30);

    private static final int BUTTONWIDTH = 50; // px
    private final Callback<Integer> callback;

    public MessageBox(Coord position, Coord size, Widget parent, String capture, String message) {
        this(position, size, parent, capture, message, BUTTONS_DEFAULT, null);

    }

    public MessageBox(Coord position, Coord size, Widget parent, String capture, String message, int buttons, final Callback<Integer> callback) {
        super(position, new Coord(size), parent, capture, true, false);
        this.justclose = true;
        this.callback = callback;
        Label message1 = new Label(new Coord(10, 6), this, message);

        int width = Math.max(message1.sz.x, sz.x);

        int buttonsCount = 0;
        {
            int buttonsCopy = buttons;
            while (buttonsCopy > 0) {
                if ((buttonsCopy % 2) == 1) {
                    buttonsCount++;
                }
                buttonsCopy >>= 1;
            }
        }
        int allButtonsWidth = 10 + buttonsCount * (BUTTONWIDTH + 5) + 5;

        width = Math.max(width, allButtonsWidth);

        int buttonsY = 6 + message1.sz.y + 7;
        if (sz.y < buttonsY + 18) {
            sz.y = buttonsY + 18;
        }

        final MessageBox me = this;

        int lastButtonX = width - 10 - BUTTONWIDTH;

        if ((buttons & BUTTON_CANCEL) != 0) {
            new Button(new Coord(lastButtonX, buttonsY), BUTTONWIDTH, this, "Cancel", true) {
                @Override
                public void click() {
                    if (callback != null) {
                        callback.result(BUTTON_CANCEL);
                    }
                    ui.destroy(me);
                }
            };
            lastButtonX -= BUTTONWIDTH + 5;
        }
        if ((buttons & BUTTON_NO) != 0) {
            new Button(new Coord(lastButtonX, buttonsY), BUTTONWIDTH, this, "No", true) {
                @Override
                public void click() {
                    if (callback != null) {
                        callback.result(BUTTON_NO);
                    }
                    ui.destroy(me);
                }
            };
            lastButtonX -= BUTTONWIDTH + 5;
        }
        if ((buttons & BUTTON_YES) != 0) {
            new Button(new Coord(lastButtonX, buttonsY), BUTTONWIDTH, this, "Yes", true) {
                @Override
                public void click() {
                    if (callback != null) {
                        callback.result(BUTTON_YES);
                    }
                    ui.destroy(me);
                }
            };
            lastButtonX -= BUTTONWIDTH + 5;
        }
        if ((buttons & BUTTON_OK) != 0) {
            new Button(new Coord(lastButtonX, buttonsY), BUTTONWIDTH, this, "Ok", true) {
                @Override
                public void click() {
                    if (callback != null) {
                        callback.result(BUTTON_OK);
                    }
                    ui.destroy(me);
                }
            };
            lastButtonX -= BUTTONWIDTH + 5;
        }
        pack();
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (checkIsCloseButton(sender)) {
            if (callback != null) {
                callback.result(BUTTON_CANCEL);
            }
        }
        if (checkIsCloseButton(sender) || checkIsFoldButton(sender)) {
            super.wdgmsg(sender, msg, args);
        }
    }

}
