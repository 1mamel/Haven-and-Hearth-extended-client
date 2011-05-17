package haven;

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
    private class SimplePrioElement implements PrioQueue.Prioritized {
        int prior;

        public int getPriority() {
            return prior;
        }

        private SimplePrioElement(int prior) {
            this.prior = prior;
        }
    }

    public void testPriority() throws Exception {
        final int C = 5;

        PrioQueue<SimplePrioElement> pq = new PrioQueue<SimplePrioElement>();
        ArrayList<SimplePrioElement> l = new ArrayList<SimplePrioElement>(C);
        for (int i = 0; i < C; ++i) {
            SimplePrioElement e = new SimplePrioElement(1);
            l.add(e);
        }
        pq.addAll(l);
        for (int i = 0; i < C; ++i) {
            l.get(i).prior = i + 3;
        }

        for (int i = C - 1; i >= 0; --i) {
            assertEquals(i + 3, pq.poll().prior);
        }
    }
}
