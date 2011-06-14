package haven.scriptengine.providers;

import haven.CustomConfig;
import haven.CustomConfigProcessor;
import haven.CustomConsole;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 23:33
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Config {

    public static void setNightvision(boolean nightvision) {
        CustomConfig.hasNightVision = nightvision;
    }

//    public void setNightvision(String arg0) {
//        arg0 = arg0.toUpperCase();
//        if (arg0.equals("ON") || arg0.equals("TRUE"))
//            CustomConfig.hasNightVision = true;
//        else if (arg0.equals("OFF") || arg0.equals("FALSE"))
//            CustomConfig.hasNightVision = false;
//        else throw new RuntimeException("setNightvision(ON|OFF) " + arg0);
//    }

    public static void setIrc(boolean irc) {
        CustomConfig.isIRCOn = irc;
    }

//    public void setIrc(String arg0) {
//        arg0 = arg0.toUpperCase();
//        if (arg0.equals("ON") || arg0.equals("TRUE"))
//            CustomConfig.isIRCOn = true;
//        else if (arg0.equals("OFF") || arg0.equals("FALSE"))
//            CustomConfig.isIRCOn = false;
//        else throw new RuntimeException("setIrc(ON|OFF)");
//    }

    public static void setDebugMsgs(String arg0) {
        arg0 = arg0.toUpperCase();
        if (arg0.equals("ON") || arg0.equals("TRUE"))
            CustomConfig.setDebugLogging(true);
        else if (arg0.equals("OFF") || arg0.equals("FALSE"))
            CustomConfig.setDebugLogging(false);
        else throw new RuntimeException("setDebugMsgs(ON|OFF)");
    }

    public static void setScreenSize(int width, int height) {
        try {
            CustomConfig.setWindowSize(Math.max(width, 800), Math.max(height, 600));
            CustomConfigProcessor.saveSettings();
            CustomConsole.append("Client must be restarted for new settings to take effect.", Color.RED.darker());
        } catch (NumberFormatException e) {
            throw new RuntimeException("setScreenSize(int width, int height);");
        }
    }

    public static void setSound(int sound) {
        setSound(String.valueOf(sound));
    }

    public static void setSound(String arg0) {
        arg0 = arg0.toUpperCase();
        int vol;
        try {
            if (arg0.equals("ON") || arg0.equals("TRUE")) {
                CustomConfig.isSoundOn = true;
            } else if (arg0.equals("OFF") || arg0.equals("FALSE")) {
                CustomConfig.isSoundOn = false;
            } else if ((vol = Integer.parseInt(arg0)) >= 0 && vol <= 100) {
                CustomConfig.sfxVol = vol;
            } else throw new RuntimeException("setSound([ON|OFF]|[0-100])");
        } catch (NumberFormatException e) {
            throw new RuntimeException("setSound([ON|OFF]|[0-100])");
        }
    }

    public static void setMusic(String arg0) {
        arg0 = arg0.toUpperCase();
        int vol;
        try {
            if (arg0.equals("ON") || arg0.equals("TRUE")) {
                CustomConfig.isMusicOn = true;
            } else if (arg0.equals("OFF") || arg0.equals("FALSE")) {
                CustomConfig.isMusicOn = false;
            } else if ((vol = Integer.parseInt(arg0)) >= 0 && vol <= 100) {
                CustomConfig.musicVol = vol;
            } else throw new RuntimeException("setMusic([ON|OFF]|[0-100])");
        } catch (NumberFormatException e) {
            throw new RuntimeException("setMusic([ON|OFF]|[0-100])");
        }
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

    public static int getScreenWidth() {
        return CustomConfig.getWindowWidth();
    }

    public static int getScreenHeight() {
        return CustomConfig.getWindowHeight();
    }

    public static int getScreenCenterX() {
        return CustomConfig.getWindowCenter().x();
    }

    public static int getScreenXenterY() {
        return CustomConfig.getWindowCenter().y();
    }

    public static boolean isSoundOn() {
        return CustomConfig.isSoundOn;
    }

    public static int getSoundVolume() {
        return CustomConfig.sfxVol;
    }

    public static boolean isMusicOn() {
        return CustomConfig.isMusicOn;
    }

    public static int getMusicVolume() {
        return CustomConfig.musicVol;
    }

    public static boolean isDebugLogging() {
        return CustomConfig.isDebugLogging();
    }

    private Config() {
    }
}
