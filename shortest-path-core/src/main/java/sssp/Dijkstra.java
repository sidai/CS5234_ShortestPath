package sssp;

import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;

class Node implements Comparator<Node>
{
    public int node;
    public double dist;

    public Node()
    {
    }

    public Node(int node, double dist)
    {
        this.node = node;
        this.dist = dist;
    }

    @Override
    public int compare(Node node1, Node node2)
    {
        if (node1.dist < node2.dist)
            return -1;
        if (node1.dist > node2.dist)
            return 1;
        return 0;
    }
}

class Dijkstra {
    IOProcesser iop;
    HashMap<Integer,Double> results;
    Dijkstra(){
        iop = new IOProcesser();
        results = new HashMap<>();
    }

    void dijkstra(int src)
    {
        iop.writeDistance(src,0);

        double currentDistance = iop.readDistance(src);

        PriorityQueue<Node> pq = new PriorityQueue<>();

        while(!pq.isEmpty()) {

            Map<Integer,Double> neighbors = iop.getNeighbors(src);

            for (Map.Entry<Integer, Double> entry : neighbors.entrySet()) {
                int node = entry.getKey();
                if(!iop.getVisited(node)) {
                    double distance = entry.getValue() + currentDistance;

                    double originalDistance = iop.readDistance(node);
                    if (distance > originalDistance) {
                        iop.writeDistance(node, distance);
                        pq.add(new Node(node, distance));
                    }
                }
            }

            Node nextNode = pq.remove();
            src = nextNode.node;
            currentDistance = nextNode.dist;
            results.put(src,currentDistance);
            iop.setVisited(src);
        }


    }

}
