package llnl.gnem.core.traveltime.Ak135;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import llnl.gnem.core.traveltime.SinglePhaseTraveltimeCalculator;

/**
 *
 * @author dodge1
 */
public class SinglePhaseCalculatorPool {

    private final ArrayBlockingQueue<SinglePhaseTraveltimeCalculator> pool;

    private SinglePhaseCalculatorPool(int capacity, String phase) throws IOException, ClassNotFoundException {
        pool = new ArrayBlockingQueue<>(capacity);
        TraveltimeCalculatorProducer pdl = TraveltimeCalculatorProducer.getInstance();

        for (int j = 0; j < capacity; ++j) {
            SinglePhaseTraveltimeCalculator sptt = pdl.getSinglePhaseTraveltimeCalculator(phase);
            pool.add(sptt);
        }
    }

    public SinglePhaseTraveltimeCalculator checkout() throws InterruptedException {
        return pool.take();
    }

    public void checkin(SinglePhaseAk135TraveltimeCalculator calculator) throws InterruptedException {
        pool.put(calculator);
    }

}
