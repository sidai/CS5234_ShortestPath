//package sssp;
//
//import util.AdjListEntryManager;
//import util.TournamentFileManager;
//import vo.TournamentEdge;
//import vo.TournamentNode;
//
//public class CacheEfficientDijkstra {
//    AdjListEntryManager adjListManager;
//
//    public CacheEfficientDijkstra() throws Exception {
//        TournamentFileManager.initialize();
//        adjListManager = new AdjListEntryManager();
//    }
//
//    void dijkstra(int src, int dest) throws Exception
//    {
//        TournamentFileManager.clearAll();
//        double currentDistance = 0.0;
//
//        TournamentFileManager.updateNodeDistance(src, currentDistance);
//
//        while(true) {
//            TournamentNode nextNode = TournamentFileManager.extractMinNode();
////            System.out.println("pop "+nextNode.getNodeId());
//            src = nextNode.getNodeId();
//            currentDistance = nextNode.getDist();
//
//            List<Neighbor> neighbors = manager.readAdjListEntry(src);
//
//            for (Neighbor neighbor : neighbors) {
////                System.out.println("neibhgor "+neighbor.getId());
//                double distance = neighbor.getDistance() + currentDistance;
//                PQNode pqnode = new PQNode(neighbor.getId(),distance);
//                //check node already in result files
////                System.out.println("get result "+result.retrieveCost(pqnode.getNodeId()));
//                if(result.retrieveCost(pqnode.getNodeId())==-1) {
//                    PQNode existNode = pq.retrieve(pqnode);
//                    if (existNode != null) {
//
//                        double originalDistance = existNode.getDist();
//                        if (distance < originalDistance) {
//                            existNode.setDist(distance);
//                            pq.update(existNode);
//                        }
//                    } else {
//                        pq.insert(pqnode);
//                    }
//                }
//            }
//            if(result.resultCount%100==0){
//                System.out.println(result.resultCount+"______________");
//                System.out.println("Priority Queue Read:"+pq.IOReadCount+" Priority Queue Write:"+pq.IOWriteCount);
//                System.out.println("Result Read:"+result.IOReadCount+" Result Write:"+result.IOWriteCount);
//                System.out.println(pq.popTime+" "+pq.insertTime +" "+pq.updateTime+" "+pq.retrieveTime);
//                System.out.println(result.insertTime+" "+result.retrieveTime);
//
//            }
//        }
//        System.out.println("Priority Queue Read:"+pq.IOReadCount+" Priority Queue Write:"+pq.IOWriteCount);
//        System.out.println("Result Read:"+result.IOReadCount+" Result Write:"+result.IOWriteCount);
//    }
//}
