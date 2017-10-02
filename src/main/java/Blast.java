
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 'Blast' a resource with many concurrent threads simultaneously, synchronized
 * to the same line of code.
 *
 * @author Jacob
 */
public class Blast {

	/* number of iterations to apply */
	private static final int ITERATIONS = 100;
	/* number of concurrent threads to use */
	private static final int THREADS = 10;

	public static void main(String[] args) throws InterruptedException {
		for (int iteration = 0; iteration < ITERATIONS; iteration++) {

			// latch to synchronize when all threads have initialized
			CountDownLatch threadsLatch = new CountDownLatch(THREADS);
			// latch to synchronize when
			CountDownLatch iterationLatch = new CountDownLatch(1);

			for (int thread = 0; thread < THREADS; thread++) {
				new Thread(() -> {

					// initialize thread resources...
					
					// register all threads
					threadsLatch.countDown();
					try {
						// wait for iteration to be synchronized
						iterationLatch.await();
					} catch (InterruptedException ex) {
						Logger.getLogger(Blast.class.getName()).log(Level.SEVERE, "Error awaiting iteration latch", ex);
						Thread.currentThread().interrupt();
					}

					// perform operation, which will be 'blasted' by many concurrent threads...
					// e.g. call SQL statement that is prone to deadlock during heavy usage, etc.
				}).start();
			}

			// wait for all threads to be synchronized
			threadsLatch.await();
			// initiate the blast
			iterationLatch.countDown();
		}
	}
}
