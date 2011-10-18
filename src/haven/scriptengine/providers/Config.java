package haven.scriptengine.providers;

import haven.*;

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

    public static void setNightvision(final boolean nightvision) {
        CustomConfig.setHasNightVision(nightvision);
    }

    public static void setIrc(final boolean irc) {
        CustomConfig.setIRCOn(irc);
    }

    public static void setScreenSize(final int width, final int height) {
        CustomConfig.setWindowSize(Math.max(width, 800), Math.max(height, 600));
        CustomConfigProcessor.saveConfig();
        CustomConsole.logger.warn("Client must be restarted for new settings to take effect.");
    }

    public static void setSoundV(int vol) {
        if (vol < 0) vol = 0;
        if (vol > 100) vol = 100;
        CustomConfig.setSfxVol(vol);
    }

    public static void setSound(final boolean state) {
        CustomConfig.setSoundOn(state);
    }

    public static void setMusicV(int vol) {
        if (vol < 0) vol = 0;
        if (vol > 100) vol = 100;
        CustomConfig.setMusicVol(vol);
    }

    public static void setMusic(final boolean state) {
        CustomConfig.setMusicOn(state);
    }

    public static void save() {
        CustomConfigProcessor.saveConfig();
    }

    public static void forcesave() {
        CustomConfig.setSaveable(true);
        CustomConfigProcessor.saveConfig();
    }

    public static boolean getNightvision() {
        return CustomConfig.isHasNightVision();
    }

    public static boolean getIrc() {
        return CustomConfig.isIRCOn();
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
        return CustomConfig.isSoundOn();
    }

    public static int getSoundV() {
        return CustomConfig.getSfxVol();
    }

    public static boolean getMusic() {
        return CustomConfig.isMusicOn();
    }

    public static int getMusicV() {
        return CustomConfig.getMusicVol();
    }


    private Config() {
    }

    public static String getBot1() {
        return bot1;
    }

    public static void setBot1(final String bot1) {
        Config.bot1 = bot1;
    }

    public static String getBot2() {
        return bot2;
    }

    public static void setBot2(final String bot2) {
        Config.bot2 = bot2;
    }
}
