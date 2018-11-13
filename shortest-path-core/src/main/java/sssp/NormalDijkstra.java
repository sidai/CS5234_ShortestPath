package sssp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import util.AdjListManager;
import util.ExternalResult;
import util.ExternalPriorityQueue;
import vo.Neighbor;
import vo.PQNode;
import util.Pair;


public class NormalDijkstra {

    long start = System.currentTimeMillis();
    PrintWriter pr;
    ExternalResult result;
    ExternalPriorityQueue pq;
    public NormalDijkstra() throws Exception {
        pq = new ExternalPriorityQueue();
        result = new ExternalResult();
        String path = "./map-data/result/normal.txt";
        Path pathToFile = Paths.get(path);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }
        pr = new PrintWriter(new BufferedWriter(new FileWriter(path)));
    }

    public void dijkstra(int src, int dest, boolean isDest, List<Integer> reportPoints) throws Exception
    {

        List<Pair<Integer, Double>> resultInMemory = new LinkedList<>();
        result.clearAll();
        pq.clearAll();


        double currentDistance = 0;

        pq.insert(new PQNode(src,0));


        while(!pq.isEmpty()) {

            PQNode nextNode = pq.pop();
//            pr.println("pop "+nextNode.getNodeId());
            if (nextNode == null) {
                pr.println("Not reachable");
            }

            src = nextNode.getNodeId();
            currentDistance = nextNode.getDist();
            //pr.println(src + ", " + currentDistance);
            resultInMemory.add(new Pair(src, currentDistance));
            if(src == dest && isDest) {
                break;
            }
            result.insertResult(src, currentDistance);

            List<Neighbor> neighbors = AdjListManager.readAdjListEntry(src);

            for (Neighbor neighbor : neighbors) {
//                pr.println("neibhgor "+neighbor.getId());
                double distance = neighbor.getDistance() + currentDistance;
                PQNode pqnode = new PQNode(neighbor.getId(),distance);
                //check node already in result files
//                pr.println("get result "+result.retrieveCost(pqnode.getNodeId()));
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
            if(reportPoints.contains(result.resultCount)){
//                pr.println(result.resultCount+"______________");
//                pr.println("Priority Queue Read:"+pq.IOReadCount+" Priority Queue Write:"+pq.IOWriteCount);
//                pr.println("Result Read:"+result.IOReadCount+" Result Write:"+result.IOWriteCount);
//                pr.println(pq.popTime+" "+pq.insertTime +" "+pq.updateTime+" "+pq.retrieveTime);
//                pr.println(result.insertTime+" "+result.retrieveTime);
                System.out.println("report at "+result.resultCount+"----------------------------------------");
                System.out.println("Time Pass: " + (System.currentTimeMillis() - start));
                System.out.println("Priority Queue Read:"+pq.IOReadCount+" Priority Queue Write:"+pq.IOWriteCount);
                System.out.println("Result Read:"+result.IOReadCount+" Result Write:"+result.IOWriteCount);

            }
            if(result.resultCount == dest && !isDest) {
                break;
            }
        }
        pr.println("-----------------------------------------------------------------------------------------");
        pr.println("Result count: " + result.resultCount);
        pr.println("PQOperation: " + pq.popTime+" "+pq.insertTime +" "+pq.updateTime+" "+pq.retrieveTime);
        pr.println("Priority Queue Read:"+pq.IOReadCount+" Priority Queue Write:"+pq.IOWriteCount);
        pr.println("Result Read:"+result.IOReadCount+" Result Write:"+result.IOWriteCount);
        pr.println("Total time Pass: " + (System.currentTimeMillis() - start));

        for(Pair p: resultInMemory) {
            pr.println(p.getKey() + ", " + p.getValue());
        }
        pr.close();
    }
}
