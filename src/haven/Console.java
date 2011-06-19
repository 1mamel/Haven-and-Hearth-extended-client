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

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Console {
    static private final Map<String, Command> scommands = new ConcurrentHashMap<String, Command>();
    private final Map<String, Command> commands = new ConcurrentHashMap<String, Command>();
    public PrintWriter out;

    {
        clearout();
    }

    public static interface Command {
        public void run(Console cons, String[] args) throws Exception;
    }

    public static interface Directory {
        public Map<String, Command> findcmds();
    }

    public static void setscmd(String name, Command cmd) {
        scommands.put(name, cmd);
    }

    public void setcmd(String name, Command cmd) {
        commands.put(name, cmd);
    }

    public Map<String, Command> findcmds() {
        Map<String, Command> ret = new ConcurrentHashMap<String, Command>();
        ret.putAll(scommands);
        ret.putAll(commands);
        return ret;
    }

    public Command findcmd(String name) {
        Command ret = commands.get(name);
        if (ret == null) ret = scommands.get(name);
        return ret;
    }

    public void run(String[] args) throws Exception {
        if (args.length < 1)
            return;
        Command cmd = findcmd(args[0]);
        if (cmd == null)
            throw (new Exception(args[0] + ": no such command"));
        cmd.run(this, args);
    }

    public void run(String cmdl) throws Exception {
        run(Utils.splitwords(cmdl));
    }

    public void clearout() {
        out = new PrintWriter(new Writer() {
            public void write(char[] b, int o, int c) {
            }

            public void close() {
            }

            public void flush() {
            }
        });
    }
}
