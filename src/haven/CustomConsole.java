package haven;

import haven.scriptengine.ScriptsMachine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Writer;
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
        ui.bind(this, CustomConfig.wdgtID++);
        out = new Textlog(Coord.z, sz.add(0, -20), this);
        in = new TextEntry(new Coord(0, 200), new Coord(sz.x, 20), this, "") {
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
            final String[] lines = log.toString().trim().split("\n");
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

    public static class OutWriter extends Writer {

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            StringBuilder b = new StringBuilder();
            b.append(cbuf, off, len);
            try {
                CustomConsole.append(b.toString());
            } catch (Exception ignored) {
            }
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }

        private static OutWriter ourInstance;

        public static OutWriter getInstance() {
            if (ourInstance == null) ourInstance = new OutWriter();
            return ourInstance;
        }

        private OutWriter() {
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
                String[] cmdArgs = cmdText.split(" ");
                in.settext("");
                String arg0 = cmdArgs[0];
                if (cmd.equals("NIGHTVISION")) {
                    if (arg0.trim().length() != 0) {
                        if (arg0.equals("ON") || arg0.equals("TRUE"))
                            CustomConfig.hasNightVision = true;
                        if (arg0.equals("OFF") || arg0.equals("FALSE"))
                            CustomConfig.hasNightVision = false;
                    } else {
                        append("NIGHTVISION - " + (CustomConfig.hasNightVision ? "ON" : "OFF"));
                    }
                } else if (cmd.equals("IRC")) {
                    if (arg0.trim().length() != 0) {
                        if (arg0.equals("ON") || arg0.equals("TRUE"))
                            CustomConfig.isIRCOn = true;
                        if (arg0.equals("OFF") || arg0.equals("FALSE"))
                            CustomConfig.isIRCOn = false;
                    } else {
                        append("IRC - " + (CustomConfig.isIRCOn ? "ON" : "OFF"));
                    }
                } else if (cmd.equals("SCREENSIZE") || cmd.equals("WINDOWSIZE")) {
                    if (arg0.trim().length() != 0 && cmdArgs.length >= 2) {
                        try {
                            int x = Integer.parseInt(arg0);
                            int y = Integer.parseInt(cmdArgs[1]);
                            if (x >= 800 && y >= 600) {
                                CustomConfig.setWindowSize(x, y);
                            }
                            CustomConfig.saveSettings();
                            append("Client must be restarted for new settings to take effect.", Color.RED.darker());
                        } catch (NumberFormatException e) {
                            append("Dimensions must be numbers");
                        }
                    } else {
                        append("SCREENSIZE = " + CustomConfig.windowSize.toString());
                    }
                } else if (cmd.equals("SOUND")) {
                    int vol;
                    if (arg0.trim().length() != 0) {
                        try {
                            if (arg0.equals("ON") || arg0.equals("TRUE")) {
                                CustomConfig.isSoundOn = true;
                            } else if (arg0.equals("OFF") || arg0.equals("FALSE")) {
                                CustomConfig.isSoundOn = false;
                            } else if ((vol = Integer.parseInt(arg0)) >= 0 && vol <= 100) {
                                CustomConfig.sfxVol = vol;
                            } else throw new NumberFormatException("vol = " + vol);
                        } catch (NumberFormatException e) {
                            append("Volume must be an integer between 0-100");
                        }
                    } else {
                        append("SOUND = " + (CustomConfig.isSoundOn ? "ON  " : "OFF ")
                                + "VOLUME = " + CustomConfig.sfxVol);
                    }
                } else if (cmd.equals("MUSIC")) {
                    int vol;
                    if (arg0.trim().length() != 0) {
                        try {
                            if (arg0.equals("ON") || arg0.equals("TRUE")) {
                                CustomConfig.isMusicOn = true;
                            } else if (arg0.equals("OFF") || arg0.equals("FALSE")) {
                                CustomConfig.isMusicOn = false;
                            } else if ((vol = Integer.parseInt(arg0)) >= 0 && vol <= 100) {
                                CustomConfig.musicVol = vol;
                            } else throw new NumberFormatException("vol = " + vol);
                        } catch (NumberFormatException e) {
                            append("Volume must be an integer between 0-100");
                        }
                    } else {
                        append("MUSIC = " + (CustomConfig.isMusicOn ? "ON  " : "OFF ")
                                + "VOLUME = " + CustomConfig.musicVol);
                    }
                } else if (cmd.equals("SAVE")) {
                    CustomConfig.saveSettings();
                } else if (cmd.equals("FORCESAVE")) {
                    CustomConfig.isSaveable = true;
                    CustomConfig.saveSettings();
                } else if (cmd.equals("DEBUG")) {
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
                } else if (cmd.equals("HELP")) {
                    append("You can check the current status of each variable by "
                            + "typing the command without arguments.", Color.RED.darker());
                    append("NIGHTVISION TRUE | FALSE | ON | OFF - Turns nightvision on or off");
                    append("SCREENSIZE #### #### - Sets the screensize to the specified size.");
                    append("SOUND TRUE | FALSE | ON | OFF | 0-100 - Turns the sound effects on/off, or sets "
                            + "the volume to the specified level");
                    append("MUSIC TRUE | FALSE | ON | OFF | 0-100 - Turns the music on/off, or sets "
                            + "the volume to the specified level");
                    append("SAVE - Saves the current settings if they are saveable.");
                    append("FORCESAVE - Saves the current settings whether or not they are "
                            + "saveable (Might cause errors).");
                    append("DEBUG IRC | LOAD   ON | OFF - Enables/disables debug text being dumped into the console "
                            + "for the specified system.");
                    append("HELP - Shows this text.");
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
