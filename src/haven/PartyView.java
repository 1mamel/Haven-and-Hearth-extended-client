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

import haven.Party.Member;

import java.util.*;
import java.util.Map.Entry;

public class PartyView extends Widget {
    final int ign; // Player id in group?
    final Party party = ui.sess.glob.party;
    final Map<Member, Avaview> avs = new HashMap<Member, Avaview>();
    Button leaveButton = null;

    static {
        Widget.addtype("pv", new WidgetFactory() {
            public Widget create(final Coord c, final Widget parent, final Object[] args) {
                return (new PartyView(c, parent, (Integer) args[0]));
            }
        });
    }

    PartyView(final Coord c, final Widget parent, final int ign) {
        super(c, new Coord(84, 140), parent);
        this.ign = ign;
        update();
    }

    private void update() {
        if (party.membersMapChanged) {
            final Collection<Member> old = new HashSet<Member>(avs.keySet());
            for (final Member m : party.memb.values()) {
                if (m.gobid == ign)
                    continue;
                Avaview w = avs.get(m);
                if (w == null) {
                    w = new Avaview(Coord.z, this, m.gobid, Avaview.smallSize) {
                        private Tex tooltip = null;

                        public Object tooltip(final Coord c, final boolean again) {
                            final Gob gob = m.getgob();
                            if (gob == null)
                                return (tooltip);
                            final KinInfo ki = gob.getattr(KinInfo.class);
                            if (ki == null)
                                return (null);
                            return (tooltip = ki.rendered());
                        }
                    };
                    avs.put(m, w);
                } else {
                    old.remove(m);
                }
            }
            for (final Member m : old) {
                ui.destroy(avs.get(m));
                avs.remove(m);
            }
            final List<Map.Entry<Member, Avaview>> wl = new ArrayList<Map.Entry<Member, Avaview>>(avs.entrySet());
            Collections.sort(wl, new Comparator<Map.Entry<Member, Avaview>>() {
                public int compare(final Entry<Member, Avaview> a, final Entry<Member, Avaview> b) {
                    return (a.getKey().gobid - b.getKey().gobid);
                }
            });
            int i = 0;
            for (final Map.Entry<Member, Avaview> e : wl) {
                e.getValue().c = getAvavievCoordinates(i); // Performance improved ;)
                i++;
            }
        }
        for (final Map.Entry<Member, Avaview> e : avs.entrySet()) {
            e.getValue().color = e.getKey().col;
        }
        if ((!avs.isEmpty()) && (leaveButton == null)) {
            leaveButton = new Button(Coord.z, 84, this, "Leave party");
        }
        if ((avs.isEmpty()) && (leaveButton != null)) {
            ui.destroy(leaveButton);
            leaveButton = null;
        }
        sz.setY(CustomConfig.getWindowHeight() - c.y);
    }

    private static final List<Coord> avaviewCoordinates;

    static {
        avaviewCoordinates = new ArrayList<Coord>(20);
        assumeAvaviewCoordinates(20);
    }

    private static void generateAvaviewCoordinates(final int toInclusive) {
        synchronized (avaviewCoordinates) {
            for (int i = avaviewCoordinates.size(); i <= toInclusive; ++i) {
                //noinspection ObjectAllocationInLoop
                avaviewCoordinates.add(new Coord((i % 2) * 43, (i / 2) * 43 + 24));
            }
        }
    }

    private static void assumeAvaviewCoordinates(final int i) {
        if (avaviewCoordinates.size() < i) {
            synchronized (avaviewCoordinates) {
                if (avaviewCoordinates.size() < i) {
                    generateAvaviewCoordinates(i);
                }
            }
        }
    }

    private static Coord getAvavievCoordinates(final int i) {
        assumeAvaviewCoordinates(i);
        return avaviewCoordinates.get(i);
    }

    public void wdgmsg(final Widget sender, final String msg, final Object... args) {
        if (sender == leaveButton) {
            wdgmsg("leave");
            return;
        }
        for (final Entry<Member, Avaview> memberAvaviewEntry : avs.entrySet()) {
            if (sender == memberAvaviewEntry.getValue()) {
                wdgmsg("click", memberAvaviewEntry.getKey().gobid, args[0]);
                return;
            }
        }
        super.wdgmsg(sender, msg, args);
    }

    public void draw(final GOut g) {
        update();
        super.draw(g);
    }
}
