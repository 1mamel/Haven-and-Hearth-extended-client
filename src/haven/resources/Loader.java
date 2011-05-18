package haven.resources;

import haven.HackThread;
import haven.util.PrioQueue;
import haven.resources.sources.ResSource;

import java.io.IOException;
import java.io.InputStream;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 18.05.11
 * Time: 19:09
 *
 * @author Vlad.Rassokhin@gmail.com
 */
class Loader implements Runnable {
    private ResSource src;
    private Loader next = null;
    private final PrioQueue<Resource> queue = new PrioQueue<Resource>();
    private transient Thread th = null;

    Loader(ResSource src) {
        this.src = src;
    }

    public void chain(Loader next) {
        this.next = next;
    }

    public void load(Resource res) {
        synchronized (queue) {
            queue.add(res);
            queue.notifyAll();
        }
        synchronized (Loader.this) {
            if (th == null) {
                th = new HackThread(Resource.loadergroup, Loader.this, "Haven resource loader");
                th.setDaemon(true);
                th.start();
            }
        }
    }

    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                Resource cur;
                synchronized (queue) {
                    while ((cur = queue.poll()) == null)
                        queue.wait();
                }
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (cur) {
                    handle(cur);
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            synchronized (Loader.this) {
                /* Yes, I know there's a race condition. */
                th = null;
            }
        }
    }

    private void handle(Resource res) {
        InputStream in = null;
        try {
            res.error = null;
            res.source = src;
            try {
                try {
                    in = src.get(res.name);
                    res.load(in);
                    res.loading = false;
                    res.notifyAll();
                } catch (IOException e) {
                    throw (new Resource.LoadException(e, res));
                }
            } catch (Resource.LoadException e) {
                if (next == null) {
                    res.error = e;
                    res.loading = false;
                    res.notifyAll();
                } else {
                    next.load(res);
                }
            } catch (RuntimeException e) {
                throw (new Resource.LoadException(e, res));
            }
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ignored) {
            }
        }
    }

    public Loader getNext() {
        return next;
    }

    public int queueSize() {
        return queue.size();
    }
}
