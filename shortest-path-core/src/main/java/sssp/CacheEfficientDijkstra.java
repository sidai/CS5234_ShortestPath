package sssp;

import javafx.util.Pair;
import util.AdjListEntryManager;
import util.TournamentFileManager;
import vo.Neighbor;
import vo.TournamentNode;

import java.util.ArrayList;
import java.util.List;

public class CacheEfficientDijkstra {
    AdjListEntryManager adjListManager;
    long start = System.currentTimeMillis();

    public CacheEfficientDijkstra() throws Exception {
        TournamentFileManager.initialize();
        adjListManager = new AdjListEntryManager();
    }

    public void dijkstra(int src, int dest) throws Exception {
        TournamentFileManager.clearAll();
        double currentDistance = 0.0;

        TournamentFileManager.updateDistance(src, src, currentDistance);

        List<Pair> result = new ArrayList<>();
        int count = 0;
        while (true) {
            TournamentNode nextNode = TournamentFileManager.extractMinNode();
            if (nextNode == null) {
                System.out.println("Not reachable");
            }

            src = nextNode.getNodeId();
            currentDistance = nextNode.getDist();
            result.add(new Pair(src, currentDistance));
            System.out.println(src + ", " + currentDistance);
            if (src == dest) {
                System.out.println("total node: " + count);
                printResult();
                break;
            }

            List<Neighbor> neighbors = adjListManager.readAdjListEntry(src);

            for (Neighbor neighbor : neighbors) {
                TournamentFileManager.updateDistance(src, neighbor.getId(), currentDistance + neighbor.getDistance());
            }
            count++;
            if(count % 100 == 0) {
                System.out.println("Time Pass: " + (System.currentTimeMillis() - start));
            }
        }
    }

    private void printResult() {
        System.out.println("Priority Queue Read:"+TournamentFileManager.IOEdgeReadCount+" Priority Queue Write:"+TournamentFileManager.IOEdgeWriteCount);
        System.out.println("Priority Queue Read:"+TournamentFileManager.IONodeReadCount+" Priority Queue Write:"+TournamentFileManager.IONodeWriteCount);
        System.out.println("Total time Pass: " + (System.currentTimeMillis() - start));
    }
}
