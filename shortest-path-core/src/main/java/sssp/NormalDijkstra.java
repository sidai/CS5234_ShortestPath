package sssp;

import java.util.List;
import util.ExternalResult;
import util.AdjListEntryManager;
import util.ExternalPriorityQueue;
import vo.Neighbor;
import vo.PQNode;




public class NormalDijkstra {

    long start = System.currentTimeMillis();
    AdjListEntryManager manager;
    ExternalResult result;
    ExternalPriorityQueue pq;
    public NormalDijkstra() throws Exception {
        manager = new AdjListEntryManager();
        pq = new ExternalPriorityQueue();
        result = new ExternalResult();
    }

    public void dijkstra(int src, int dest) throws Exception
    {
        result.clearAll();
        pq.clearAll();


        double currentDistance = 0;

        pq.insert(new PQNode(src,0));


        while(!pq.isEmpty()) {

            PQNode nextNode = pq.pop();
//            System.out.println("pop "+nextNode.getNodeId());
            if (nextNode == null) {
                System.out.println("Not reachable");
            }

            src = nextNode.getNodeId();
            currentDistance = nextNode.getDist();
            System.out.println(src + ", " + currentDistance);
            if(src == dest) {
                break;
            }
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
            if(result.resultCount%100==0){
//                System.out.println(result.resultCount+"______________");
//                System.out.println("Priority Queue Read:"+pq.IOReadCount+" Priority Queue Write:"+pq.IOWriteCount);
//                System.out.println("Result Read:"+result.IOReadCount+" Result Write:"+result.IOWriteCount);
//                System.out.println(pq.popTime+" "+pq.insertTime +" "+pq.updateTime+" "+pq.retrieveTime);
//                System.out.println(result.insertTime+" "+result.retrieveTime);
                System.out.println("Time Pass: " + (System.currentTimeMillis() - start));

            }
        }
        System.out.println("Priority Queue Read:"+pq.IOReadCount+" Priority Queue Write:"+pq.IOWriteCount);
        System.out.println("Result Read:"+result.IOReadCount+" Result Write:"+result.IOWriteCount);
        System.out.println("Total time Pass: " + (System.currentTimeMillis() - start));
    }
}
