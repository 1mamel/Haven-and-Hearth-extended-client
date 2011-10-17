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

import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Party {
    final Map<Integer, Member> memb = new TreeMap<Integer, Member>();
    boolean membersMapChanged = false;
    Member leader = null;
    public static final int PD_LIST = 0;
    public static final int PD_LEADER = 1;
    public static final int PD_MEMBER = 2;
    private final Glob glob;

    public Party(final Glob glob) {
        this.glob = glob;
    }

    public class Member {
        final int gobid;
        private Coord c = null;
        Color col = Color.BLACK;

        public Member(final int gobid) {
            this.gobid = gobid;
        }

        public Gob getgob() {
            return (glob.oc.getgob(gobid));
        }

        public Coord getc() {
            final Gob gob;
            if ((gob = getgob()) != null)
                return (gob.getc());
            return (c);
        }
    }

    public void msg(final Message msg) {
        final Set<Integer> ids = new HashSet<Integer>();
        while (!msg.eom()) {
            final int type = msg.uint8();
            if (type == PD_LIST) {
                ids.clear();
                while (true) {
                    final int id = msg.int32();
                    if (id < 0) break;
                    ids.add(id);
                }
                memb.keySet().retainAll(ids);
                for (final int id : ids) {
                    Member m = memb.get(id);
                    if (m == null) {
                        m = new Member(id);
                    }
                    memb.put(id, m);
                }
                membersMapChanged = true;
                final int lid = (leader == null) ? -1 : leader.gobid;
                leader = memb.get(lid);
            } else if (type == PD_LEADER) {
                final Member m = memb.get(msg.int32());
                if (m != null) leader = m;
            } else if (type == PD_MEMBER) {
                final Member m = memb.get(msg.int32());
                Coord c = null;
                final boolean visible = msg.uint8() == 1;
                if (visible) c = msg.coord();
                final Color col = msg.color();
                if (m != null) {
                    m.c = c;
                    m.col = col;
                }
            }
        }
    }
}
