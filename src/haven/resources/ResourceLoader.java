package haven.resources;

import haven.HackThread;
import haven.resources.sources.ResSource;
import haven.util.PrioQueue;

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
class ResourceLoader implements Runnable {
    private ResSource src;
    private ResourceLoader next = null;
    private final PrioQueue<Resource> queue = new PrioQueue<Resource>();
    private transient Thread th = null;

    ResourceLoader(ResSource src) {
        this.src = src;
        checkThread();
    }

    public void chain(ResourceLoader next) {
        this.next = next;
    }

    public void load(Resource res) {
        synchronized (queue) {
            queue.notifyAll();
            queue.add(res);
        }
        checkThread();
    }

    private void checkThread() {
        synchronized (this) {
            if (th == null) {
                th = new HackThread(Resource.loadergroup, ResourceLoader.this, "Haven resource loader");
                th.setDaemon(true);
                th.start();
            }
        }
    }

    public void run() {
        Resource cur = null;
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                synchronized (queue) {
                    while ((cur = queue.poll()) == null)
                        queue.wait();
                }
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (cur) {
                    try {
                        handle(cur);
                    } finally {
                        cur.notifyAll();
                    }
                }
                cur = null;
            }
        } catch (InterruptedException ignored) {
        } finally {
            if (cur != null) {
                synchronized (cur) {
                    cur.notifyAll();
                }
            }
            synchronized (this) {
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
                    res.loading.set(false);
                } catch (IOException e) {
                    throw (new Resource.LoadException(e, res));
                }
            } catch (Resource.LoadException e) {
                if (next == null) {
                    res.error = e;
                    res.loading.set(false);
                } else {
                    next.load(res);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                res.loading.set(false);
                res.error = new Resource.LoadException(e, res);
                throw (res.error);
            }
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ignored) {
            }
        }
    }

    public ResourceLoader getNext() {
        return next;
    }

    public int queueSize() {
        return queue.size();
    }

    public void wakeUpChain() {
        synchronized (queue) {
            queue.notifyAll();
        }
        if (next != null) next.wakeUpChain();
    }
}
