package haven.scriptengine.providers;

import haven.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * Player: Vlad.Rassokhin@gmail.com
 * Date: 10.01.11
 * Time: 19:15
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Player {

    private static Coord position;

    public static int getId() {
        return CustomConfig.playerId;
    }

    public static int getStamina() {
        return stamina;
    }

    public static int getAuthority() {
        return authorityNow;
    }

    public static int getAuthorityMax() {
        return authorityMax;
    }

    public static int getHPHard() {
        return hpHard;
    }

    public static int getHPSoft() {
        return hpSoft;
    }

    public static int getHP() {
        return hp;
    }

    public static int getHappy() {
        return happy;
    }

    public static int getHappyTowards() {
        return happyTowards;
    }

    public static int getHungry() {
        return hungry;
    }

    public static int getSpeed() {
        return speedCurrent;
    }

    public static int getMaxSpeed() {
        return speedMax;
    }

    public static boolean setSpeed(int speed) {
        return speedGet != null && speedGet.changeSpeed(speed);
    }

    public static void iMeterGenerated(IMeter meter, String resName) {
        newMeter(meter, resName);
    }

    public static void meterUpdated(IMeter meter, String tooltip) {
        meterUp(meter, tooltip);
    }

    private static void meterUp(IMeter meter, String tooltip) {
        try {
            if (meter == hpMeter) updateHp(tooltip);
            if (meter == energyMeter) updateEnergy(tooltip);
            if (meter == happyMeter) updateHappy(tooltip);
            if (meter == hungryMeter) updateHungry(tooltip);
            if (meter == authorityMeter) updateAuthority(tooltip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void newMeter(IMeter meter, String resName) {
        if (resName.equals("gfx/hud/meter/hp")) //HP meter
            hpMeter = meter;
        else if (resName.equals("gfx/hud/meter/nrj")) // Energy Meter
            energyMeter = meter;
        else if (resName.equals("gfx/hud/meter/hngr")) // Hungry Meter
            hungryMeter = meter;
        else if (resName.equals("gfx/hud/meter/happy")) // Happyness Meter
            happyMeter = meter;
        else if (resName.equals("gfx/hud/meter/auth")) // Authority Meter
            authorityMeter = meter;
        else
            System.err.println("Unexpected IMeter with imagename=" + resName);
    }

    private static void updateAuthority(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        authorityNow = Integer.parseInt(makeMatch.group());
        makeMatch.find();
        authorityMax = Integer.parseInt(makeMatch.group());
    }

    private static void updateHungry(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        makeMatch.find();
        hungry = Integer.parseInt(makeMatch.group());
    }

    private static void updateHappy(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        happy = Integer.parseInt(makeMatch.group());
        happyTowards = (makeMatch.find()) ? Integer.parseInt(makeMatch.group()) : 0;
        if (tooltip.startsWith("Neutral")) {
            happyTowards = happy;
            happy = 0;
        }
        if (tooltip.startsWith("Un")) happy *= -1;
    }

    private static void updateEnergy(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        stamina = Integer.parseInt(makeMatch.group());
    }


    public static void updateHp(String tooltip) {
        Matcher makeMatch = intsOnly.matcher(tooltip);
        makeMatch.find();
        hp = Integer.parseInt(makeMatch.group());
        makeMatch.find();
        hpSoft = Integer.parseInt(makeMatch.group());
        makeMatch.find();
        hpHard = Integer.parseInt(makeMatch.group());
    }

    static private IMeter hpMeter;
    static private IMeter energyMeter;
    static private IMeter happyMeter;
    static private IMeter hungryMeter;
    static private IMeter authorityMeter;

    static private int id = -1;
    static private int stamina = -1;
    static private int hp = -1;
    static private int hpSoft = -1;
    static private int hpHard = -1;
    static private int hungry = -1;
    static private int authorityNow = -1;
    static private int authorityMax = -1;
    static private int happy = -1;
    static private int happyTowards = -1;

    static private int progress = -1; // [0-100] or -1 if not in progress

    static private Speedget speedGet;
    static private int speedCurrent = -1; // [0-3]
    static private int speedMax = -1; // [0-3]

    static final Pattern intsOnly = Pattern.compile("[-]?\\d+");

    public static void updateProgress(int p) {
        progress = p;
    }

    public static int getProgress() {
        return progress;
    }

    public static boolean isInProgress() {
        return progress > 0;
    }

    public static void updateSpeed(int cur, int max, Speedget sg) {
        speedCurrent = cur;
        speedMax = max;
        speedGet = sg;
    }

    public static Coord getPosition() {
        Gob pl;
        if ( ((pl = CustomConfig.glob.oc.getgob(getId())) != null) ) {
            return pl.getc();
        } else {
            return new Coord(0, 0);
        }
    }
}
