import java.util.concurrent.atomic.AtomicLong;

public class jubayer_thread {

    // Static counters: shared by ALL threads
    static final AtomicLong safeStatic = new AtomicLong(0); // Experiment A
    static long unsafeStatic = 0;                           // Experiment B

    // Each thread gets its own CounterTask object
    static class CounterTask implements Runnable {
        long nonStaticCount = 0;          // instance (non-static) counter
        final long increments;
        final boolean threadSafe;

        CounterTask(long increments, boolean threadSafe) {
            this.increments = increments;
            this.threadSafe = threadSafe;
        }

        public void run() {
            for (long i = 0; i < increments; i++) {
                nonStaticCount++;
                if (threadSafe) safeStatic.incrementAndGet();
                else unsafeStatic++;      // race condition
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.out.println("Usage: java jubayer_thread <threads> <increments> <true|false>");
            return;
        }
        int threads = Integer.parseInt(args[0]);
        long incs = Long.parseLong(args[1]);
        boolean safe = Boolean.parseBoolean(args[2]);

        CounterTask[] tasks = new CounterTask[threads];
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            tasks[i] = new CounterTask(incs, safe);
            ts[i] = new Thread(tasks[i]);
        }
        for (Thread t : ts) t.start();
        for (Thread t : ts) t.join();   // wait before reading totals

        long staticCount = safe ? safeStatic.get() : unsafeStatic;
        long nonStaticTotal = 0;
        for (CounterTask t : tasks) nonStaticTotal += t.nonStaticCount;

        long expected = threads * incs;
        long diff = Math.abs(staticCount - nonStaticTotal);
        double pct = nonStaticTotal == 0 ? (staticCount == 0 ? 0.0 : Double.NaN)
                : diff * 100.0 / nonStaticTotal;

        System.out.println("Mode                : " + (safe ? "Thread-safe (AtomicLong)" : "Unsynchronized (static long)"));
        System.out.println("Threads             : " + threads);
        System.out.println("Increments/thread   : " + incs);
        System.out.println("Expected count      : " + expected);
        System.out.println("Static count        : " + staticCount);
        System.out.println("Non-static total    : " + nonStaticTotal);
        System.out.println("Absolute difference : " + diff);
        System.out.printf ("Difference (%%)      : %.6f%%%n", pct);
    }
}