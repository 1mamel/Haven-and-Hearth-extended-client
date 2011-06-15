package haven;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 15.06.11
 * Time: 22:25
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class TextInputBox extends Window {

    private static final int BUTTONWIDTH = 50; // px
    private final Callback<String> callback;
    private final TextEntry input;

    public TextInputBox(Coord position, Coord size, Widget parent, String capture, String message, final Callback<String> callback) {
        super(position, size, parent, capture, true, false);
        this.justclose = true;
        this.callback = callback;
        Label message1 = new Label(new Coord(10, 6), this, message);
        input = new TextEntry(new Coord(10, 23), new Coord(sz.x, 30), this, "");

        int width = Math.max(message1.sz.x, sz.x);

        int allButtonsWidth = 10 + 2 * (BUTTONWIDTH + 5) + 5;

        width = Math.max(width, allButtonsWidth);

        int buttonsY = 58;
        if (sz.y < buttonsY + 18) {
            sz.y = buttonsY + 18;
        }

        final TextInputBox me = this;

        int lastButtonX = width - 10 - BUTTONWIDTH;
        new Button(new Coord(lastButtonX, buttonsY), BUTTONWIDTH, this, "Cancel", true) {
            @Override
            public void click() {
                callback.result(null);
                ui.destroy(me);
            }
        };
        lastButtonX -= BUTTONWIDTH + 5;
        new Button(new Coord(lastButtonX, buttonsY), BUTTONWIDTH, this, "Ok", true) {
            @Override
            public void click() {
                callback.result(input.getText());
                ui.destroy(me);
            }
        };
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (checkIsCloseButton(sender)) {
            callback.result(null);
        }
        if (checkIsCloseButton(sender) || checkIsFoldButton(sender)) {
            super.wdgmsg(sender, msg, args);
        }
    }

}
