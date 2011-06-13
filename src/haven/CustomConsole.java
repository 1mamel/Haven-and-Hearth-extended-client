package haven;

import haven.scriptengine.ScriptsMachine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomConsole extends Window {
    public static Textlog out;
    public final TextEntry in;
    public static StringBuilder log = new StringBuilder("IRC-Extended Client Console - Type HELP for a list of commands");
    public static String newText = "";
    public static final List<String> lastCommands = new ArrayList<String>();
    public static int lastCommandId;
    public static boolean logChanged = false;

    public static void log(String text) {
        newText += '\n' + text;
        logChanged = true;
    }

    public void draw(GOut g) {
        if (logChanged) {
            append(newText);
            newText = "";
            logChanged = false;
        }
        super.draw(g);
    }

    CustomConsole(CustomConsole oldConsole, Widget newParent) {
        super(oldConsole.c, oldConsole.sz, newParent, oldConsole.cap.text);
        in = oldConsole.in;
        out.parent = this;
        in.parent = this;
        setfocus(in);
    }

    CustomConsole(Coord c, Coord sz, Widget parent, String title) {
        super(c, sz, parent, title, false);
        ui.bind(this, CustomConfig.getNextCustomWidgetId());
        out = new Textlog(Coord.z, sz.add(0, -20), this);
        in = new TextEntry(new Coord(0, 200), new Coord(sz.x(), 20), this, "") {
            public boolean type(char c, KeyEvent ev) {
                if (c == '`' && !(ev.isAltDown() || ev.isControlDown() || ev.isShiftDown())) {
                    ev.consume();
                    parent.toggle();
                    return true;
                }
                if (ev.getKeyCode() == KeyEvent.VK_DOWN) {
                    buf.line = lastCommands.get(lastCommandId++);
                    if (lastCommandId >= lastCommands.size()) lastCommandId = lastCommands.size() - 1;
                } else if (ev.getKeyCode() == KeyEvent.VK_UP) {
                    buf.line = lastCommands.get(lastCommandId--);
                    if (lastCommandId < 0) lastCommandId = 0;
                }
                return super.type(c, ev);
            }
        };
        in.canactivate = true;

        if (log != null) {
            final String[] lines = Utils.eoLinePattern.split(log.toString().trim());
            Thread consoleThread = new Thread(HackThread.tg(), "Console starter thread") {
                public void run() {
                    for (String line : lines) out.append(line);
                }
            };
            consoleThread.setPriority(Thread.MIN_PRIORITY);
            consoleThread.start();
        }
        setfocus(in);
    }

    public static void append(String text, Color color) {
        out.append(text, color);
        log.append('\n').append(text);
    }

    public static void append(String text) {
        append(text, Color.BLACK);
    }

    public static class OutStream extends OutputStream {

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            System.err.println(b);
            StringBuilder builder = new StringBuilder();
            for (byte sb : b) {
                builder.append(sb);
            }
            try {
                CustomConsole.append(builder.toString());
            } catch (Exception ignored) {
            }
        }

        private static OutStream ourInstance;

        public static OutStream getInstance() {
            if (ourInstance == null) ourInstance = new OutStream();
            return ourInstance;
        }

        private OutStream() {
        }

        @Override
        public void write(int b) throws IOException {
            // ignore
        }
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == in) {
            if (args[0] != null || ((String) args[0]).length() > 0) {
                lastCommands.add(((String) args[0]).trim());
                lastCommandId++;
                String cmdText = ((String) args[0]).trim().toUpperCase();
                String cmd = cmdText.contains(" ") ? cmdText.substring(0, cmdText.indexOf(' ')).trim() : cmdText;
                cmdText = cmdText.contains(" ") ? cmdText.substring(cmdText.indexOf(' ')).trim() : "";
                append("Command: " + cmd + "\nArguments: " + cmdText, Color.BLUE.darker());
                String[] cmdArgs = Utils.whitespacePattern.split(cmdText);
                in.settext("");
                String arg0 = cmdArgs[0];
                if (cmd.equals("DEBUG")) {
                    if (arg0.trim().length() != 0) {
                        if (arg0.equals("IRC")) {
                            if (cmdArgs.length >= 2) {
                                if (cmdArgs[1].equals("ON") || cmdArgs[1].equals("TRUE")) {
                                    CustomConfig.logIRC = true;
                                } else if (cmdArgs[1].equals("OFF") || cmdArgs[1].equals("FALSE")) {
                                    CustomConfig.logIRC = false;
                                }
                            } else {
                                append("DEBUG LOGS", Color.BLUE.darker());
                                append("IRC - " + (CustomConfig.logIRC ? "ON" : "OFF"), Color.GREEN.darker());
                            }
                        } else if (arg0.equals("SRVMSG")) {
                            if (cmdArgs.length >= 2) {
                                if (cmdArgs[1].equals("ON") || cmdArgs[1].equals("TRUE")) {
                                    CustomConfig.logServerMessages = true;
                                } else if (cmdArgs[1].equals("OFF") || cmdArgs[1].equals("FALSE")) {
                                    CustomConfig.logServerMessages = false;
                                }
                            } else {
                                append("DEBUG LOGS", Color.BLUE.darker());
                                append("SRVMSG - " + (CustomConfig.logServerMessages ? "ON" : "OFF"), Color.GREEN.darker());
                            }
                        }
                        if (arg0.equals("LOAD")) {
                            if (cmdArgs.length >= 2) {
                                if (cmdArgs[1].equals("ON") || cmdArgs[1].equals("TRUE")) {
                                    CustomConfig.logLoad = true;
                                } else if (cmdArgs[1].equals("OFF") || cmdArgs[1].equals("FALSE")) {
                                    CustomConfig.logLoad = false;
                                }
                            } else {
                                append("DEBUG LOGS", Color.BLUE.darker());
                                append("LOAD - " + (CustomConfig.logLoad ? "ON" : "OFF"), Color.GREEN.darker());
                            }
                        }
                    } else {
                        append("DEBUG LOGS", Color.BLUE.darker());
                        append("IRC - " + (CustomConfig.logIRC ? "ON" : "OFF"), Color.GREEN.darker());
                        append("LOAD - " + (CustomConfig.logLoad ? "ON" : "OFF"), Color.GREEN.darker());
                        append("SRVMSG - " + (CustomConfig.logServerMessages ? "ON" : "OFF"), Color.GREEN.darker());
                    }
                } else {
                    ScriptsMachine.executeScript((String) args[0]);
                }
            }
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    public boolean toggle() {
        if (super.toggle())
            setfocus(in);
        return visible;
    }
}
