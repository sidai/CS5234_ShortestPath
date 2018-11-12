package sssp;

import util.AdjListEntryManager;
import util.TournamentFileManager;
import vo.Neighbor;
import vo.TournamentNode;

import java.util.List;

public class CacheEfficientDijkstra {
    AdjListEntryManager adjListManager;

    public CacheEfficientDijkstra() throws Exception {
        TournamentFileManager.initialize();
        adjListManager = new AdjListEntryManager();
    }

    public void dijkstra(int src, int dest) throws Exception {
        TournamentFileManager.clearAll();
        adjListManager.clearAll();
        double currentDistance = 0.0;

        TournamentFileManager.updateDistance(src, src, currentDistance);

        while (true) {
            TournamentNode nextNode = TournamentFileManager.extractMinNode();
            if (nextNode == null) {
                System.out.println("Not reachable");
            }

            src = nextNode.getNodeId();
            currentDistance = nextNode.getDist();
            if (src == dest) {
                printResult();
                break;
            }

            List<Neighbor> neighbors = adjListManager.readAdjListEntry(src);

            for (Neighbor neighbor : neighbors) {
                TournamentFileManager.updateDistance(src, neighbor.getId(), currentDistance + neighbor.getDistance());
            }
        }
    }

    private void printResult() {
        System.out.println("Priority Queue Read:"+TournamentFileManager.IOEdgeReadCount+" Priority Queue Write:"+TournamentFileManager.IOEdgeWriteCount);
        System.out.println("Priority Queue Read:"+TournamentFileManager.IONodeReadCount+" Priority Queue Write:"+TournamentFileManager.IONodeWriteCount);
    }
}
