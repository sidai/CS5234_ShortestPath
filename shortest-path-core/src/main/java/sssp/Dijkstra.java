package sssp;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.PriorityQueue;
import util.ExternalResult;
import util.AdjListEntryManager;
import util.ExternalPriorityQueue;
import vo.Neighbor;
import vo.PQNode;




class Dijkstra {
    AdjListEntryManager manager;
    ExternalResult result;
    ExternalPriorityQueue pq;
    Dijkstra() throws Exception {
        manager = new AdjListEntryManager();
        pq = new ExternalPriorityQueue();
        result = new ExternalResult();
    }

    void dijkstra(int src) throws Exception
    {

        result.clearAll();
        pq.clearAll();


        double currentDistance = 0;

        pq.insert(new PQNode(src,0));


        while(!pq.isEmpty()) {

            PQNode nextNode = pq.pop();
//            System.out.println("pop "+nextNode.getNodeId());
            src = nextNode.getNodeId();
            currentDistance = nextNode.getDist();
            result.insertResult(src, currentDistance);

            List<Neighbor> neighbors = manager.readAdjListEntry(src);

            for (Neighbor neighbor : neighbors) {
//                System.out.println("neibhgor "+neighbor.getId());
                double distance = neighbor.getDistance() + currentDistance;
                PQNode pqnode = new PQNode(neighbor.getId(),distance);
                //check node already in result files
//                System.out.println("get result "+result.retrieveCost(pqnode.getNodeId()));
                if(result.retrieveCost(pqnode.getNodeId())==-1) {
                    PQNode existNode = pq.retrieve(pqnode);
                    if (existNode != null) {

                        double originalDistance = existNode.getDist();
                        if (distance < originalDistance) {
                            existNode.setDist(distance);
                            pq.update(existNode);
                        }
                    } else {
                        pq.insert(pqnode);
                    }
                }
            }
//            System.out.println("done neighbor");
        }
        System.out.println("Priority Queue Read:"+pq.IOReadCount+" Priority Queue Write:"+pq.IOWriteCount);
        System.out.println("Result Read:"+result.IOReadCount+" Result Write:"+result.IOWriteCount);
    }
}
