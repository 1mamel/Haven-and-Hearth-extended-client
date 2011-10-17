package ender.timer;

import haven.Coord;
import haven.Label;
import haven.UI;
import haven.Window;


public class Timer {
    private static final int SERVER_RATIO = 3;

    public static long server;
    public static long local;

    private long start;
    private long time;
    private String name;
    private long seconds;
    public Callback updcallback;

    public Timer(final long start, final long time, final String name) {
        this.start = start;
        this.time = time;
        this.name = name;
        TimersController.add(this);
    }

    public Timer(final long time, final String name) {
        this(0, time, name);
    }

    public boolean isWorking() {
        return start != 0;
    }

    public void stop() {
        start = 0;
        if (updcallback != null) {
            updcallback.run(this);
        }
        TimersController.save();
    }

    public void start() {
        start = server + SERVER_RATIO * ((System.currentTimeMillis() / 1000) - local);
        TimersController.save();
    }

    public void start(final long start) {
        this.start = start;
    }

    public synchronized boolean update() {
        final long now = System.currentTimeMillis() / 1000;
        seconds = time - now + local - (server - start) / SERVER_RATIO;
        if (seconds <= 0) {
            final Window wnd = new Window(new Coord(250, 100), Coord.z, UI.instance.root, "Timer");
            final String str;
            if (seconds < -60) {
                str = String.format("%s elapsed since timer named \"%s\"  finished it's work", toString(), name);
            } else {
                str = String.format("Timer named \"%s\" just finished it's work", name);
            }
            new Label(Coord.z, wnd, str);
            wnd.justclose = true;
            wnd.pack();
            return true;
        }
        if (updcallback != null) {
            updcallback.run(this);
        }
        return false;
    }

    public synchronized long getStart() {
        return start;
    }

    public synchronized void setStart(final long start) {
        this.start = start;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(final String name) {
        this.name = name;
    }

    public synchronized void setTime(final long time) {
        this.time = time;
    }

    public synchronized long getTime() {
        return time;
    }

    @Override
    public String toString() {
        final long t = Math.abs(isWorking() ? seconds : time);
        final int h = (int) (t / 3600);
        final int m = (int) ((t % 3600) / 60);
        final int s = (int) (t % 60);
        return String.format("%d:%02d:%02d", h, m, s);
    }

    public void destroy() {
        TimersController.remove(this);
        updcallback = null;
    }

}
