package haven;

import haven.scriptengine.ConsoleCommandListener;
import haven.scriptengine.ScriptsConsole;
import haven.scriptengine.ScriptsManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CustomConsole extends Window implements ScriptsConsole {
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final Logger logger = Logger.getLogger(CustomConsole.class);

    private final List<String> enteredCommands = new ArrayList<String>();
    private int lastCommand;
    public final Textlog out;
    private final TextEntry in;

    @Nullable
    private ConsoleCommandListener myListener;
    private final CustomWriter outWriter;
    private final CustomWriter errWriter;

    public void draw(final GOut g) {
        super.draw(g);
    }

    CustomConsole(final Coord c, final Coord sz, final Widget parent, final String title) {
        super(c, sz, parent, title, false, false);
        ui.bind(this, CustomConfig.current().getNextCustomWidgetId());

        out = new Textlog(Coord.z, sz.add(0, -20), this);

        in = new TextEntry(new Coord(0, sz.y - 20), new Coord(sz.x, 20), this, "") {
            public boolean type(final char c, final KeyEvent ev) {
                if (c == '`' && !(ev.isAltDown() || ev.isControlDown() || ev.isShiftDown())) {
                    ev.consume();
                    parent.toggle();
                    return true;
                }
                if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
                    buf.line = enteredCommands.get(lastCommand++);
                    if (lastCommand >= enteredCommands.size()) {
                        lastCommand = enteredCommands.size() - 1;
                    }
                } else if (ev.getKeyCode() == KeyEvent.VK_UP) {
                    buf.line = enteredCommands.get(lastCommand--);
                    if (lastCommand < 0) {
                        lastCommand = 0;
                    }
                }
                return super.type(c, ev);
            }
        };
        in.canactivate = true;

        outWriter = new CustomWriter();
        errWriter = new CustomWriter(Color.RED.darker());

        ScriptsManager.registerConsole(this);

        setfocus(in);
    }

    @Override
    public void setCommandListener(@Nullable final ConsoleCommandListener listener) {
        myListener = listener;
    }

    @NotNull
    @Override
    public Writer getConsoleStdOutWriter() {
        return outWriter;
    }

    @NotNull
    @Override
    public Writer getConsoleStdErrWriter() {
        return errWriter;
    }

    public class CustomWriter extends Writer {
        private final Color color;

        CustomWriter() {
            this.color = DEFAULT_TEXT_COLOR;
        }

        CustomWriter(final Color color) {
            this.color = color;
        }

        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            final String line = new String(cbuf, off, len);
            try {
                out.append(line, color);
            } catch (Exception ignored) {
            }
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (sender != in) {
            super.wdgmsg(sender, msg, args);
            return;
        }

        // Assume message from text input
        if (!(args[0] instanceof String)) {
            return;
        }

        final String command = (String) args[0];
        if (myListener != null) {
            myListener.onCommandSubmitted(command);
            enteredCommands.add(command);
            lastCommand++;
            in.settext("");
        }
    }

    public boolean toggle() {
        if (super.toggle())
            setfocus(in);
        return visible;
    }

    @Override
    public void destroy() {
        ScriptsManager.unregisterConsole(this);
        super.destroy();
    }
}
