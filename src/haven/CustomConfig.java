/**
 * Custom Configuration.
 * For extensions.
 * @author Vlad.Rassokhin@gmail.com
 * @version 2.00
 */

package haven;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomConfig {

    private static final AtomicInteger customWidgetIdGenerator = new AtomicInteger(-10); // for Userspace widgets
    private boolean sshot_compress;
    private boolean sshot_noui;
    private boolean sshot_nonames;
    private boolean fastFlowerAnim;
    private boolean newclaim;
    private boolean showq;
    private boolean grid;
    private boolean addChatTimestamp;
    private boolean new_chat;
    private boolean highlight;
    private boolean use_smileys;
    private boolean zoom;
    private boolean noborders;
    private boolean new_minimap = true;
    private boolean simple_plants;

    private final Set<String> hidingObjects = new HashSet<String>();

    // View
    private boolean showRadius;
    private boolean showHidden;
    private boolean showBeast;
    private boolean showDirection;
    private boolean showNames;
    private boolean showOtherNames;

    private String GoogleTranslateApiKey;

    public static int getNextCustomWidgetId() {
        return customWidgetIdGenerator.decrementAndGet();
    }

    // Config
    private boolean isSaveable;

    // View
    private boolean xray;
    private boolean hideObjects;
    private boolean highlightObjectAtMouse;
    private boolean render = true;
    private boolean hasNightVision;

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
    private boolean logLoad;
    private boolean logSave;
    private boolean logIRC;
    private boolean logServerMessages;

    // Other config
    private final Map<String, String> windowProperties = new HashMap<String, String>();


    Coord windowSize;
    Coord windowCenter;

    public transient Glob glob = null;

    public transient int playerId;


    // Belts
    private transient CharData activeCharacter;
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

    public static boolean isNightVision() {
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
        return ourConfig.activeCharacter;
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
        setGrid(!isGrid());
    }

    public static void toggleConsole() {
        if (UI.console == null) {
            UI.console = new CustomConsole(Coord.z, new Coord(CustomConfig.getWindowWidth() - 30, 220), UI.instance.root, "Console");
        } else {
            UI.console.toggle();
            UI.console.raise();
        }
    }

    public static int getMusicVolume() {
        return isMusicOn() ? getMusicVol() : 0;
    }

    public static Map<String, String> getWindowProperties() {
        return ourConfig.windowProperties;
    }

    @Nullable
    public static String getWindowProperty(@NotNull final String key, @Nullable final String defValue) {
        if (ourConfig.windowProperties.containsKey(key)) {
            return ourConfig.windowProperties.get(key);
        } else return defValue;
    }

    @Nullable
    public static String getWindowProperty(@NotNull final String key) {
        return ourConfig.windowProperties.get(key);
    }

    public static <T> void setWindowOpt(@NotNull final String key, @Nullable final T value) {
        synchronized (getWindowProperties()) {
            final String prev_val = getWindowProperties().get(key);
            if ((prev_val != null) && prev_val.equals(value))
                return;
            getWindowProperties().put(key, String.valueOf(value));
        }
        save();
    }

    public static boolean save() {
        return CustomConfigProcessor.saveConfig(ourConfig);
    }

    public static boolean load() {
        final CustomConfig config = CustomConfigProcessor.loadConfig();
        if (config == null) {
            return false;
        }
        setConfig(config);
        return true;
    }

    public static boolean isShowRadius() {
        return ourConfig.showRadius;
    }

    public static void setShowRadius(final boolean showRadius) {
        ourConfig.showRadius = showRadius;
    }

    public static boolean isShowHidden() {
        return ourConfig.showHidden;
    }

    public static void setShowHidden(final boolean showHidden) {
        ourConfig.showHidden = showHidden;
    }

    public static boolean isShowBeast() {
        return ourConfig.showBeast;
    }

    public static void setShowBeast(final boolean showBeast) {
        ourConfig.showBeast = showBeast;
    }

    public static boolean isShowDirection() {
        return ourConfig.showDirection;
    }

    public static void setShowDirection(final boolean showDirection) {
        ourConfig.showDirection = showDirection;
    }

    public static boolean isShowNames() {
        return ourConfig.showNames;
    }

    public static void setShowNames(final boolean showNames) {
        ourConfig.showNames = showNames;
    }

    public static boolean isShowOtherNames() {
        return ourConfig.showOtherNames;
    }

    public static void setShowOtherNames(final boolean showOtherNames) {
        ourConfig.showOtherNames = showOtherNames;
    }

    public static boolean isSshot_compress() {
        return ourConfig.sshot_compress;
    }

    public static void setSshot_compress(final boolean sshot_compress) {
        ourConfig.sshot_compress = sshot_compress;
    }

    public static boolean isSshot_noui() {
        return ourConfig.sshot_noui;
    }

    public static void setSshot_noui(final boolean sshot_noui) {
        ourConfig.sshot_noui = sshot_noui;
    }

    public static boolean isSshot_nonames() {
        return ourConfig.sshot_nonames;
    }

    public static void setSshot_nonames(final boolean sshot_nonames) {
        ourConfig.sshot_nonames = sshot_nonames;
    }

    public static boolean isFastFlowerAnim() {
        return ourConfig.fastFlowerAnim;
    }

    public static void setFastFlowerAnim(final boolean fastFlowerAnim) {
        ourConfig.fastFlowerAnim = fastFlowerAnim;
    }

    public static boolean isNewclaim() {
        return ourConfig.newclaim;
    }

    public static void setNewclaim(final boolean newclaim) {
        ourConfig.newclaim = newclaim;
    }

    public static boolean isShowq() {
        return ourConfig.showq;
    }

    public static void setShowq(final boolean showq) {
        ourConfig.showq = showq;
    }

    public static boolean isGrid() {
        return ourConfig.grid;
    }

    public static void setGrid(final boolean grid) {
        ourConfig.grid = grid;
    }

    public static boolean isAddChatTimestamp() {
        return ourConfig.addChatTimestamp;
    }

    public static void setAddChatTimestamp(final boolean timestamp) {
        ourConfig.addChatTimestamp = timestamp;
    }

    public static boolean isNew_chat() {
        return ourConfig.new_chat;
    }

    public static void setNew_chat(final boolean new_chat) {
        ourConfig.new_chat = new_chat;
    }

    public static boolean isHighlight() {
        return ourConfig.highlight;
    }

    public static void setHighlight(final boolean highlight) {
        ourConfig.highlight = highlight;
    }

    public static boolean isUse_smileys() {
        return ourConfig.use_smileys;
    }

    public static void setUse_smileys(final boolean use_smileys) {
        ourConfig.use_smileys = use_smileys;
    }

    public static boolean isZoom() {
        return ourConfig.zoom;
    }

    public static void setZoom(final boolean zoom) {
        ourConfig.zoom = zoom;
    }

    public static boolean isNoborders() {
        return ourConfig.noborders;
    }

    public static void setNoborders(final boolean noborders) {
        ourConfig.noborders = noborders;
    }

    public static boolean isNew_minimap() {
        return ourConfig.new_minimap;
    }

    public static void setNew_minimap(final boolean new_minimap) {
        ourConfig.new_minimap = new_minimap;
    }

    public static boolean isSimple_plants() {
        return ourConfig.simple_plants;
    }

    public static void setSimple_plants(final boolean simple_plants) {
        ourConfig.simple_plants = simple_plants;
    }

    public static Set<String> getHidingObjects() {
        return ourConfig.hidingObjects;
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
                ourConfig.activeCharacter = cData;
                setSaveable(true);
                setNoChars(false);
                return;
            }
        }
        ourConfig.activeCharacter = new CharData(name);
        ourConfig.characterList.add(ourConfig.activeCharacter);
        setSaveable(true);
        setNoChars(false);
    }


    public static String getGoogleTranslateApiKey() {
        return ourConfig.GoogleTranslateApiKey;
    }

    public static void setGoogleTranslateApiKey(String googleTranslateApiKey) {
        ourConfig.GoogleTranslateApiKey = googleTranslateApiKey;
    }
}
