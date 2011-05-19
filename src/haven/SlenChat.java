/**
 * @(#)SlenChat.java
 *
 *
 * @author
 * @version 1.00 2009/10/15
 */

package haven;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SlenChat extends ChatHW {
    public boolean initialized = false;
    private String channel;
    private final String password;
    public final UserList userList;

    private final SlenConsole handler;

    public static class UserList extends Window {
        List<Listbox.Option> users = new ArrayList<Listbox.Option>();
        final Listbox out;
        SlenChat owner;
        boolean isVisible = true;

        static {
            Widget.addtype("ircuserlist", new WidgetFactory() {
                public Widget create(Coord c, Widget parent, Object[] args) {
                    return (new UserList((SlenChat) parent));
                }
            });
        }

        public UserList(SlenChat parent) {
            super(new Coord(10, CustomConfig.getWindowSize().y - parent.sz.y - 10), new Coord(100, parent.sz.y - 10), parent.parent.parent, "Users", false);
            out = new Listbox(Coord.z, new Coord(100, 105), this, users);
            owner = parent;
            ui.bind(this, CustomConfig.getNextCustomWidgetId());
            ui.bind(out, CustomConfig.getNextCustomWidgetId());
        }

        synchronized public void addUser(String user, String nick) {
            if (user != null && !containsNick(nick)) {
                users.add(new Listbox.Option(user, nick));
            }
        }

        public void addUserList(List<Listbox.Option> userList) {
            users = userList == null ? userList : users;
        }

        public void rmvUser(String name) {
            Listbox.Option tUser;
            if (name != null
                    && (tUser = getUser(name)) != null) {
                users.remove(tUser);
            }
        }

        public boolean containsUser(String user) {
            for (Listbox.Option tUser : users) {
                if (tUser.containsString(user)) return true;
            }
            return false;
        }

        public boolean containsNick(String nick) {
            return containsUser(nick);
        }

        public Listbox.Option getUser(String ident) {
            if (containsUser(ident)) {
                for (Listbox.Option tUser : users) {
                    if (tUser.containsString(ident)) {
                        return tUser;
                    }
                }
            }
            return null;
        }

        public void changeUser(String olduser, String newuser) {
            if (!containsUser(olduser)) return;
            Listbox.Option tUser = getUser(olduser);
            if (tUser != null) tUser.name = newuser;
        }

        public void changeNick(String oldnick, String newnick) {
            oldnick = SlenConsole.parseNick(oldnick);
            newnick = SlenConsole.parseNick(newnick);
            if (!containsNick(oldnick)) return;
            Listbox.Option tUser = getUser(oldnick);
            if (tUser != null) tUser.disp = newnick;
        }

        public boolean keydown(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER
                    && out.chosen != null
                    && out.hasfocus
                    && owner.handler.findWindow(out.chosen.disp) == null) {
                owner.handler.wndList.add(new SlenChat(owner.handler, out.chosen.disp, null, false));
            }
            return true;
        }

        public boolean toggle() {
            isVisible = !isVisible;
            visible = isVisible;
            return isVisible;
        }

        public void destroy() {
            visible = false;
            users.clear();
            owner = null;
            out.destroy();
            super.destroy();
        }

    }

    static {
        Widget.addtype("ircchat", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                String channel = (String) args[0];
                return (new SlenChat((SlenConsole) parent, channel, null));
            }
        });
    }

    SlenChat(SlenConsole parentHandler, String channel, String password) {
        this(parentHandler, channel, password, true);
    }

    SlenChat(SlenConsole parentHandler, String channel, String password, boolean hasUserList) {
        super(parentHandler.parent, channel, true);
        this.channel = channel;
        this.password = password == null ? "" : password;
        handler = parentHandler;
        userList = hasUserList ? new UserList(this) : null;
        initialized = true;
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == in) {
            handler.handleInput((String) args[0], this);
            in.settext("");
            return;
        } else if (sender == cbtn) {
            destroy();
            return;
        }
        super.wdgmsg(sender, msg, args);
    }

    public void hide() {
        if (userList != null) userList.hide();
        super.hide();
    }

    public void show() {
        if (userList != null && userList.isVisible) userList.show();
        super.show();
    }

    public void setChannel(String newChannel) {
        channel = newChannel;
    }

    public String getChannel() {
        return channel;
    }

    public String getPassword() {
        return password;
    }

    public void destroy() {
        handler.IRC.writeln("PART " + channel + ' ' + handler.user + " closed this window.");
        if (userList != null) userList.destroy();
        channel = null;
        initialized = false;
        super.destroy();
    }
}