import java.util.*;
import static java.lang.System.exit;

class Person implements Comparable<Person> {
    int priority;

    Person (int p) {
        priority = p;
    }

    @Override
    public int compareTo(Person person) {
        if (this.priority < person.priority)
            return -1;
        else if (this.priority == person.priority)
            return 0;
        return 1;
    }

    public boolean equals(Person p) {
        return this.priority == p.priority;
    }

    @Override
    public String toString() {
        return "Person{" +
                "priority=" + priority +
                '}';
    }
}

class QueueManager {
    private static final int MAX_QUEUE = 10000000;
    private static final int MIN_QUEUE = 10000;

    PriorityQueue<Person> q;
    Random r;
    Person you;
    boolean reversed = false;
    int ceiling;
    int negCeiling = -1;

    public QueueManager() {
        q = new PriorityQueue<Person>();
        r = new Random();

        this.ceiling = r.nextInt(MAX_QUEUE + 1 - MIN_QUEUE) + MIN_QUEUE;
        int i;
        for (i = 0; i < this.ceiling; i++) {
            q.add(new Person(i));
        }

        you = new Person(i);
        q.add(you);
        ceiling++;
    }

    public void reverseQueue() {
        q = reverseQ(q);
        reversed = !reversed;
    }

    public boolean isEmpty() {
        return getQ().isEmpty();
    }

    private static <E> PriorityQueue<E> reverseQ(PriorityQueue<E> q) {
        PriorityQueue<E> newQ = new PriorityQueue<>(Collections.reverseOrder());
        newQ.addAll(Arrays.asList((E[]) q.toArray()));
        return newQ;
    }

    public PriorityQueue<Person> getQ() {
        return q;
    }

    public void append() {
        if (reversed) {
            q.add(new Person(negCeiling));
            negCeiling--;
        } else {
            q.add(new Person(ceiling));
            ceiling++;
        }
        System.out.println("j: " + q.size());
    }
}

class CallCenter implements Runnable {
    public static final int MAX_WAIT = 4;
    public static final int MIN_WAIT = 2;
    QueueManager manager;
    Random r;
    public CallCenter(QueueManager q) {
        manager = q;
        r = new Random();
    }

    @Override
    public void run() {
        while (!manager.isEmpty()) {
            // wait for agent
            try {
                Thread.sleep((r.nextInt(MAX_WAIT) + MIN_WAIT) * 1000);
                //Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // get next caller, but check if they are you.
            if (manager.you.equals(manager.getQ().peek())) {
                System.out.println();
                System.out.println("How can I help you today?");
                exit(0);
            }

            manager.getQ().poll();
            System.out.println("h: " + manager.getQ().size());
        }
    }
}

class PeopleQueuer implements Runnable {
    // in milliseconds
    public static final int MAX_WAIT = 2000;
    public static final int MIN_WAIT = 500;
    QueueManager manager;
    Random r;

    public PeopleQueuer(QueueManager m) {
        manager = m;
        r = new Random();
    }

    @Override
    public void run() {
        // Add random people to queue
        while (!manager.isEmpty()) {
            try {
                Thread.sleep(r.nextInt(MAX_WAIT - 1 - MIN_WAIT) + MIN_WAIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            manager.append();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        QueueManager m = new QueueManager();
        CallCenter c = new CallCenter(m);
        PeopleQueuer queuer = new PeopleQueuer(m);

        Thread center = new Thread(c, "center");
        Thread pQueuer = new Thread(queuer, "queuer");

        center.start();
        pQueuer.start();

        System.out.println("Welcome to the Call Center!");
        System.out.printf("You are number %d in the queue.%n", m.you.priority);
        System.out.println("j: someone joined the queue, h: the next person is now being helped");
        System.out.println("To Reverse the Queue, Enter \"r\"");
        System.out.println();

        Scanner scnr = new Scanner(System.in);

        // player game loop
        while (!m.getQ().isEmpty()) {
            if (scnr.next().equals("r")) {
                System.out.println("Queue Reversed!");
                m.reverseQueue();
            }
        }
    }
}
