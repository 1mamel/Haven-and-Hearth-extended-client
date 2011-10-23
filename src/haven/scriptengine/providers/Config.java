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
        CustomConfig.current().setHasNightVision(nightvision);
    }

    public static void setIrc(final boolean irc) {
        CustomConfig.current().setIRCOn(irc);
    }

    public static void setScreenSize(final int width, final int height) {
        CustomConfig.current().setWindowSize(Math.max(width, 800), Math.max(height, 600));
        CustomConfig.current().save();
        CustomConsole.logger.warn("Client must be restarted for new settings to take effect.");
    }

    public static void setSoundV(int vol) {
        if (vol < 0) vol = 0;
        if (vol > 100) vol = 100;
        CustomConfig.current().setSfxVol(vol);
    }

    public static void setSound(final boolean state) {
        CustomConfig.current().setSoundOn(state);
    }

    public static void setMusicV(int vol) {
        if (vol < 0) vol = 0;
        if (vol > 100) vol = 100;
        CustomConfig.current().setMusicVol(vol);
    }

    public static void setMusic(final boolean state) {
        CustomConfig.current().setMusicOn(state);
    }

    public static void save() {
        CustomConfig.current().save();
    }

    public static void forcesave() {
        CustomConfig.current().setSaveable(true);
        CustomConfig.current().save();
    }

    public static boolean getNightvision() {
        return CustomConfig.current().isNightVision();
    }

    public static boolean getIrc() {
        return CustomConfig.current().isIRCOn();
    }

    public static Coord getWindowSize() {
        return CustomConfig.current().getWindowSize();
    }

    public static int getWindowWidth() {
        return CustomConfig.current().getWindowWidth();
    }

    public static int getWindowHeight() {
        return CustomConfig.current().getWindowHeight();
    }

    public static Coord getWindowCenter() {
        return CustomConfig.current().getWindowCenter();
    }

    public static int getWindowCenterX() {
        return CustomConfig.current().getWindowCenter().x;
    }

    public static int getWindowCenterY() {
        return CustomConfig.current().getWindowCenter().y;
    }

    public static boolean getSound() {
        return CustomConfig.current().isSoundOn();
    }

    public static int getSoundV() {
        return CustomConfig.current().getSfxVol();
    }

    public static boolean getMusic() {
        return CustomConfig.current().isMusicOn();
    }

    public static int getMusicV() {
        return CustomConfig.current().getMusicVol();
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
