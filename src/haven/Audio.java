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

import haven.resources.layers.AudioL;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class Audio {
    public static boolean enabled = true;
    private static Thread player;
    public static final AudioFormat fmt = new AudioFormat(44100, 16, 2, true, false);
    private static final Collection<CS> ncl = new LinkedList<CS>();
    private static final Object queuemon = new Object();
    private static Collection<Runnable> queue = new LinkedList<Runnable>();
    private static int bufsize = 32768;
    public static double volume = 1.0;

    static {
        volume = Double.parseDouble(Utils.getpref("sfxvol", "1.0"));
    }

    public static void setvolume(final double volume) {
        Audio.volume = volume;
        Utils.setpref("sfxvol", Double.toString(volume));
    }

    public interface CS {
        public boolean get(double[] sample);
    }

    public static class DataClip implements CS {
        private final InputStream clip;
        private final double vol, sp;
        private int ack = 0;
        private final double[] ov = new double[2];
        public boolean eof;

        public DataClip(final InputStream clip, final double vol, final double sp) {
            this.clip = clip;
            this.vol = vol;
            this.sp = sp;
        }

        public DataClip(final InputStream clip) {
            this(clip, 1.0, 1.0);
        }

        public void finwait() throws InterruptedException {
            synchronized (this) {
                if (eof)
                    return;
                wait();
            }
        }

        public boolean get(final double[] sm) {
            try {
                ack += 44100.0 * sp;
                while (ack >= 44100) {
                    for (int i = 0; i < 2; i++) {
                        final int b1 = clip.read();
                        final int b2 = clip.read();
                        if ((b1 < 0) || (b2 < 0)) {
                            synchronized (this) {
                                eof = true;
                                notifyAll();
                            }
                            return (false);
                        }
                        int v = b1 + (b2 << 8);
                        if (v >= 32768)
                            v -= 65536;
                        ov[i] = ((double) v / 32768.0) * vol;
                    }
                    ack -= 44100;
                }
            } catch (java.io.IOException e) {
                synchronized (this) {
                    eof = true;
                    notifyAll();
                }
                return (false);
            }
            System.arraycopy(ov, 0, sm, 0, 2);
            return (true);
        }
    }

    private static class Player extends HackThread {
        private final Collection<CS> clips = new LinkedList<CS>();
        @SuppressWarnings({"UnusedDeclaration"})
        private int srate;
        private final int nch = 2;

        Player() {
            super("Haven audio player");
            setDaemon(true);
            srate = (int) fmt.getSampleRate();
        }

        private void fillbuf(final byte[] buf, int off, int len) {
            final double[] val = new double[nch];
            final double[] sm = new double[nch];
            while (len > 0) {
                for (int i = 0; i < nch; i++)
                    val[i] = 0;
                for (Iterator<CS> i = clips.iterator(); i.hasNext();) {
                    final CS cs = i.next();
                    if (!cs.get(sm)) {
                        i.remove();
                        continue;
                    }
                    for (int ch = 0; ch < nch; ch++)
                        val[ch] += sm[ch];
                }
                for (int i = 0; i < nch; i++) {
                    int iv = (int) (val[i] * volume * 32767.0);
                    if (iv < 0) {
                        if (iv < -32768)
                            iv = -32768;
                        iv += 65536;
                    } else {
                        if (iv > 32767)
                            iv = 32767;
                    }
                    buf[off++] = (byte) (iv & 0xff);
                    buf[off++] = (byte) ((iv & 0xff00) >> 8);
                    len -= 2;
                }
            }
        }

        public void run() {
            SourceDataLine line = null;
            try {
                try {
                    line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, fmt));
                    line.open(fmt, bufsize);
                    line.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                final byte[] buf = new byte[1024];
                //noinspection InfiniteLoopStatement
                while (true) {
                    if (Thread.interrupted())
                        throw (new InterruptedException());
                    synchronized (queuemon) {
                        final Collection<Runnable> queue = Audio.queue;
                        Audio.queue = new LinkedList<Runnable>();
                        for (final Runnable r : queue)
                            r.run();
                    }
                    synchronized (ncl) {
                        for (final CS cs : ncl)
                            clips.add(cs);
                        ncl.clear();
                    }
                    fillbuf(buf, 0, 1024);
                    //noinspection StatementWithEmptyBody
                    for (int off = 0; off < buf.length; off += line.write(buf, off, buf.length - off)) ;
                }
            } catch (InterruptedException ignored) {
            } finally {
                synchronized (Audio.class) {
                    player = null;
                }
                if (line != null)
                    line.close();
            }
        }
    }

    private static synchronized void ckpl() {
        if (enabled) {
            if (player == null) {
                player = new Player();
                player.start();
            }
        } else {
            ncl.clear();
        }
    }

    public static void play(final CS clip) {
        synchronized (ncl) {
            ncl.add(clip);
        }
        ckpl();
    }

    public static void play(final InputStream clip, final double vol, final double sp) {
        play(new DataClip(clip, vol, sp));
    }

    public static void play(final byte[] clip, final double vol, final double sp) {
        play(new DataClip(new java.io.ByteArrayInputStream(clip), vol, sp));
    }

    public static void play(final byte[] clip) {
        play(clip, CustomConfig.current().getSfxVol()/100.0D, 1.0);
    }

    public static void queue(final Runnable d) {
        synchronized (queuemon) {
            queue.add(d);
        }
        ckpl();
    }

    private static void playres(final Resource res) {
        final Collection<AudioL> clips = res.layers(Resource.audio);
        int s = (int) (Math.random() * clips.size());
        AudioL clip = null;
        for (final AudioL cp : clips) {
            clip = cp;
            if (--s < 0)
                break;
        }
        if (clip != null) {
            play(clip.clip);
        }
    }

    public static void play(final Resource clip) {
        queue(new Runnable() {
            public void run() {
                if (clip.loading.get())
                    queue.add(this);
                else
                    playres(clip);
            }
        });
    }

    public static void play(final Indir<Resource> clip) {
        queue(new Runnable() {
            public void run() {
                final Resource r = clip.get();
                if (r == null)
                    queue.add(this);
                else
                    playres(r);
            }
        });
    }

    public static byte[] readclip(final InputStream in) throws java.io.IOException {
        final AudioInputStream cs;
        try {
            cs = AudioSystem.getAudioInputStream(fmt, AudioSystem.getAudioInputStream(in));
        } catch (UnsupportedAudioFileException e) {
            throw (new java.io.IOException("Unsupported audio encoding"));
        }
        final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        final byte[] bbuf = new byte[65536];
        while (true) {
            final int rv = cs.read(bbuf);
            if (rv < 0)
                break;
            buf.write(bbuf, 0, rv);
        }
        return (buf.toByteArray());
    }

    public static void main(final String[] args) throws Exception {
        final Collection<DataClip> clips = new LinkedList<DataClip>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-b")) {
                bufsize = Integer.parseInt(args[++i]);
            } else {
                final DataClip c = new DataClip(new java.io.FileInputStream(args[i]));
                clips.add(c);
            }
        }
        for (final DataClip c : clips)
            play(c);
        for (final DataClip c : clips)
            c.finwait();
    }

    static {
        Console.setscmd("sfx", new Console.Command() {
            public void run(final Console cons, final String[] args) {
                play(Resource.load(args[1]));
            }
        });
        Console.setscmd("sfxvol", new Console.Command() {
            public void run(final Console cons, final String[] args) {
                setvolume(Double.parseDouble(args[1]));
            }
        });
    }
}
