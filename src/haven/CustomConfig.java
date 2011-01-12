/**
 * @(#)CustomConfig.java
 *
 *
 * @author
 * @version 1.00 2009/10/19
 */

package haven;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedDeclaration"})
public class CustomConfig {
    static UI ui;
    public static OpenedInv.InvItem atMouseItem = null;
    public static boolean debugMsgs = true;

    public static void openInventory(int id, String name, Coord size, Coord pos) {
        openedInventories.put(id, new OpenedInv(id, name, size, pos));
    }

    public static void closeInventory(int id) {
        if (openedInventories.containsKey(id))
            openedInventories.remove(id);
    }

    public static void closeWidget(int id) {
        try {
            closeInventory(id);
            //TODO widget type maybe Item
            if ((atMouseItem != null) && (id == atMouseItem.getId())) atMouseItem = null;
            Widget wtd = ui.getWidgetById(id);
            int parent = ui.getIdByWidget(wtd.parent);
            if (openedInventories.containsKey(parent)) {
                openedInventories.get(parent).deleteItem(id);
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void newItem(int parent, int id, int itype, int iquality, Coord position) {
        try {
            if (parent == 0) {
                atMouseItem = new OpenedInv.InvItem(itype, iquality, id);
            }

            if (!openedInventories.containsKey(parent)) return;
            OpenedInv.InvItem item = new OpenedInv.InvItem(itype, iquality, id);
            OpenedInv inventory = openedInventories.get(parent);
            inventory.setItem(position.add(new Coord(-1, -1).div(new Coord(31, 31))), item);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static class OpenedInv {
        @SuppressWarnings({"UnusedDeclaration"})
        public static class InvItem {
            int type;
            int quality;
            int id;

            public InvItem(int type, int quality, int id) {
                this.type = type;
                this.quality = quality;
                this.id = id;
            }

            public int getType() {
                return type;
            }

            public int getQuality() {
                return quality;
            }

            public int getId() {
                return id;
            }

        }

        Map<Coord, InvItem> items;
        Coord size;
        String name;
        int id;
        Coord screenCoordinates;

        public OpenedInv(int id, String name, Coord size, Coord position) {
            this.id = id;
            this.name = name;
            this.size = size;
            this.screenCoordinates = position;
            this.items = new HashMap<Coord, InvItem>();
        }

        public void setItem(Coord position, InvItem item) {
            items.put(position, item);
        }

        public void deleteItem(Coord position) {
            if (items.containsKey(position))
                items.remove(position);
        }

        public void deleteItem(int id) {
            for (Map.Entry<Coord, InvItem> itempair : items.entrySet())
                if (itempair.getValue().getId() == id) {
                    items.remove(itempair.getKey());
                    return;
                }

        }

        public InvItem getItem(Coord position) {
            return items.get(position);
        }

        public static Coord getItemScreenPosition(Coord position) {
            return position.add(new Coord(position.x, position.y).mul(31).add(1, 1));
        }

        public Coord getSize() {
            return size;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }

    public static Map<Integer, OpenedInv> openedInventories = new HashMap<Integer, OpenedInv>();

    static class FilteredTextField extends JTextField {

        String defbadchars = "`~!@#$%^&*()_-+=\\|\"':;?/>.<, ";
        String badchars = defbadchars;
        boolean noLetters = false;
        boolean noNumbers = false;
        int maxCharacters = 0;

        public void processKeyEvent(KeyEvent ev) {

            char c = ev.getKeyChar();

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

        public void setBadChars(String badchars) {
            this.badchars = badchars;
        }

        public void setMaxCharacters(int maxChars) {
            maxCharacters = maxChars;
        }

        public void setDefaultBadChars() {
            badchars = defbadchars;
        }

        public void setNoNumbers(boolean state) {
            noNumbers = state;
        }

        public void setNoLetters(boolean state) {
            noLetters = state;
        }
    }

    static class CharData {
        String name;
        int hudActiveBelt = 1;
        String[][] hudBelt = new String[SlenHud._BELTSIZE][SlenHud._BELTSIZE];

        CharData(String name) {
            this.name = name;
        }

        public String toString() {
            return "Name=\"" + name + '\"';
        }
    }

    //windows last positions
    public static Map<String, Coord> windowsCoordinates = new HashMap<String, Coord>();

    public static void setWindowPosition(String windowName, Coord coordinate) {
        windowsCoordinates.put(windowName, coordinate);
    }

    public static Coord getWindowPosition(String windowName, Coord defcoordinates) {
        if (!windowsCoordinates.containsKey(windowName)) return defcoordinates;
        return windowsCoordinates.get(windowName);
    }

    public static Coord windowSize = new Coord(800, 600);
    public static Coord windowCenter = windowSize.div(2);
    public static Coord invCoord = Coord.z;
    public static int sfxVol = 100;
    public static int musicVol = 100;
    public static String ircServerAddress = "irc.synirc.net";
    public static List<Listbox.Option> ircChannelList = new ArrayList<Listbox.Option>();
    public static List<CharData> characterList = new ArrayList<CharData>();
    public static String ircDefNick = "";
    public static String ircAltNick = "";
    public static CharData activeCharacter;
    public static int wdgtID = -100; // for Userspace widgets
    public static boolean isMusicOn = true;
    public static boolean isSoundOn = true;
    public static boolean isIRCOn = true;
    public static boolean hasNightVision = false;
    public static boolean isSaveable = false;
    public static boolean noChars = true;
    public static CustomConsole console;

    public static boolean logLoad = false;
    public static boolean logSave = false;
    public static boolean logIRC = false;
    public static boolean logServerMessages = false;

    public static void setActiveCharacter(String name) {
        for (CharData cData : characterList) {
            if (cData.name.equalsIgnoreCase(name)) {
                activeCharacter = cData;
                CustomConfig.isSaveable = true;
                CustomConfig.noChars = false;
                return;
            }
        }
        activeCharacter = new CharData(name);
        characterList.add(activeCharacter);
        CustomConfig.isSaveable = true;
        CustomConfig.noChars = false;
    }

    public static void setWindowSize(int x, int y) {
        setWindowSize(new Coord(x, y));
    }

    public static void setWindowSize(Coord size) {
        windowSize = new Coord(size);
        windowCenter = windowSize.div(2);
    }

    public static void setDefaults() {
        setWindowSize(800, 600);

        sfxVol = 100;
        musicVol = 100;
        ircServerAddress = "irc.synirc.net";
        ircChannelList.clear();
        ircChannelList.add(new Listbox.Option("#Haven", ""));
        ircDefNick = "";
        ircAltNick = "";
        isMusicOn = true;
        isSoundOn = true;
        hasNightVision = false;
    }

    public static boolean load() {
        setDefaults();
        BufferedReader reader = null;
        try {
            SAXParserFactory spFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = spFactory.newSAXParser();

            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new DefaultHandler() {
                private boolean ircElementActive = false;
                private boolean beltElementActive = false;
                private boolean beltListElementActive = false;
                private int activeBelt = 0;

                public void startElement(String namespaceURI, String localName,
                                         String qName, Attributes atts) throws SAXException {
                    String value;
                    String key = qName.toUpperCase().trim();

                    //	Logs the loading sequence on the console
                    if (logLoad) {
                        CustomConsole.log.append("|| ").append(key).append(" \t ");
                        for (int i = 0; i < atts.getLength(); i++) {
                            CustomConsole.log.append(" \t ").append(atts.getQName(i)).append(" \t ").append(atts.getValue(i));
                        }
                        if (console != null) {
                            CustomConsole.out.append(CustomConsole.log.toString());
                            CustomConsole.log = new StringBuilder();
                        } else {
                            CustomConsole.log.append("\n");
                        }
                    }

                    if (key.equals("SCREENSIZE")) {
                        value = atts.getValue("width") == null ? "1024" : atts.getValue("width");
                        windowSize.x = Integer.parseInt(value);

                        value = atts.getValue("height") == null ? "1024" : atts.getValue("height");
                        windowSize.y = Integer.parseInt(value);
                        setWindowSize(windowSize);
                    } else if (key.equals("SOUND")) {
                        value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
                        isSoundOn = Boolean.parseBoolean(value);

                        value = atts.getValue("volume") == null ? "100" : atts.getValue("volume");
                        sfxVol = Integer.parseInt(value);
                    } else if (key.equals("MUSIC")) {
                        value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
                        isMusicOn = Boolean.parseBoolean(value);

                        value = atts.getValue("volume") == null ? "100" : atts.getValue("volume");
                        musicVol = Integer.parseInt(value);
                    } else if (key.equals("IRC") && !(beltElementActive || ircElementActive)) {
                        ircElementActive = true;
                        ircChannelList.clear();
                        value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
                        isIRCOn = Boolean.parseBoolean(value);

                        value = atts.getValue("server") == null ? "irc.synirc.net" : atts.getValue("server");
                        ircServerAddress = value;

                        value = atts.getValue("default-nick") == null ? "" : atts.getValue("default-nick");
                        ircDefNick = value;

                        value = atts.getValue("alternate-nick") == null ? "" : atts.getValue("alternate-nick");
                        ircAltNick = value;
                    } else if (key.equals("CHANNEL") && atts.getValue("name") != null
                            && ircElementActive) {
                        value = atts.getValue("password") == null ? "" : atts.getValue("password");
                        Listbox.Option chan = new Listbox.Option(atts.getValue("name"), value.trim());
                        ircChannelList.add(chan);
                    } else if (key.equals("BELT-LIST") && !(beltElementActive || ircElementActive
                            || beltListElementActive)
                            && atts.getValue("name") != null) {
                        beltListElementActive = true;
                        activeCharacter = new CharData(atts.getValue("name"));
                        activeCharacter.hudActiveBelt = Integer.parseInt(atts.getValue("active-belt"));
                        noChars = false;
                    } else if (key.equals("BELT") && !(beltElementActive || ircElementActive)
                            && beltListElementActive) {
                        beltElementActive = true;
                        activeBelt = Integer.parseInt(atts.getValue("value"));
                    } else if (key.equals("SLOT") && atts.getValue("value") != null && atts.getValue("position") != null
                            && beltElementActive
                            && beltListElementActive) {
                        activeCharacter.hudBelt[activeBelt][Integer.parseInt(atts.getValue("position"))]
                                = atts.getValue("value").equalsIgnoreCase("null") ? null : atts.getValue("value");
                    }
                }

                public void endElement(String namespaceURI, String localName,
                                       String qName) throws SAXException {
                    if (ircElementActive && qName.equals("IRC")) {
                        ircElementActive = false;
                    } else if (beltElementActive && qName.equals("BELT")) {
                        beltElementActive = false;
                    } else if (beltListElementActive && qName.equals("BELT-LIST")) {
                        beltListElementActive = false;
                        characterList.add(activeCharacter);
                    }
                }
            });
            if (ResCache.global != null) {
                xmlReader.parse(new InputSource(ResCache.global.fetch("config.xml")));
            } else {
                xmlReader.parse("config.xml");
            }
            if (windowSize.x < 800 || windowSize.y < 600) {
                System.out.println("Window size must be at least 800x600");
                setWindowSize(800, 600);
            }
            return true;
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("Config file not found, creating a new one");
        } catch (IOException IOExcep) {
            IOExcep.printStackTrace();
        } catch (NullPointerException NPExcep) {
            System.out.println("File format corrupted, creating a new one");
            NPExcep.printStackTrace();
        } catch (NumberFormatException NFExcep) {
            System.out.println("Wrong config file format, creating a new one");
        } catch (ParserConfigurationException pcExcep) {
            pcExcep.printStackTrace();
        } catch (SAXException saxExcep) {
            saxExcep.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public static double getSFXVolume() {
        return (double) sfxVol / 100;
    }

    public static synchronized void saveSettings() {
        try {
            BufferedWriter writer;
            if (ResCache.global != null) {
                writer = new BufferedWriter(new OutputStreamWriter(ResCache.global.store("config.xml"), "UTF-8"));
            } else {
                writer = new BufferedWriter(new FileWriter(new File("config.xml")));
            }
            writer.write("<?xml version=\"1.0\" ?>\n");
            writer.write("<CONFIG>\n");
            writer.write("\t<SCREENSIZE width=\"" + windowSize.x + "\" height=\"" + windowSize.y + "\"/>\n");
            writer.write("\t<SOUND enabled=\"" + Boolean.toString(isSoundOn)
                    + "\" volume=\"" + Integer.toString(sfxVol) + "\"/>\n");
            writer.write("\t<MUSIC enabled=\"" + Boolean.toString(isMusicOn)
                    + "\" volume=\"" + Integer.toString(musicVol) + "\"/>\n");
            writer.write("\t<IRC enabled=\"" + Boolean.toString(isIRCOn)
                    + "\" server=\"" + ircServerAddress
                    + "\" default-nick=\"" + ircDefNick
                    + "\" alternate-nick=\"" + ircAltNick + "\">\n");
            for (Listbox.Option channel : ircChannelList) {
                writer.write("\t\t<CHANNEL name=\"" + channel.name + "\" password=\"" + channel.disp + "\"/>\n");
            }
            writer.write("\t</IRC>\n");

            for (CharData cData : characterList) {
                if (noChars) break;
                if (cData.name.equals(activeCharacter.name)) cData.hudActiveBelt = activeCharacter.hudActiveBelt;
                writer.write("\t<BELT-LIST name=\"" + cData.name
                        + "\" active-belt=\"" + Integer.toString(cData.hudActiveBelt) + "\">\n");
                for (int i = 0; i < cData.hudBelt.length; i++) {
                    writer.write("\t\t<BELT value=\"" + Integer.toString(i) + "\">\n");
                    for (int j = 0; j < cData.hudBelt[i].length; j++) {
                        writer.write("\t\t\t<SLOT value=\"" + cData.hudBelt[i][j]
                                + "\" position=\"" + Integer.toString(j) + "\"/>\n");
                    }
                    writer.write("\t\t</BELT>\n");
                }
                writer.write("\t</BELT-LIST>\n");
            }
            writer.write("</CONFIG>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String args[]) {
        if (!load()) {
            setDefaults();
            final JFrame configFrame = new JFrame("Screen Size");
            Container contentPane = configFrame.getContentPane();
            final JPanel clientSettingsPanel = new JPanel(new GridBagLayout(), true);
            final JPanel ircSettingsPanel = new JPanel(new GridBagLayout(), true);
            JButton startBtn = new JButton("Start!");
            GridBagConstraints constraints;
            final JCheckBox ircOn = new JCheckBox("IRC Enabled", true);
            final FilteredTextField xField = new FilteredTextField();
            final FilteredTextField yField = new FilteredTextField();
            final FilteredTextField ircDefNickField = new FilteredTextField();
            final FilteredTextField ircAltNickField = new FilteredTextField();
            final JRadioButton typeStandard = new JRadioButton("Standard resolution:", true);
            final JRadioButton typeCustom = new JRadioButton("Custom resolution:", false);
            final JComboBox stdRes = new JComboBox(new Coord[]{
                    new Coord(800, 600),
                    new Coord(1024, 768),
                    new Coord(1280, 720),
                    new Coord(1280, 768),
                    new Coord(1280, 800)
            });

            configFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            stdRes.setSelectedIndex(0);
            stdRes.setEditable(false);

            xField.setNoLetters(true);
            yField.setNoLetters(true);
            xField.setColumns(4);
            yField.setColumns(4);
            xField.setText("800");
            yField.setText("600");
            xField.setEditable(false);
            yField.setEditable(false);

            ircDefNickField.setBadChars("@#$%^~&? ");
            ircAltNickField.setBadChars("@#$%^~&? ");
            ircDefNickField.setColumns(10);
            ircAltNickField.setColumns(10);
            ircDefNickField.setMaxCharacters(30);
            ircAltNickField.setMaxCharacters(30);

            contentPane.setLayout(new GridBagLayout());

            //	Adding client components
            constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;

            constraints.gridx = 0;
            constraints.gridy = 0;
            clientSettingsPanel.add(typeStandard, constraints);

            constraints.gridx = 1;
            constraints.gridwidth = 2;
            clientSettingsPanel.add(stdRes, constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.gridwidth = 1;
            clientSettingsPanel.add(typeCustom, constraints);

            constraints.gridx = 1;
            clientSettingsPanel.add(xField, constraints);

            constraints.gridx = 2;
            clientSettingsPanel.add(yField, constraints);

            //	Adding irc components
            constraints.gridx = 0;
            constraints.gridy = 0;
            ircSettingsPanel.add(new JLabel("Default IRC Nickname:"), constraints);

            constraints.gridy = 1;
            ircSettingsPanel.add(new JLabel("Alternate Nickname:"), constraints);

            constraints.gridx = 1;
            constraints.gridy = 0;
            ircSettingsPanel.add(ircDefNickField, constraints);

            constraints.gridy = 1;
            ircSettingsPanel.add(ircAltNickField, constraints);

            //	Adding panel components
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.gridx = 0;
            constraints.gridy = 0;
            contentPane.add(clientSettingsPanel, constraints);

            constraints.gridx = 2;
            constraints.gridy = 0;
            contentPane.add(ircSettingsPanel, constraints);

            constraints.gridx = 2;
            constraints.gridy = 2;
            constraints.insets.top = 10;
            clientSettingsPanel.add(startBtn, constraints);

            constraints.gridx = 1;
            constraints.gridy = 3;
            constraints.insets.top = 0;
            clientSettingsPanel.add(ircOn, constraints);

            ircOn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    isIRCOn = ircOn.isSelected();
                }
            });

            typeStandard.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (!typeStandard.isSelected() && !typeCustom.isSelected()) {
                        typeStandard.setSelected(true);
                    }
                    if (typeStandard.isSelected()) {
                        stdRes.setEnabled(true);
                        typeCustom.setSelected(false);
                    } else {
                        stdRes.setEnabled(false);
                    }
                }
            });
            typeCustom.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (!typeStandard.isSelected() && !typeCustom.isSelected()) {
                        typeCustom.setSelected(true);
                    }
                    if (typeCustom.isSelected()) {
                        xField.setEnabled(true);
                        yField.setEnabled(true);
                        xField.setEditable(true);
                        yField.setEditable(true);
                        typeStandard.setSelected(false);
                        stdRes.setEnabled(false);
                    } else {
                        xField.setEditable(false);
                        yField.setEditable(false);
                        xField.setEnabled(false);
                        yField.setEnabled(false);
                        stdRes.setEnabled(true);
                    }
                }
            });
            xField.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                }

                public void focusLost(FocusEvent e) {
                    try {
                        if (Integer.parseInt(xField.getText()) < 800) {
                            xField.setText("800");
                        }
                    } catch (NumberFormatException NFExcep) {
                        xField.setText("800");
                    }
                }
            });
            yField.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                }

                public void focusLost(FocusEvent e) {
                    try {
                        if (Integer.parseInt(yField.getText()) < 600) {
                            yField.setText("600");
                        }
                    } catch (NumberFormatException NFExcep) {
                        yField.setText("600");
                    }
                }
            });
            ircDefNickField.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                }

                public void focusLost(FocusEvent e) {
                    if (ircDefNickField.getText().trim().length() != 0) {
                        ircDefNick = ircDefNickField.getText().trim();
                    }
                    if (ircAltNickField.getText().trim().length() == 0) {
                        ircAltNick = ircDefNickField.getText().trim() + "|C";
                    }
                }
            });
            ircAltNickField.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                }

                public void focusLost(FocusEvent e) {
                    if (ircAltNickField.getText().trim().length() != 0) {
                        if (ircDefNickField.getText().trim().length() == 0) {
                            ircDefNickField.setText(ircAltNickField.getText().trim());
                            ircDefNick = ircDefNickField.getText();
                            ircAltNickField.setText(ircDefNick + "|C");
                            return;
                        }
                        ircAltNick = ircAltNickField.getText().trim();
                        return;
                    }
                    if (ircDefNickField.getText().trim().length() != 0) {
                        ircAltNick = ircDefNickField.getText().trim() + "|C";
                    }
                }
            });
            startBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    windowSize.x = stdRes.isEnabled() ? ((Coord) stdRes.getSelectedItem()).x
                            : Integer.parseInt(xField.getText());
                    windowSize.y = stdRes.isEnabled() ? ((Coord) stdRes.getSelectedItem()).y
                            : Integer.parseInt(yField.getText());
                    setWindowSize(windowSize);
                    saveSettings();
                    Thread mainThread = new Thread() {
                        public void run() {
                            MainFrame.main(args);
                        }
                    };
                    mainThread.start();
                    configFrame.dispose();
                }
            });
            configFrame.pack();
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            configFrame.setLocation((int) (toolkit.getScreenSize().getWidth() - configFrame.getWidth()) / 2,
                    (int) (toolkit.getScreenSize().getHeight() - configFrame.getHeight()) / 2);
            configFrame.setVisible(true);
        } else {
            saveSettings();
            MainFrame.main(args);
        }
    }
}
