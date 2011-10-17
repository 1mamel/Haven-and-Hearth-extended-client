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

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;

public class JnlpCache implements ResCache {
    private final PersistenceService back;
    private final URL base;

    private JnlpCache(final PersistenceService back, final URL base) {
        this.back = back;
        this.base = base;
    }

    public static JnlpCache create() {
        try {
            final Class<? extends ServiceManager> cl = Class.forName("javax.jnlp.ServiceManager").asSubclass(ServiceManager.class);
            final Method m = cl.getMethod("lookup", String.class);
            final BasicService basic = (BasicService) m.invoke(null, "javax.jnlp.BasicService");
            final PersistenceService prs = (PersistenceService) m.invoke(null, "javax.jnlp.PersistenceService");
            return (new JnlpCache(prs, basic.getCodeBase()));
        } catch (Exception e) {
            return (null);
        }
    }

    private static String mangle(final String nm) {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < nm.length(); i++) {
            final char c = nm.charAt(i);
            if (c == '/')
                buf.append('_');
            else
                buf.append(c);
        }
        return (buf.toString());
    }

    private void realput(final URL loc, final byte[] data) {
        FileContents file;
        try {
            try {
                file = back.get(loc);
            } catch (FileNotFoundException e) {
                back.create(loc, data.length);
                file = back.get(loc);
            }
            if (file.getMaxLength() < data.length) {
                if (file.setMaxLength(data.length) < data.length) {
                    back.delete(loc);
                    return;
                }
            }
            final OutputStream s = file.getOutputStream(true);
            try {
                s.write(data);
            } finally {
                s.close();
            }
        } catch (IOException ignored) {
        } catch (Exception e) {
            /* There seems to be a strange bug in NetX. */
        }
    }

    private void put(final URL loc, final byte[] data) {
        /*
       * Interestingly enough, it seems that the JNLP persistence
       * cache, at least in Sun's implementation, is REALLY
       * SLOW. "Really slow" meaning that it takes like 1 second to
       * save each muffin. How they managed to do this, I have no
       * clue, but to counter it, so as to not take up precious
       * resource/minimap loader time, the actual storage is
       * deferred.
       *
       * Sucks? You don't say.
       */
        Utils.defer(new Runnable() {
            public void run() {
                realput(loc, data);
            }
        });
    }

    private InputStream get(final URL loc) throws IOException {
        final FileContents file = back.get(loc);
        return (file.getInputStream());
    }

    public OutputStream store(final String name) throws IOException {
        /*
       * The persistence service actually yields a real
       * OutputStream, but since it needs to know the final size of
       * the data before that, it isn't actually possible to use it
       * as an OutputStream in any reasonable manner. Thus, all data
       * is first "pre-cached" in a ByteArrayOutputStream, and then
       * written to the persistence store.
       *
       * Oh God, it's so stupid.
       */
        final OutputStream ret = new ByteArrayOutputStream() {
            public void close() {
                final byte[] res = toByteArray();
                try {
                    put(new URL(base, mangle(name)), res);
                } catch (java.net.MalformedURLException e) {
                    throw (new RuntimeException(e));
                }
            }
        };
        return (ret);
    }

    public InputStream fetch(final String name) throws IOException {
        try {
            final URL loc = new URL(base, mangle(name));
            return (get(loc));
        } catch (IOException e) {
            throw (e);
        } catch (Exception e) {
            /* There seems to be a weird bug in NetX */
            throw ((IOException) (new IOException("Virtual NetX IO exception").initCause(e)));
        }
    }
}
