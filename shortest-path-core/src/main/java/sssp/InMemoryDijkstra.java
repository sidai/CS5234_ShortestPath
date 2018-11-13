package sssp;

import util.AdjListEntryManager;
import vo.Neighbor;
import vo.TournamentNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class InMemoryDijkstra {
    AdjListEntryManager adjListManager;
    PrintWriter pr;
    long start = System.currentTimeMillis();
    private static int NODE_SIZE = 2675656;
    public static int updateCount = 0;
    public static int popCount = 0;

    public InMemoryDijkstra() throws Exception {
        adjListManager = new AdjListEntryManager();
        String path = "./../map-data/result/in-memory.txt";
        Path pathToFile = Paths.get(path);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }
        pr = new PrintWriter(new BufferedWriter(new FileWriter(path)));
    }

    public void dijkstra(int src, int dest) throws Exception {
        PriorityQueue<TournamentNode> nodeQueue = new PriorityQueue<>();
        // maintain the current optimal cost of unsettled nodes
        Map<Integer, Double> costMap = new HashMap<>();
        boolean[] added = new boolean[NODE_SIZE];

        for (int i = 0; i < NODE_SIZE; i++) {
            costMap.put(i, Double.MAX_VALUE);
        }

        nodeQueue.add(new TournamentNode(src, 0.0));
        costMap.put(src, 0.0);

        List<TournamentNode> result = new ArrayList<>();

        while (!nodeQueue.isEmpty()) {
            TournamentNode nodeWithWeight = nodeQueue.poll();
            popCount++;
            int curr = nodeWithWeight.getNodeId();

            if (added[curr]) {
                continue;
            }

            double dist = nodeWithWeight.getDist();
            if (curr == dest) {
                break;
            }

            // curr is settled
            added[curr] = true;
            result.add(nodeWithWeight);

            if(result.size() == 4400) {
                break;
            }
            List<Neighbor> neighbors = adjListManager.readAdjListEntry(curr);

            for (Neighbor neighbor : neighbors) {
                int node = neighbor.getId();
                double newDist = dist + neighbor.getDistance();
                if (!added[node]) {
                    if (newDist < costMap.get(node)) {
                        updateCount++;
                        nodeQueue.add(new TournamentNode(node, newDist));
                        costMap.put(node, newDist);
                    }
                }
            }
        }
        pr.println("-----------------------------------------------------------------------------------------");
        pr.println("Result count: " + result.size());
        pr.println("Priority Queue Pop: " + popCount + " Priority Queue Update: " + updateCount);
        pr.println("Total time Pass: " + (System.currentTimeMillis() - start));

        for(TournamentNode node: result) {
            pr.println(node.getNodeId() + ", " + node.getDist());
        }

        pr.close();
    }


}
