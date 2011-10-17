/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven.test;

import haven.*;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashSet;

public class TestClient implements Runnable {
    public Session sess;
    public InetAddress addr;
    public String user;
    public byte[] cookie;
    public ThreadGroup tg;
    public Thread me;
    public UI ui;
    public final boolean loop = false;
    public final Collection<Robot> robots = new HashSet<Robot>();
    private final static Object errsync = new Object();

    public TestClient(final String user) {
        try {
            addr = InetAddress.getByName("localhost");
        } catch (java.net.UnknownHostException e) {
            throw (new RuntimeException("localhost not known"));
        }
        this.user = user;
        this.cookie = new byte[64];
        tg = new ThreadGroup(HackThread.tg(), "Test client") {
            public void uncaughtException(final Thread t, final Throwable e) {
                synchronized (errsync) {
                    System.err.println("Exception in test client: " + TestClient.this.user);
                    e.printStackTrace(System.err);
                }
                TestClient.this.stop();
            }
        };
    }

    public void connect() throws InterruptedException {
        sess = new Session(addr, user, cookie);
        synchronized (sess) {
            while (sess.state != Session.State.ERROR) {
//            while (sess.state.length() != 0) {
                if (sess.connfailed != 0)
                    throw (new RuntimeException("Connection failure for " + user + " (" + sess.connfailed + ')'));
                sess.wait();
            }
        }
    }

    public void addbot(final Robot bot) {
        synchronized (robots) {
            robots.add(bot);
        }
    }

    public void rembot(final Robot bot) {
        synchronized (robots) {
            robots.remove(bot);
        }
    }

    public class TestUI extends UI {
        public TestUI(final Coord sz, final Session sess) {
            super(sz, sess);
        }

        public void newwidget(final int id, @NotNull final String type, @NotNull final Coord c, final int parent, final Object... args) throws InterruptedException {
            super.newwidget(id, type, c, parent, args);
            final Widget w = widgets.get(id);
            synchronized (robots) {
                for (final Robot r : robots)
                    r.newwdg(id, w, args);
            }
        }

        public void destroy(final Widget w) {
            final int id;
            if (!rwidgets.containsKey(w))
                id = -1;
            else
                id = rwidgets.get(w);
            synchronized (robots) {
                for (final Robot r : robots)
                    r.dstwdg(id, w);
            }
            super.destroy(w);
        }

        public void uimsg(final int id, @NotNull final String msg, final Object... args) {
            final Widget w = widgets.get(id);
            synchronized (robots) {
                for (final Robot r : robots)
                    r.uimsg(id, w, msg, args);
            }
            super.uimsg(id, msg, args);
        }
    }

    public void run() {
        try {
            try {
                do {
                    connect();
                    final RemoteUI rui = new RemoteUI(sess);
                    ui = new TestUI(new Coord(800, 600), sess);
                    rui.run(ui);
                } while (loop);
            } catch (InterruptedException ignored) {
            }
        } finally {
            stop();
        }
    }

    public void start() {
        me = new HackThread(tg, this, "Main thread");
        me.start();
    }

    public void stop() {
        tg.interrupt();
    }

    public boolean alive() {
        return ((me != null) && me.isAlive());
    }

    public void join() {
        while (alive()) {
            try {
                me.join();
            } catch (InterruptedException e) {
                tg.interrupt();
            }
        }
    }

    public String toString() {
        return ("Client " + user);
    }
}
