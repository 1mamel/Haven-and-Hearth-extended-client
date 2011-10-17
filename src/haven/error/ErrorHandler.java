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

package haven.error;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class ErrorHandler extends ThreadGroup {
    private final URL errordest;
    private static final String[] sysprops = {
            "java.version",
            "java.vendor",
            "os.name",
            "os.arch",
            "os.version",
    };
    private final ThreadGroup initial;
    private final Map<String, Object> props = new HashMap<String, Object>();
    private final Reporter reporter;

    public static void setprop(final String key, final Object val) {
        final ThreadGroup tg = Thread.currentThread().getThreadGroup();
        if (tg instanceof ErrorHandler)
            ((ErrorHandler) tg).lsetprop(key, val);
    }

    public void lsetprop(final String key, final Object val) {
        props.put(key, val);
    }

    private class Reporter extends Thread {
        private final Queue<Report> errors = new LinkedList<Report>();
        private ErrorStatus status;

        Reporter(final ErrorStatus status) {
            super(initial, "Error reporter");
            setDaemon(true);
            this.status = status;
        }

        public void run() {
            while (true) {
                synchronized (errors) {
                    try {
                        errors.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    Report r;
                    while ((r = errors.poll()) != null) {
                        try {
                            doreport(r);
                        } catch (Exception e) {
                            status.senderror(e);
                        }
                    }
                }
            }
        }

        private void doreport(final Report r) throws IOException {
            if (!status.goterror(r.t))
                return;
            final URLConnection c = errordest.openConnection();
            status.connecting();
            c.setDoOutput(true);
            c.addRequestProperty("Content-Type", "application/x-java-error");
            c.connect();
            final ObjectOutputStream o = new ObjectOutputStream(c.getOutputStream());
            status.sending();
            o.writeObject(r);
            o.close();
            final InputStream i = c.getInputStream();
            final byte[] buf = new byte[1024];
            //noinspection StatementWithEmptyBody
            while (i.read(buf) >= 0) ;
            i.close();
            status.done();
        }

        public void report(final Thread th, final Throwable t) {
            final Report r = new Report(t);
            r.props.putAll(props);
            r.props.put("thnm", th.getName());
            r.props.put("thcl", th.getClass().getName());
            synchronized (errors) {
                errors.add(r);
                errors.notifyAll();
            }
            try {
                r.join();
            } catch (InterruptedException e) { /* XXX? */ }
        }
    }

    private void defprops() {
        for (final String p : sysprops)
            props.put(p, System.getProperty(p));
        final Runtime rt = Runtime.getRuntime();
        props.put("cpus", rt.availableProcessors());
        final InputStream in = ErrorHandler.class.getResourceAsStream("/buildinfo");
        try {
            try {
                if (in != null) {
                    final Properties info = new Properties();
                    info.load(in);
                    for (final Map.Entry<Object, Object> e : info.entrySet())
                        props.put("jar." + e.getKey(), e.getValue());
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw (new Error(e));
        }
    }

    public ErrorHandler(final ErrorStatus ui, final URL errordest) {
        super("Haven client");
        this.errordest = errordest;
        initial = Thread.currentThread().getThreadGroup();
        reporter = new Reporter(ui);
        reporter.start();
        defprops();
    }

    public ErrorHandler(final URL errordest) {
        this(new ErrorStatus.Simple(), errordest);
    }

    public void sethandler(final ErrorStatus handler) {
        reporter.status = handler;
    }

    public void uncaughtException(final Thread t, final Throwable e) {
        reporter.report(t, e);
    }
}
