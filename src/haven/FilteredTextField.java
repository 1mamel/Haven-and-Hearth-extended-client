package haven;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 12.01.11
 * Time: 14:15
 */
class FilteredTextField extends JTextField {

    static final String defbadchars = "`~!@#$%^&*()_-+=\\|\"':;?/>.<, ";
    String badchars = defbadchars;
    boolean noLetters = false;
    boolean noNumbers = false;
    int maxCharacters = 0;

    public void processKeyEvent(final KeyEvent ev) {

        final char c = ev.getKeyChar();

        if (Character.isDigit(c) && noNumbers && !ev.isAltDown() || badchars.indexOf(c) > -1) {
            ev.consume();
            return;
        }
        if (Character.isLetter(c) && noLetters && !ev.isAltDown() || badchars.indexOf(c) > -1) {
            ev.consume();
            return;
        }
        if (getText().length() >= maxCharacters
                && maxCharacters > 0
                && ev.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && ev.getKeyCode() != KeyEvent.VK_LEFT
                && ev.getKeyCode() != KeyEvent.VK_RIGHT
                && ev.getKeyCode() != KeyEvent.VK_HOME
                && ev.getKeyCode() != KeyEvent.VK_END) {
            ev.consume();
            return;
        }
        super.processKeyEvent(ev);

    }

    public void setBadChars(final String badchars) {
        this.badchars = badchars;
    }

    public void setMaxCharacters(final int maxChars) {
        maxCharacters = maxChars;
    }

    public void setDefaultBadChars() {
        badchars = defbadchars;
    }

    public void setNoNumbers(final boolean state) {
        noNumbers = state;
    }

    public void setNoLetters(final boolean state) {
        noLetters = state;
    }
}

