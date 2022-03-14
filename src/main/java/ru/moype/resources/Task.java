package ru.moype.resources;

import com.sun.xml.xsom.impl.scd.Iterators;

import java.util.ArrayList;
import java.util.HashSet;

public class Task {

    public int maxCost;
    //the actual cost of the task
    public int cost;
    //the cost of the task along the critical path
    public int criticalCost;
    //a name for the task for printing
    public String name;
    // the earliest start
    public int earlyStart;
    // the earliest finish
    public int earlyFinish;
    // the latest start
    public int latestStart;
    // the latest finish
    public int latestFinish;

    public int onCritical;
    //the tasks on which this task is dependant
    public HashSet<Task> dependencies; // = new HashSet<Task>();

    public Task(){

    }

    public Task(String name, int cost,  HashSet<Task> dependencies) {
        this.name = name;
        this.cost = cost;
        this.dependencies = dependencies;
        this.earlyFinish = -1;
    }

    @Override
    public String toString() {
        return name+": "+criticalCost;
    }

    public boolean isDependent(Task t){
        //is t a direct dependency?
        if(dependencies.contains(t)){
            return true;
        }
        //is t an indirect dependency
        for(Task dep : dependencies){
            if(dep.isDependent(t)){
                return true;
            }
        }
        return false;
    }

    public void setLatest() {
        latestStart = maxCost - criticalCost;
        latestFinish = latestStart + cost;
    }

    public void setMaxCost(int maxCost){
        this.maxCost = maxCost;
    }

    public void setCritical(int crit){
        this.onCritical = crit;
    }

    public String getName(){
        return name;
    }

    public int getEarlyStart(){
        return earlyStart;
    }

    public int getLatestStart(){
        return latestStart;
    }

    public int getOnCritical(){
        return onCritical;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getCost(){
        return cost;
    }

    public void setCost(int cost){
        this.cost = cost;
    }

    public void setDependencies(HashSet<Task> dep){
        this.dependencies = dep;
    }
}
