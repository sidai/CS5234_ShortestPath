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
    Dijkstra() throws Exception{
        manager = new AdjListEntryManager();
        pq = new ExternalPriorityQueue();
        result = new ExternalResult();
    }

    void dijkstra(int src) throws Exception
    {

        double currentDistance = 0;

        pq.insert(new PQNode(src,0));


        while(!pq.isEmpty()) {

            List<Neighbor> neighbors = manager.readAdjListEntry(src);

            for (Neighbor neighbor : neighbors) {
                double distance = neighbor.getDistance() + currentDistance;
                PQNode pqnode = new PQNode(neighbor.getId(),distance);
                //check node already in result files
                if(result.retrieveCost(pqnode.getNodeId())==-1) {
                    PQNode existNode = pq.retrieve(pqnode);
                    if (existNode != null) {

                        double originalDistance = existNode.getDist();
                        if (distance < originalDistance) {

                            pq.update(pqnode);
                        }
                    } else {
                        pq.insert(pqnode);
                    }
                }
            }

            PQNode nextNode = pq.pop();
            src = nextNode.getNodeId();
            currentDistance = nextNode.getDist();
            result.insertResult(src, currentDistance);


        }


    }

}
