/**
 * Custom Configuration.
 * For extensions.
 * @author Vlad.Rassokhin@gmail.com
 * @version 2.00
 */

package haven;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomConfig {

    private static final AtomicInteger customWidgetIdGenerator = new AtomicInteger(-10); // for Userspace widgets

    public static int getNextCustomWidgetId() {
        return customWidgetIdGenerator.decrementAndGet();
    }

    // Config
    private boolean isSaveable = false;

    // View
    private boolean xray = false;
    private boolean hideObjects = false;
    private boolean highlightObjectAtMouse = false;
    private boolean render = true;
    private boolean hasNightVision = false;

    // IRC
    private String ircServerAddress = "irc.synirc.net";
    private List<Listbox.Option> ircChannelList = new ArrayList<Listbox.Option>();
    private String ircDefNick = "";
    private String ircAltNick = "";
    private boolean isIRCOn = true;


    // Sound system
    private int sfxVol = 100;
    private int musicVol = 100;
    private boolean isMusicOn = true;
    private boolean isSoundOn = true;

    // Logging
    private boolean logLoad = false;
    private boolean logSave = false;
    private boolean logIRC = false;
    private boolean logServerMessages = false;


    Coord windowSize;
    Coord windowCenter;

    public transient Glob glob = null;

    public transient int playerId;


    // Belts
    private static CharData activeCharacter;
    private List<CharData> characterList = new ArrayList<CharData>();
    private boolean noChars = true;

    static private CustomConfig ourConfig = new CustomConfig();

    {
        windowSize = new Coord(800, 600);
        windowCenter = windowSize.div(2);
        ircChannelList.add(new Listbox.Option("#Haven", ""));
    }

    public static Glob getGlob() {
        return ourConfig.glob;
    }

    public static void setGlob(final Glob glob) {
        ourConfig.glob = glob;
    }

    public static int getPlayerId() {
        return ourConfig.playerId;
    }

    public static void setPlayerId(final int playerId) {
        ourConfig.playerId = playerId;
    }


    public static void setConfig(final CustomConfig config) {
        CustomConfig.ourConfig = config;
    }

    public static CustomConfig getConfig() {
        return ourConfig;
    }


    public static void toggleXray() {
        ourConfig.xray = !ourConfig.xray;
    }

    public static boolean isXray() {
        return ourConfig.xray;
    }

    public static void setXray(final boolean xray) {
        ourConfig.xray = xray;
    }

    public static boolean isHideObjects() {
        return ourConfig.hideObjects;
    }

    public static void setHideObjects(final boolean hide) {
        ourConfig.hideObjects = hide;
    }

    public static boolean isHighlightObjectAtMouse() {
        return ourConfig.highlightObjectAtMouse;
    }

    public static void setHighlightObjectAtMouse(final boolean highlightObjectAtMouse) {
        ourConfig.highlightObjectAtMouse = highlightObjectAtMouse;
    }

    public static boolean isRender() {
        return ourConfig.render;
    }

    public static void setRender(final boolean render) {
        ourConfig.render = render;
    }

    public static int getSfxVol() {
        return ourConfig.sfxVol;
    }

    public static void setSfxVol(final int sfxVol) {
        ourConfig.sfxVol = sfxVol;
    }

    public static int getMusicVol() {
        return ourConfig.musicVol;
    }

    public static void setMusicVol(final int musicVol) {
        ourConfig.musicVol = musicVol;
    }

    public static String getIrcServerAddress() {
        return ourConfig.ircServerAddress;
    }

    public static void setIrcServerAddress(final String ircServerAddress) {
        ourConfig.ircServerAddress = ircServerAddress;
    }

    public static List<Listbox.Option> getIrcChannelList() {
        return ourConfig.ircChannelList;
    }

    public static void setIrcChannelList(final List<Listbox.Option> ircChannelList) {
        ourConfig.ircChannelList = ircChannelList;
    }

    public static List<CharData> getCharacterList() {
        return ourConfig.characterList;
    }

    public static void setCharacterList(final List<CharData> characterList) {
        ourConfig.characterList = characterList;
    }

    public static String getIrcDefNick() {
        return ourConfig.ircDefNick;
    }

    public static void setIrcDefNick(final String ircDefNick) {
        ourConfig.ircDefNick = ircDefNick;
    }

    public static String getIrcAltNick() {
        return ourConfig.ircAltNick;
    }

    public static void setIrcAltNick(final String ircAltNick) {
        ourConfig.ircAltNick = ircAltNick;
    }

    public static boolean isMusicOn() {
        return ourConfig.isMusicOn;
    }

    public static void setMusicOn(final boolean musicOn) {
        ourConfig.isMusicOn = musicOn;
    }

    public static boolean isSoundOn() {
        return ourConfig.isSoundOn;
    }

    public static void setSoundOn(final boolean soundOn) {
        ourConfig.isSoundOn = soundOn;
    }

    public static boolean isIRCOn() {
        return ourConfig.isIRCOn;
    }

    public static void setIRCOn(final boolean IRCOn) {
        ourConfig.isIRCOn = IRCOn;
    }

    public static boolean isHasNightVision() {
        return ourConfig.hasNightVision;
    }

    public static void setHasNightVision(final boolean hasNightVision) {
        ourConfig.hasNightVision = hasNightVision;
    }

    public static boolean isSaveable() {
        return ourConfig.isSaveable;
    }

    public static void setSaveable(final boolean saveable) {
        ourConfig.isSaveable = saveable;
    }

    public static boolean isNoChars() {
        return ourConfig.noChars;
    }

    public static void setNoChars(final boolean noChars) {
        ourConfig.noChars = noChars;
    }

    public static boolean isLogLoad() {
        return ourConfig.logLoad;
    }

    public static void setLogLoad(final boolean logLoad) {
        ourConfig.logLoad = logLoad;
    }

    public static boolean isLogSave() {
        return ourConfig.logSave;
    }

    public static void setLogSave(final boolean logSave) {
        ourConfig.logSave = logSave;
    }

    public static boolean isLogIRC() {
        return ourConfig.logIRC;
    }

    public static void setLogIRC(final boolean logIRC) {
        ourConfig.logIRC = logIRC;
    }

    public static boolean isLogServerMessages() {
        return ourConfig.logServerMessages;
    }

    public static void setLogServerMessages(final boolean logServerMessages) {
        ourConfig.logServerMessages = logServerMessages;
    }

    public static Coord getWindowSize() {
        return ourConfig.windowSize;
    }

    public static Coord getWindowCenter() {
        return ourConfig.windowCenter;
    }

    public static void setWindowCenter(final Coord windowCenter) {
        ourConfig.windowCenter = windowCenter;
    }

    public static int getWindowWidth() {
        return getWindowSize().x;
    }

    public static int getWindowHeight() {
        return getWindowSize().y;
    }

    public static int getCenterX() {
        return getWindowCenter().x;
    }

    public static int getCenterY() {
        return getWindowCenter().y;
    }

    public static CharData getActiveCharacter() {
        return activeCharacter;
    }

    public static void toggleNightvision() {
        ourConfig.hasNightVision = !ourConfig.hasNightVision;
    }

    public static void toggleHideObjects() {
        ourConfig.hideObjects = !ourConfig.hideObjects;
    }

    public static void toggleRender() {
        ourConfig.render = !ourConfig.render;
    }

    public static void toggleMapGrid() {
        Config.grid = !Config.grid;
    }

    public static void toggleConsole() {
        if (UI.console == null) {
            UI.console = new CustomConsole(Coord.z, new Coord(CustomConfig.getWindowWidth() - 30, 220), UI.instance.root, "Console");
        } else {
            UI.console.toggle();
            UI.console.raise();
        }
    }

    static class CharData {
        String name;
        int hudActiveBelt = 1;
        String[][] hudBelt = new String[SlenHud._BELTSIZE][SlenHud._BELTSIZE];

        CharData(final String name) {
            this.name = name;
        }

        public String toString() {
            return "Name=\"" + name + '\"';
        }
    }


    public static void setWindowSize(final Coord size) {
        ourConfig.windowSize = size;
        MainFrame.setWindowSize(size.toDimension());
    }

    public static void setWindowSize(final int width, final int height) {
        ourConfig.windowSize = new Coord(width, height);
        MainFrame.setWindowSize(new Dimension(width, height));
    }

    public static void updateWindowSize(final int width, final int height) {
        ourConfig.windowSize = new Coord(width, height);
    }

    public static void setActiveCharacter(final String name) {
        for (final CharData cData : ourConfig.characterList) {
            if (cData.name.equalsIgnoreCase(name)) {
                activeCharacter = cData;
                setSaveable(true);
                setNoChars(false);
                return;
            }
        }
        activeCharacter = new CharData(name);
        ourConfig.characterList.add(activeCharacter);
        setSaveable(true);
        setNoChars(false);
    }

}
