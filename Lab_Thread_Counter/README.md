# Lab_Thread_Counter: Static vs Non-Static Counter in Multithreading

**Name:** __Jubayer ahmed__________  
**ID:** __IT-24027__________

## 1. Files
- `jubayer_thread.java`: Java source code (single class)
- `handwritten_code/`: scan/photo of the handwritten code
- `outputs/`: screenshots of the program output for each test case
- `Result_Table.pdf`: result tables
- `README.md`: this file

## 2. How to run
```
javac jubayer_thread.java
java jubayer_thread <threads> <increments> <true|false>
```
- `true`: thread-safe static counter (`AtomicLong`)
- `false`: unsynchronized static counter (`static long`)

Example: `java jubayer_thread 10 50000 false`

## 3. Inputs (test cases)

| Test | Threads | Increments per thread | Expected count |
|---|---|---|---|
| TC1 | 1 | 1,000 | 1,000 |
| TC2 | 2 | 10,000 | 20,000 |
| TC3 | 5 | 10,000 | 50,000 |
| TC4 | 10 | 50,000 | 500,000 |
| TC5 | 20 | 50,000 | 1,000,000 |
| TC6 | 50 | 50,000 | 2,500,000 |
| TC7 | 100 | 50,000 | 5,000,000 |

## 4. Results

Percentage difference = |static - non-static| / non-static x 100

### Experiment A: thread-safe (`true`)

| Threads | Expected | Static (AtomicLong) | Non-static total | Abs. difference | Difference (%) |
|---|---|---|---|---|---|
| 1 | 1,000 | 1,000 | 1,000 | 0 | 0% |
| 2 | 20,000 | 20,000 | 20,000 | 0 | 0% |
| 5 | 50,000 | 50,000 | 50,000 | 0 | 0% |

### Experiment B: unsynchronized (`false`)

| Threads | Run | Expected | Static (unsafe) | Non-static total | Abs. difference | Difference (%) |
|---|---|---|---|---|---|---|
| 1 | 1 | 1,000 | 1,000 | 1,000 | 0 | 0.0000% |
| 2 | 1 | 20,000 | 10,750 | 20,000 | 9,250 | 46.2500% |
| 2 | 2 | 20,000 | 13,042 | 20,000 | 6,958 | 34.7900% |
| 5 | 1 | 50,000 | 13,190 | 50,000 | 36,810 | 73.6200% |
| 10 | 1 | 500,000 | 127,602 | 500,000 | 372,398 | 74.4796% |
| 100 | 1 | 5,000,000 | 4,155,270 | 5,000,000 | 844,730 | 16.8946% |
| 100 | 2 | 5,000,000 | 3,734,523 | 5,000,000 | 1,265,477 | 25.3095% |

Average difference: 2 threads = 40.5200%, 100 threads = 21.1021%.

Screenshots of every run are in the `outputs/` folder.

## 5. Observation
- In the thread-safe experiment, the static count always equaled the non-static total, so the difference was 0%.
- In the unsynchronized experiment, with 1 thread the difference was 0%. With 2 or more threads, the static count was lower than expected in every run.
- The difference did not increase steadily with thread count. It was about 40% with 2 threads, about 74% with 5 and 10 threads, and about 21% with 100 threads.
- Runs with the same number of threads gave different results (2 threads: 46.25% and 34.79%; 100 threads: 16.89% and 25.31%).

## 6. Conclusion
Static vs non-static describes who owns a variable, not whether it is thread-safe. A static variable is shared by all threads, so concurrent updates need synchronization or atomic operations. The statement `count++` is three steps (read, add, write). When two threads read the same value, both write the same result and one increment is lost (a lost update). Each thread's own non-static counter is touched by only one thread, so it never loses updates. `AtomicLong` makes each increment atomic, so no update is lost.

The percentage difference is not guaranteed to grow with thread count. It depends on thread scheduling, number of CPU cores, and runtime conditions, so it changes from run to run.

## 7. Analysis answers
1. A static variable belongs to the class (one copy shared by all objects). A non-static variable belongs to an object (each object has its own copy).
2. Because a static variable belongs to the class, and all threads use the same class.
3. Each thread has its own `CounterTask` object, and the non-static counter lives inside that object.
4. `join()` makes the main thread wait until all threads finish. Without it, the totals could be read before the threads complete.
5. `count++` is not atomic. Two threads can read the same old value and write the same new value, so one increment is lost.
6. No. In my runs the difference was about 40% for 2 threads, about 74% for 5 and 10 threads, and about 21% for 100 threads. It depends on scheduling and hardware.
7. Thread scheduling and timing are different every run, so the number of lost updates is different.
8. `AtomicLong` performs each increment as one atomic operation, so no updates are lost and the static count equals the expected count.
9. Use one shared object and pass it to all threads, so its instance counter is shared. Also make the update thread-safe (use `AtomicLong` or `synchronized`).
