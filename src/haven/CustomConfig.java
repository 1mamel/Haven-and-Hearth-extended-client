/**
 * @(#)CustomConfig.java
 *
 *
 * @author
 * @version 1.00 2009/10/19
 */

package haven;

import org.apache.log4j.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"UnusedDeclaration"})
public class CustomConfig {
    static UI ui;
    private static boolean xray = false;
    private static boolean hide = false;

    public static Logger logger;

    static {
        logger = Logger.getLogger("Main log");
        try {
            logger.addAppender(new FileAppender(new SimpleLayout(), "main.log"));
        } catch (IOException e) {
            logger.addAppender(new ConsoleAppender(new SimpleLayout()));
        }
        logger.setLevel(Level.DEBUG);
    }

    public static boolean isDebugLogging() {
        return logger.getLevel().isGreaterOrEqual(Level.DEBUG);
    }

    public static void setDebugLogging(boolean debug) {
        logger.setLevel(debug ? Level.DEBUG : Level.INFO);
    }

    public static boolean isXray() {
        return xray;
    }

    public static void setXray(boolean xray) {
        CustomConfig.xray = xray;
    }

    public static boolean isHideObjects() {
        return hide;
    }

    public static void setHideObjects(boolean hide) {
        CustomConfig.hide = hide;
    }

    public static void toggleXray() {
        xray = !xray;
    }

    public static void toggleHideObjects() {
        hide = !hide;
    }

    public static Coord getWindowSize() {
        return windowSize;
    }

    public static int getWindowWidth() {
        return windowSize.getX();
    }

    public static int getWindowHeight() {
        return windowSize.getY();
    }

    public static Coord getWindowCenter() {
        return windowCenter;
    }

    public static int getCenterX() {
        return windowCenter.getX();
    }

    public static int getCenterY() {
        return windowCenter.getY();
    }

    public static void setWindowSize(Coord size) {
        windowSize = size.getUnmodifiableVersion();
        MainFrame.setWindowSize(size.toDimension());
    }

    public static void setWindowSize(int width, int height) {
        windowSize = new Coord(width, height).getUnmodifiableVersion();
        MainFrame.setWindowSize(new Dimension(width, height));
    }

    public static void updateWindowSize(int width, int height) {
        windowSize = new Coord(width, height).getUnmodifiableVersion();
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

    public static Coord invCoord = Coord.z;
    public static int sfxVol = 100;
    public static int musicVol = 100;
    public static String ircServerAddress = "irc.synirc.net";
    public static List<Listbox.Option> ircChannelList = new ArrayList<Listbox.Option>();
    public static List<CharData> characterList = new ArrayList<CharData>();
    public static String ircDefNick = "";
    public static String ircAltNick = "";
    public static CharData activeCharacter;
    private static AtomicInteger wdgtID = new AtomicInteger(-10); // for Userspace widgets
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

    public static double getSFXVolume() {
        return (double) sfxVol / 100;
    }

    public static int getNextCustomWidgetId() {
        return wdgtID.decrementAndGet();
    }

    private static Coord windowSize = new Coord(800, 600);
    private static Coord windowCenter = windowSize.div(2);
}
