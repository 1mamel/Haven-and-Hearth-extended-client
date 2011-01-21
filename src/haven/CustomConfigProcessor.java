package haven;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vlad.Rassokhin@gmail.com
 * Date: 12.01.11
 * Time: 14:26
 */
public class CustomConfigProcessor {
    public static void setDefaultSettings() {
        CustomConfig.setWindowSize(800, 600);

        CustomConfig.sfxVol = 100;
        CustomConfig.musicVol = 100;
        CustomConfig.ircServerAddress = "irc.synirc.net";
        CustomConfig.ircChannelList.clear();
        CustomConfig.ircChannelList.add(new Listbox.Option("#Haven", ""));
        CustomConfig.ircDefNick = "";
        CustomConfig.ircAltNick = "";
        CustomConfig.isMusicOn = true;
        CustomConfig.isSoundOn = true;
        CustomConfig.hasNightVision = false;
        CustomConfig.debugMsgs = false;
        CustomConfig.xray = false;
        CustomConfig.hide = false;
    }

    public static boolean loadSettings() {
        setDefaultSettings();
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
                    if (CustomConfig.logLoad) {
                        CustomConsole.log.append("|| ").append(key).append(" \t ");
                        for (int i = 0; i < atts.getLength(); i++) {
                            CustomConsole.log.append(" \t ").append(atts.getQName(i)).append(" \t ").append(atts.getValue(i));
                        }
                        if (CustomConfig.console != null) {
                            CustomConsole.out.append(CustomConsole.log.toString());
                            CustomConsole.log = new StringBuilder();
                        } else {
                            CustomConsole.log.append('\n');
                        }
                    }

                    if (key.equals("SCREENSIZE")) {
                        value = atts.getValue("width") == null ? "1024" : atts.getValue("width");
                        CustomConfig.windowSize.x = Integer.parseInt(value);

                        value = atts.getValue("height") == null ? "1024" : atts.getValue("height");
                        CustomConfig.windowSize.y = Integer.parseInt(value);
                        CustomConfig.setWindowSize(CustomConfig.windowSize);
                    } else if (key.equals("SOUND")) {
                        value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
                        CustomConfig.isSoundOn = Boolean.parseBoolean(value);

                        value = atts.getValue("volume") == null ? "100" : atts.getValue("volume");
                        CustomConfig.sfxVol = Integer.parseInt(value);
                    } else if (key.equals("MUSIC")) {
                        value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
                        CustomConfig.isMusicOn = Boolean.parseBoolean(value);

                        value = atts.getValue("volume") == null ? "100" : atts.getValue("volume");
                        CustomConfig.musicVol = Integer.parseInt(value);
                    } else if (key.equals("IRC") && !(beltElementActive || ircElementActive)) {
                        ircElementActive = true;
                        CustomConfig.ircChannelList.clear();
                        value = atts.getValue("enabled") == null ? "true" : atts.getValue("enabled");
                        CustomConfig.isIRCOn = Boolean.parseBoolean(value);

                        value = atts.getValue("server") == null ? "irc.synirc.net" : atts.getValue("server");
                        CustomConfig.ircServerAddress = value;

                        value = atts.getValue("default-nick") == null ? "" : atts.getValue("default-nick");
                        CustomConfig.ircDefNick = value;

                        value = atts.getValue("alternate-nick") == null ? "" : atts.getValue("alternate-nick");
                        CustomConfig.ircAltNick = value;
                    } else if (key.equals("CHANNEL") && atts.getValue("name") != null
                            && ircElementActive) {
                        value = atts.getValue("password") == null ? "" : atts.getValue("password");
                        Listbox.Option chan = new Listbox.Option(atts.getValue("name"), value.trim());
                        CustomConfig.ircChannelList.add(chan);
                    } else if (key.equals("BELT-LIST") && !(beltElementActive || ircElementActive
                            || beltListElementActive)
                            && atts.getValue("name") != null) {
                        beltListElementActive = true;
                        CustomConfig.activeCharacter = new CustomConfig.CharData(atts.getValue("name"));
                        CustomConfig.activeCharacter.hudActiveBelt = Integer.parseInt(atts.getValue("active-belt"));
                        CustomConfig.noChars = false;
                    } else if (key.equals("BELT") && !(beltElementActive || ircElementActive)
                            && beltListElementActive) {
                        beltElementActive = true;
                        activeBelt = Integer.parseInt(atts.getValue("value"));
                    } else if (key.equals("SLOT") && atts.getValue("value") != null && atts.getValue("position") != null
                            && beltElementActive
                            && beltListElementActive) {
                        CustomConfig.activeCharacter.hudBelt[activeBelt][Integer.parseInt(atts.getValue("position"))]
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
                        CustomConfig.characterList.add(CustomConfig.activeCharacter);
                    }
                }
            });
            if (ResCache.global != null) {
                xmlReader.parse(new InputSource(ResCache.global.fetch("config.xml")));
            } else {
                xmlReader.parse("config.xml");
            }
            if (CustomConfig.windowSize.x < 800 || CustomConfig.windowSize.y < 600) {
                System.out.println("Window size must be at least 800x600");
                CustomConfig.setWindowSize(800, 600);
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
            writer.write("\t<SCREENSIZE width=\"" + CustomConfig.windowSize.x + "\" height=\"" + CustomConfig.windowSize.y + "\"/>\n");
            writer.write("\t<SOUND enabled=\"" + Boolean.toString(CustomConfig.isSoundOn)
                    + "\" volume=\"" + Integer.toString(CustomConfig.sfxVol) + "\"/>\n");
            writer.write("\t<MUSIC enabled=\"" + Boolean.toString(CustomConfig.isMusicOn)
                    + "\" volume=\"" + Integer.toString(CustomConfig.musicVol) + "\"/>\n");
            writer.write("\t<IRC enabled=\"" + Boolean.toString(CustomConfig.isIRCOn)
                    + "\" server=\"" + CustomConfig.ircServerAddress
                    + "\" default-nick=\"" + CustomConfig.ircDefNick
                    + "\" alternate-nick=\"" + CustomConfig.ircAltNick + "\">\n");
            for (Listbox.Option channel : CustomConfig.ircChannelList) {
                writer.write("\t\t<CHANNEL name=\"" + channel.name + "\" password=\"" + channel.disp + "\"/>\n");
            }
            writer.write("\t</IRC>\n");

            for (CustomConfig.CharData cData : CustomConfig.characterList) {
                if (CustomConfig.noChars) break;
                if (cData.name.equals(CustomConfig.activeCharacter.name))
                    cData.hudActiveBelt = CustomConfig.activeCharacter.hudActiveBelt;
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

}
