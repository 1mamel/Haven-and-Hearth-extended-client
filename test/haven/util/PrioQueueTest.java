package haven.util;

import haven.util.PrioQueue;
import haven.util.Prioritized;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * // TODO: write javadoc
 * Created by IntelliJ IDEA.
 * Date: 17.05.11
 * Time: 20:48
 *
 * @author Vlad.Rassokhin@gmail.com
 */
public class PrioQueueTest extends TestCase {

    public void testPriority() throws Exception {
        final int C = 5;

        PrioQueue<Prioritized> pq = new PrioQueue<Prioritized>();
        ArrayList<Prioritized> l = new ArrayList<Prioritized>();
        for (int i = 0; i < C; ++i) {
            Prioritized e = new Prioritized();
            e.setPriority(1);
            l.add(e);
        }
        pq.addAll(l);
        for (int i = 0; i < C; ++i) {
            l.get(i).setPriority(i + 3);
        }

        for (int i = C - 1; i >= 0; --i) {
            assertEquals(i + 3, pq.poll().getPriority());
//            System.out.println("pq.poll().getPriority() = " + pq.poll().getPriority());
        }
    }
}
