/**
 * Custom Configuration.
 * For extensions.
 * @author Vlad.Rassokhin@gmail.com
 * @version 2.00
 */

package haven;

import com.memetix.mst.language.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import translation.MicrosoftTranslatorProvider;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomConfig {

    private static final AtomicInteger customWidgetIdGenerator = new AtomicInteger(-10); // for Userspace widgets
    public static final String[][] CHECKBOXES_LIST = {{"Walls", "gfx/arch/walls"},
            {"Gates", "gfx/arch/gates"},
            {"Wooden Houses", "gfx/arch/cabin"},
            {"Stone Mansions", "gfx/arch/inn"},
            {"Plants", "gfx/terobjs/plants"},
            {"Trees", "gfx/terobjs/trees"},
            {"Stones", "gfx/terobjs/bumlings"},
            {"Flavor objects", "flavobjs"},
            {"Bushes", "gfx/tiles/wald"},
            {"Thicket", "gfx/tiles/dwald"}};
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

    private final MicrosoftTranslatorProvider translator = new MicrosoftTranslatorProvider();

    public int getNextCustomWidgetId() {
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

    public static CustomConfig current() {
        return getConfig();
    }

    public Glob getGlob() {
        return this.glob;
    }

    public void setGlob(final Glob glob) {
        this.glob = glob;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }


    public static void setConfig(final CustomConfig config) {
        ourConfig = config;
    }

    public static CustomConfig getConfig() {
        return ourConfig;
    }


    public void toggleXray() {
        this.xray = !this.xray;
    }

    public boolean isXray() {
        return this.xray;
    }

    public void setXray(final boolean xray) {
        this.xray = xray;
    }

    public boolean isHideObjects() {
        return this.hideObjects;
    }

    public void setHideObjects(final boolean hide) {
        this.hideObjects = hide;
    }

    public boolean isHighlightObjectAtMouse() {
        return this.highlightObjectAtMouse;
    }

    public void setHighlightObjectAtMouse(final boolean highlightObjectAtMouse) {
        this.highlightObjectAtMouse = highlightObjectAtMouse;
    }

    public boolean isRender() {
        return this.render;
    }

    public void setRender(final boolean render) {
        this.render = render;
    }

    public int getSfxVol() {
        return this.sfxVol;
    }

    public void setSfxVol(final int sfxVol) {
        this.sfxVol = sfxVol;
    }

    public int getMusicVol() {
        return this.musicVol;
    }

    public void setMusicVol(final int musicVol) {
        this.musicVol = musicVol;
    }

    public String getIrcServerAddress() {
        return this.ircServerAddress;
    }

    public void setIrcServerAddress(final String ircServerAddress) {
        this.ircServerAddress = ircServerAddress;
    }

    public List<Listbox.Option> getIrcChannelList() {
        return this.ircChannelList;
    }

    public void setIrcChannelList(final List<Listbox.Option> ircChannelList) {
        this.ircChannelList = ircChannelList;
    }

    public List<CharData> getCharacterList() {
        return this.characterList;
    }

    public void setCharacterList(final List<CharData> characterList) {
        this.characterList = characterList;
    }

    public String getIrcDefNick() {
        return this.ircDefNick;
    }

    public void setIrcDefNick(final String ircDefNick) {
        this.ircDefNick = ircDefNick;
    }

    public String getIrcAltNick() {
        return this.ircAltNick;
    }

    public void setIrcAltNick(final String ircAltNick) {
        this.ircAltNick = ircAltNick;
    }

    public boolean isMusicOn() {
        return this.isMusicOn;
    }

    public void setMusicOn(final boolean musicOn) {
        this.isMusicOn = musicOn;
    }

    public boolean isSoundOn() {
        return this.isSoundOn;
    }

    public void setSoundOn(final boolean soundOn) {
        this.isSoundOn = soundOn;
    }

    public boolean isIRCOn() {
        return this.isIRCOn;
    }

    public void setIRCOn(final boolean IRCOn) {
        this.isIRCOn = IRCOn;
    }

    public boolean isNightVision() {
        return this.hasNightVision;
    }

    public void setHasNightVision(final boolean hasNightVision) {
        this.hasNightVision = hasNightVision;
    }

    public boolean isSaveable() {
        return this.isSaveable;
    }

    public void setSaveable(final boolean saveable) {
        this.isSaveable = saveable;
    }

    public boolean isNoChars() {
        return this.noChars;
    }

    public void setNoChars(final boolean noChars) {
        this.noChars = noChars;
    }

    public boolean isLogLoad() {
        return this.logLoad;
    }

    public void setLogLoad(final boolean logLoad) {
        this.logLoad = logLoad;
    }

    public boolean isLogSave() {
        return this.logSave;
    }

    public void setLogSave(final boolean logSave) {
        this.logSave = logSave;
    }

    public boolean isLogIRC() {
        return this.logIRC;
    }

    public void setLogIRC(final boolean logIRC) {
        this.logIRC = logIRC;
    }

    public boolean isLogServerMessages() {
        return this.logServerMessages;
    }

    public void setLogServerMessages(final boolean logServerMessages) {
        this.logServerMessages = logServerMessages;
    }

    public Coord getWindowSize() {
        return this.windowSize;
    }

    public Coord getWindowCenter() {
        return this.windowCenter;
    }

    public void setWindowCenter(final Coord windowCenter) {
        this.windowCenter = windowCenter;
    }

    public int getWindowWidth() {
        return getWindowSize().x;
    }

    public int getWindowHeight() {
        return getWindowSize().y;
    }

    public int getCenterX() {
        return getWindowCenter().x;
    }

    public int getCenterY() {
        return getWindowCenter().y;
    }

    public CharData getActiveCharacter() {
        return this.activeCharacter;
    }

    public void toggleNightvision() {
        this.hasNightVision = !this.hasNightVision;
    }

    public void toggleHideObjects() {
        this.hideObjects = !this.hideObjects;
    }

    public void toggleRender() {
        this.render = !this.render;
    }

    public void toggleMapGrid() {
        setGrid(!isGrid());
    }

    public static void toggleConsole() {
        if (UI.console == null) {
            UI.console = new CustomConsole(Coord.z, new Coord(CustomConfig.current().getWindowWidth() - 30, 220), UI.instance.root, "Console");
        } else {
            UI.console.toggle();
            UI.console.raise();
        }
    }

    public int getMusicVolume() {
        return isMusicOn() ? getMusicVol() : 0;
    }

    public Map<String, String> getWindowProperties() {
        return this.windowProperties;
    }

    @Nullable
    public String getWindowProperty(@NotNull final String key, @Nullable final String defValue) {
        if (this.windowProperties.containsKey(key)) {
            return this.windowProperties.get(key);
        } else return defValue;
    }

    @Nullable
    public String getWindowProperty(@NotNull final String key) {
        return this.windowProperties.get(key);
    }

    public <T> void setWindowOpt(@NotNull final String key, @Nullable final T value) {
        synchronized (getWindowProperties()) {
            final String prev_val = getWindowProperties().get(key);
            if ((prev_val != null) && prev_val.equals(value))
                return;
            getWindowProperties().put(key, String.valueOf(value));
        }
        save();
    }

    public static boolean save() {
        return CustomConfigProcessor.saveConfig(current());
    }

    public static boolean load() {
        final CustomConfig config = CustomConfigProcessor.loadConfig();
        if (config == null) {
            return false;
        }
        setConfig(config);
        return true;
    }

    public boolean isShowRadius() {
        return this.showRadius;
    }

    public void setShowRadius(final boolean showRadius) {
        this.showRadius = showRadius;
    }

    public boolean isShowHidden() {
        return this.showHidden;
    }

    public void setShowHidden(final boolean showHidden) {
        this.showHidden = showHidden;
    }

    public boolean isShowBeast() {
        return this.showBeast;
    }

    public void setShowBeast(final boolean showBeast) {
        this.showBeast = showBeast;
    }

    public boolean isShowDirection() {
        return this.showDirection;
    }

    public void setShowDirection(final boolean showDirection) {
        this.showDirection = showDirection;
    }

    public boolean isShowNames() {
        return this.showNames;
    }

    public void setShowNames(final boolean showNames) {
        this.showNames = showNames;
    }

    public boolean isShowOtherNames() {
        return this.showOtherNames;
    }

    public void setShowOtherNames(final boolean showOtherNames) {
        this.showOtherNames = showOtherNames;
    }

    public boolean isScreenShotsCompressing() {
        return this.sshot_compress;
    }

    public void setSshot_compress(final boolean sshot_compress) {
        this.sshot_compress = sshot_compress;
    }

    public boolean isScreenShotExcludeUI() {
        return this.sshot_noui;
    }

    public void setSshot_noui(final boolean sshot_noui) {
        this.sshot_noui = sshot_noui;
    }

    public boolean isScreenShotsExcludeNames() {
        return this.sshot_nonames;
    }

    public void setSshot_nonames(final boolean sshot_nonames) {
        this.sshot_nonames = sshot_nonames;
    }

    public boolean isFastFlowerAnim() {
        return this.fastFlowerAnim;
    }

    public void setFastFlowerAnim(final boolean fastFlowerAnim) {
        this.fastFlowerAnim = fastFlowerAnim;
    }

    public boolean isNewclaim() {
        return this.newclaim;
    }

    public void setNewclaim(final boolean newclaim) {
        this.newclaim = newclaim;
    }

    public boolean isShowq() {
        return this.showq;
    }

    public void setShowq(final boolean showq) {
        this.showq = showq;
    }

    public boolean isGrid() {
        return this.grid;
    }

    public void setGrid(final boolean grid) {
        this.grid = grid;
    }

    public boolean isAddChatTimestamp() {
        return this.addChatTimestamp;
    }

    public void setAddChatTimestamp(final boolean timestamp) {
        this.addChatTimestamp = timestamp;
    }

    public boolean isNew_chat() {
        return this.new_chat;
    }

    public void setNew_chat(final boolean new_chat) {
        this.new_chat = new_chat;
    }

    public boolean isHighlight() {
        return this.highlight;
    }

    public void setHighlight(final boolean highlight) {
        this.highlight = highlight;
    }

    public boolean isUse_smileys() {
        return this.use_smileys;
    }

    public void setUse_smileys(final boolean use_smileys) {
        this.use_smileys = use_smileys;
    }

    public boolean isZoom() {
        return this.zoom;
    }

    public void setZoom(final boolean zoom) {
        this.zoom = zoom;
    }

    public boolean isNoborders() {
        return this.noborders;
    }

    public void setNoborders(final boolean noborders) {
        this.noborders = noborders;
    }

    public boolean isNew_minimap() {
        return this.new_minimap;
    }

    public void setNew_minimap(final boolean new_minimap) {
        this.new_minimap = new_minimap;
    }

    public boolean isSimple_plants() {
        return this.simple_plants;
    }

    public void setSimple_plants(final boolean simple_plants) {
        this.simple_plants = simple_plants;
    }

    public Set<String> getHidingObjects() {
        return this.hidingObjects;
    }

    public void setHidingObjects(@NotNull final Set<String> hidingObjects) {
        synchronized (this.hidingObjects) {
            this.hidingObjects.clear();
            this.hidingObjects.addAll(hidingObjects);
        }
    }

    public void setTranslatorLanguage(@NotNull final Language language) {
        this.translator.useLanguage(language);
    }

    public Language getTranslatorLanguage() {
        return this.translator.getLanguage();
    }

    public MicrosoftTranslatorProvider getTranslator() {
        return this.translator;
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


    public void setWindowSize(final Coord size) {
        this.windowSize = size;
        MainFrame.setWindowSize(size.toDimension());
    }

    public void setWindowSize(final int width, final int height) {
        this.windowSize = new Coord(width, height);
        MainFrame.setWindowSize(new Dimension(width, height));
    }

    public void updateWindowSize(final int width, final int height) {
        this.windowSize = new Coord(width, height);
    }

    public void setActiveCharacter(final String name) {
        for (final CharData cData : this.characterList) {
            if (cData.name.equalsIgnoreCase(name)) {
                this.activeCharacter = cData;
                setSaveable(true);
                setNoChars(false);
                return;
            }
        }
        this.activeCharacter = new CharData(name);
        this.characterList.add(this.activeCharacter);
        setSaveable(true);
        setNoChars(false);
    }


    public String getTranslatorApiKey() {
        return this.translator.getKey();
    }

    public void setTranslatorApiKey(final String apiKey) {
        this.translator.useKey(apiKey);
    }
}
