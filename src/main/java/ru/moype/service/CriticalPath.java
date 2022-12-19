package ru.moype.service;

import ru.moype.model.Task;

import java.util.*;

public class CriticalPath {

    public static int maxCost;
    public static String format = "%1$-10s %2$-5s %3$-5s %4$-5s %5$-5s %6$-5s %7$-10s\n";
    public Task[] result;

    public CriticalPath(HashSet<Task> allTasks){
        this.result = criticalPath(allTasks);
    }

    public static Task[] criticalPath(Set<Task> tasks) {
        // tasks whose critical cost has been calculated
        HashSet<Task> completed = new HashSet<Task>();
        // tasks whose critical cost needs to be calculated
        HashSet<Task> remaining = new HashSet<Task>(tasks);

        // Backflow algorithm
        // while there are tasks whose critical cost isn't calculated.
        while (!remaining.isEmpty()) {
            boolean progress = false;

            // find a new task to calculate
            for (Iterator<Task> it = remaining.iterator(); it.hasNext();) {
                Task task = it.next();
                if (completed.containsAll(task.dependencies)) {
                    // all dependencies calculated, critical cost is max
                    // dependency
                    // critical cost, plus our cost
                    int critical = 0;
                    for (Task t : task.dependencies) {
                        if (t.criticalCost > critical) {
                            critical = t.criticalCost;
                        }
                    }
                    task.criticalCost = critical + task.cost;
                    // set task as calculated an remove
                    completed.add(task);
                    it.remove();
                    // note we are making progress
                    progress = true;
                }
            }
            // If we haven't made any progress then a cycle must exist in
            // the graph and we wont be able to calculate the critical path
            if (!progress)
                throw new RuntimeException("Cyclic dependency, algorithm stopped!");
        }

        // get the cost
        maxCost(tasks);
        HashSet<Task> initialNodes = initials(tasks);
        calculateEarly(initialNodes);

        // get the tasks
        Task[] ret = completed.toArray(new Task[0]);
        // create a priority list
        Arrays.sort(ret, new Comparator<Task>() {

            @Override
            public int compare(Task o1, Task o2) {
                return o1.name.compareTo(o2.name);
            }
        });

        for (Task t: ret){
            if (t.getLatestStart() == t.getEarlyStart()){
                t.setCritical(1);
            }
        }

        return ret;
    }

    public static void calculateEarly(HashSet<Task> initials) {
        for (Task initial : initials) {
            initial.earlyStart = 0;
            initial.earlyFinish = initial.cost;
            setEarly(initial);
        }
    }

    public static void setEarly(Task initial) {
        int completionTime = initial.earlyFinish;
        for (Task t : initial.dependencies) {
            if (completionTime >= t.earlyStart) {
                t.earlyStart = completionTime;
                t.earlyFinish = completionTime + t.cost;
            }
            setEarly(t);
        }
    }

    public static HashSet<Task> initials(Set<Task> tasks) {
        HashSet<Task> remaining = new HashSet<Task>(tasks);
        for (Task t : tasks) {
            for (Task td : t.dependencies) {
                remaining.remove(td);
            }
        }

        System.out.print("Initial nodes: ");
        for (Task t : remaining)
            System.out.print(t.name + " ");
        System.out.print("\n\n");
        return remaining;
    }

    public static void maxCost(Set<Task> tasks) {
        int max = -1;
        for (Task t : tasks) {
            if (t.criticalCost > max)
                max = t.criticalCost;
        }
        maxCost = max;
        System.out.println("Critical path length (cost): " + maxCost);
        for (Task t : tasks) {
            t.setMaxCost(maxCost);
            t.setLatest();
        }
    }
}
