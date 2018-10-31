package sssp;

import java.util.HashMap;

class IOProcesser {

    public boolean writeDistance(int node, double distance){
        return true;
    }

    public double readDistance(int node){
        return 8.0;
    }

    public boolean setVisited(int node){
        return true;
    }

    public boolean getVisited(int node){
        return false;
    }

    public HashMap<Integer, Double> getNeighbors(int node){
        HashMap<Integer, Double> map = new HashMap<>();
        map.put(4, 3.2);
        return map;
    }
}
