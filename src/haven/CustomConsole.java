package haven;

import haven.scriptengine.ScriptsManager;
import org.apache.log4j.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CustomConsole extends Window {
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    public static final Logger logger = Logger.getLogger(CustomConsole.class);
    private static final Appender mainAppender;

    private final List<String> enteredCommands = new ArrayList<String>();
    private int lastCommand;
    public final Textlog out;
    private final TextEntry in;

    static {
        Appender mainAppender1;
        try {
            mainAppender1 = new FileAppender(new SimpleLayout(), "console.log");
        } catch (IOException e) {
            System.err.println("Cannot create file appender for CustomConsole logger");
            e.printStackTrace(System.err);
            mainAppender1 = new ConsoleAppender(new SimpleLayout());
        }
        mainAppender = mainAppender1;
    }

    public void draw(GOut g) {
        super.draw(g);
    }

    CustomConsole(Coord c, Coord sz, Widget parent, String title) {
        super(c, sz, parent, title, false, false);
        ui.bind(this, CustomConfig.getNextCustomWidgetId());

        out = new Textlog(Coord.z, sz.add(0, -20), this);

        in = new TextEntry(new Coord(0, sz.y - 20), new Coord(sz.x, 20), this, "") {
            public boolean type(char c, KeyEvent ev) {
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

        CustomWriter outWriter = new CustomWriter();
        CustomWriter errWriter = new CustomWriter(Color.RED.darker());

        logger.removeAllAppenders();
        logger.addAppender(mainAppender);
        logger.addAppender(new WriterAppender(new SimpleLayout(), outWriter));

        ScriptsManager.registerOut(outWriter);
        ScriptsManager.registerErr(errWriter);

        setfocus(in);
    }

    public class CustomWriter extends Writer {
        private final Color color;

        CustomWriter() {
            this.color = DEFAULT_TEXT_COLOR;
        }

        CustomWriter(Color color) {
            this.color = color;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            String line = new String(cbuf, off, len);
            try {
                System.out.println("console writer:" + line);
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

    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender != in) {
            super.wdgmsg(sender, msg, args);
            return;
        }

        // Assume message from text input
        if (!(args[0] instanceof String)) {
            return;
        }

        String command = (String) args[0];
        enteredCommands.add(command);
        lastCommand++;

        ScriptsManager.exec(command);
        in.settext("");


//        String cmdText = ((String) args[0]).trim().toUpperCase();
//        String cmd = cmdText.contains(" ") ? cmdText.substring(0, cmdText.indexOf(' ')).trim() : cmdText;
//        cmdText = cmdText.contains(" ") ? cmdText.substring(cmdText.indexOf(' ')).trim() : "";
//        String[] cmdArgs = Utils.whitespacePattern.split(cmdText);
//        String arg0 = cmdArgs[0];
//        if (cmd.equals("DEBUG")) {
//            if (arg0.trim().length() != 0) {
//                if (arg0.equals("IRC")) {
//                    if (cmdArgs.length >= 2) {
//                        if (cmdArgs[1].equals("ON") || cmdArgs[1].equals("TRUE")) {
//                            CustomConfig.logIRC = true;
//                        } else if (cmdArgs[1].equals("OFF") || cmdArgs[1].equals("FALSE")) {
//                            CustomConfig.logIRC = false;
//                        }
//                    } else {
//                        append("DEBUG LOGS", Color.BLUE.darker());
//                        append("IRC - " + (CustomConfig.logIRC ? "ON" : "OFF"), Color.GREEN.darker());
//                    }
//                } else if (arg0.equals("SRVMSG")) {
//                    if (cmdArgs.length >= 2) {
//                        if (cmdArgs[1].equals("ON") || cmdArgs[1].equals("TRUE")) {
//                            CustomConfig.logServerMessages = true;
//                        } else if (cmdArgs[1].equals("OFF") || cmdArgs[1].equals("FALSE")) {
//                            CustomConfig.logServerMessages = false;
//                        }
//                    } else {
//                        append("DEBUG LOGS", Color.BLUE.darker());
//                        append("SRVMSG - " + (CustomConfig.logServerMessages ? "ON" : "OFF"), Color.GREEN.darker());
//                    }
//                }
//                if (arg0.equals("LOAD")) {
//                    if (cmdArgs.length >= 2) {
//                        if (cmdArgs[1].equals("ON") || cmdArgs[1].equals("TRUE")) {
//                            CustomConfig.logLoad = true;
//                        } else if (cmdArgs[1].equals("OFF") || cmdArgs[1].equals("FALSE")) {
//                            CustomConfig.logLoad = false;
//                        }
//                    } else {
//                        append("DEBUG LOGS", Color.BLUE.darker());
//                        append("LOAD - " + (CustomConfig.logLoad ? "ON" : "OFF"), Color.GREEN.darker());
//                    }
//                }
//            } else {
//                append("DEBUG LOGS", Color.BLUE.darker());
//                append("IRC - " + (CustomConfig.logIRC ? "ON" : "OFF"), Color.GREEN.darker());
//                append("LOAD - " + (CustomConfig.logLoad ? "ON" : "OFF"), Color.GREEN.darker());
//                append("SRVMSG - " + (CustomConfig.logServerMessages ? "ON" : "OFF"), Color.GREEN.darker());
//            }
//        } else {
//            ScriptsManager.exec((String) args[0]);
//        }
    }

    public boolean toggle() {
        if (super.toggle())
            setfocus(in);
        return visible;
    }
}
