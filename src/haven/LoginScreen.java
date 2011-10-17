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

package haven;


import org.jetbrains.annotations.NotNull;

public class LoginScreen extends Widget {
    Login cur;
    Text error;
    IButton btn;
    static final Text.Foundry textf;
    static final Text.Foundry textfs;
    final Tex bg = Resource.loadtex("gfx/loginscr");
    final Tex logo = Resource.loadtex("gfx/logo");
    Text progress = null;
    boolean first = true;
    private long time_to_reconnect = 0;
    long RECONNECT_TIME = 15000;
    boolean logging = false;

    static {
        textf = new Text.Foundry(new java.awt.Font("Sans", java.awt.Font.PLAIN, 16));
        textfs = new Text.Foundry(new java.awt.Font("Sans", java.awt.Font.PLAIN, 14));
    }

    public LoginScreen(final Widget parent) {
        super(Coord.z, CustomConfig.getWindowSize(), parent);
        setfocustab(true);
        parent.setfocus(this);
        logging = false;
        time_to_reconnect = RECONNECT_TIME;
        new Img(CustomConfig.getWindowCenter().sub(bg.sz().div(2)), bg, this);
        new Img(CustomConfig.getWindowCenter().add(20, -85).sub(logo.sz().div(2)), logo, this);
    }

    private static abstract class Login extends Widget {
        private Login(final Coord c, final Coord sz, final Widget parent) {
            super(c, sz, parent);
        }

        abstract Object[] data();

        abstract boolean enter();

        abstract String get_username();
    }

    private class Pwbox extends Login {
        final TextEntry user;
        final TextEntry pass;
        final CheckBox savepass;

        private Pwbox(final String username, final boolean save) {
            super(CustomConfig.getWindowCenter().add(-55, 10), new Coord(150, 150), LoginScreen.this);
            setfocustab(true);
            new Label(new Coord(0, 0), this, "Player name", textf);
            user = new TextEntry(new Coord(0, 20), new Coord(150, 20), this, username);
            new Label(new Coord(0, 60), this, "Password", textf);
            pass = new TextEntry(new Coord(0, 80), new Coord(150, 20), this, "");
            pass.pw = true;
            savepass = new CheckBox(new Coord(0, 110), this, "Remember me");
            savepass.a = save;
            if (user.text.length() == 0)
                setfocus(user);
            else
                setfocus(pass);
        }

        public void wdgmsg(final Widget sender, final String name, final Object... args) {
        }

        public String get_username() {
            return user.text;
        }

        Object[] data() {
            return (new Object[]{user.text, pass.text, savepass.a});
        }

        boolean enter() {
            if (user.text.length() == 0) {
                setfocus(user);
                return (false);
            } else if (pass.text.length() == 0) {
                setfocus(pass);
                return (false);
            } else {
                return (true);
            }
        }
    }

    private class Tokenbox extends Login {
        final Text label;
        final Button btn;
        private String acc;

        private Tokenbox(final String username) {
            super(CustomConfig.getWindowCenter().add(-105, 10), new Coord(250, 100), LoginScreen.this);
            acc = username;
            label = textfs.render("Identity is saved for " + username, java.awt.Color.WHITE);
            btn = new Button(new Coord(75, 30), 100, this, "Forget me");
        }


        public String get_username() {
            return acc;
        }

        Object[] data() {
            return (new Object[0]);
        }

        boolean enter() {
            return (true);
        }

        public void wdgmsg(final Widget sender, final String name, final Object... args) {
            if (sender == btn) {
                LoginScreen.this.wdgmsg("forget");
                return;
            }
            super.wdgmsg(sender, name, args);
        }

        public void draw(final GOut g) {
            g.image(label.tex(), new Coord((sz.x / 2) - (label.sz().x / 2), 0));
            super.draw(g);
        }
    }

    private void mklogin() {
        synchronized (ui) {
            btn = new IButton(CustomConfig.getWindowCenter().add(-27, 160), this, Resource.loadimg("gfx/hud/buttons/loginu"), Resource.loadimg("gfx/hud/buttons/logind"));
            progress(null);
        }
    }

    private void error(final String error) {
        logging = false;
        time_to_reconnect = RECONNECT_TIME;
        synchronized (ui) {
            if (this.error != null)
                this.error = null;
            if (error != null)
                this.error = textf.render(error, java.awt.Color.RED);
        }
    }

    private void progress(final String p) {
        synchronized (ui) {
            if (progress != null)
                progress = null;
            if (p != null)
                progress = textfs.render(p, java.awt.Color.WHITE);
        }
    }

    private void clear() {
        if (cur != null) {
            ui.destroy(cur);
            cur = null;
            ui.destroy(btn);
            btn = null;
        }
        progress(null);
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (sender == btn) {
            if (cur.enter())
                super.wdgmsg("login", cur.data());
            return;
        }
        super.wdgmsg(sender, msg, args);
    }

    public void uimsg(@NotNull final String msg, final Object... args) {
        synchronized (ui) {
            if (msg.equals("passwd")) {
                clear();
                cur = new Pwbox((String) args[0], (Boolean) args[1]);
                mklogin();
            } else if (msg.equals("token")) {
                clear();
                cur = new Tokenbox((String) args[0]);
                mklogin();
            } else if (msg.equals("error")) {
                error((String) args[0]);
            } else if (msg.equals("prg")) {
                error(null);
                clear();
                progress((String) args[0]);
            }
        }
    }

    public void draw(final GOut g) {
        c = CustomConfig.getWindowCenter().sub(400, 300);
        super.draw(g);

        if (error != null) {
            g.image(error.tex(), new Coord(CustomConfig.getWindowCenter().x - (error.sz().x / 2), CustomConfig.getWindowCenter().y + 200));
        }
        if (progress != null) {
            g.image(progress.tex(), new Coord(CustomConfig.getWindowCenter().x + 20 - (progress.sz().x / 2), CustomConfig.getWindowCenter().y + 50));
        }

        g.text("keep connect=" + Config.keep_connect, new Coord(20, 200));
        g.text("time=" + time_to_reconnect, new Coord(20, 220));
        g.text("first login=" + Config.FirstLogin, new Coord(20, 240));
        g.text("quick login=" + Config.quick_login, new Coord(20, 260));
    }

    public boolean type(final char k, final java.awt.event.KeyEvent ev) {
        if (k == 10) {
            if ((cur != null) && cur.enter())
                wdgmsg("login", cur.data());
            return (true);
        }
        return (super.type(k, ev));
    }

    public void update(final long dt) {
        if (time_to_reconnect > 0)
            time_to_reconnect = time_to_reconnect - dt;
        if (time_to_reconnect < 0)
            time_to_reconnect = 0;

        if (Config.keep_connect && !Config.FirstLogin && !logging)
            if (time_to_reconnect <= 0) {
                logging = true;
                super.wdgmsg("login", cur.data());
            }

        if ((first) && (cur != null)) {
            first = false;
            if (Config.quick_login && Config.FirstLogin) {
                if (cur.enter()) {
                    login();
                }
            }
        }
    }

    static public String Account = "";

    public void login() {
        Config.FirstLogin = false;
        Account = cur.get_username();
        super.wdgmsg("login", cur.data());
    }

}
