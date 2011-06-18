package haven.scriptengine.providers;

import haven.Coord;
import haven.CustomConfig;
import haven.CustomConfigProcessor;
import haven.CustomConsole;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 23:33
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Config {

    private static String bot1;
    private static String bot2;

    public static void setNightvision(boolean nightvision) {
        CustomConfig.hasNightVision = nightvision;
    }

    public static void setIrc(boolean irc) {
        CustomConfig.isIRCOn = irc;
    }

    public static void setDebugLogging(boolean dm) {
        CustomConfig.setDebugLogging(dm);
    }

    public static void setScreenSize(int width, int height) {
        CustomConfig.setWindowSize(Math.max(width, 800), Math.max(height, 600));
        CustomConfigProcessor.saveSettings();
        CustomConsole.logger.warn("Client must be restarted for new settings to take effect.");
    }

    public static void setSoundV(int vol) {
        if (vol < 0) vol = 0;
        if (vol > 100) vol = 100;
        CustomConfig.sfxVol = vol;
    }

    public static void setSound(boolean state) {
        CustomConfig.isSoundOn = state;
    }

    public static void setMusicV(int vol) {
        if (vol < 0) vol = 0;
        if (vol > 100) vol = 100;
        CustomConfig.musicVol = vol;
    }

    public static void setMusic(boolean state) {
        CustomConfig.isMusicOn = state;
    }

    public static void save() {
        CustomConfigProcessor.saveSettings();
    }

    public static void forcesave() {
        CustomConfig.isSaveable = true;
        CustomConfigProcessor.saveSettings();
    }

    public static boolean getNightvision() {
        return CustomConfig.hasNightVision;
    }

    public static boolean getIrc() {
        return CustomConfig.isIRCOn;
    }

    public static Coord getWindowSize() {
        return CustomConfig.getWindowSize();
    }

    public static int getWindowWidth() {
        return CustomConfig.getWindowWidth();
    }

    public static int getWindowHeight() {
        return CustomConfig.getWindowHeight();
    }

    public static Coord getWindowCenter() {
        return CustomConfig.getWindowCenter();
    }

    public static int getWindowCenterX() {
        return CustomConfig.getWindowCenter().x;
    }

    public static int getWindowCenterY() {
        return CustomConfig.getWindowCenter().y;
    }

    public static boolean getSound() {
        return CustomConfig.isSoundOn;
    }

    public static int getSoundV() {
        return CustomConfig.sfxVol;
    }

    public static boolean getMusic() {
        return CustomConfig.isMusicOn;
    }

    public static int getMusicV() {
        return CustomConfig.musicVol;
    }

    public static boolean getDebugLogging() {
        return CustomConfig.isDebugLogging();
    }


    private Config() {
    }

    public static String getBot1() {
        return bot1;
    }

    public static void setBot1(String bot1) {
        Config.bot1 = bot1;
    }

    public static String getBot2() {
        return bot2;
    }

    public static void setBot2(String bot2) {
        Config.bot2 = bot2;
    }
}
