package sssp;

import util.AdjListManager;
import vo.Neighbor;
import vo.TournamentNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class InMemoryDijkstra {
    PrintWriter pr;
    private static int NODE_SIZE = 2675656;
    public static int updateCount = 0;
    public static int popCount = 0;

    public InMemoryDijkstra() throws Exception {
        String path = "./map-data/result/in-memory.txt";
        Path pathToFile = Paths.get(path);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }
        pr = new PrintWriter(new BufferedWriter(new FileWriter(path)));
    }

    public void dijkstra(int src, int dest, boolean isDest, List<Integer> reportPoints) throws Exception {
        long start = System.currentTimeMillis();
        PriorityQueue<TournamentNode> nodeQueue = new PriorityQueue<>(new Comparator<TournamentNode>() {
            @Override
            public int compare(TournamentNode o1, TournamentNode o2) {
                if (o1.getDist() < o2.getDist())
                    return -1;
                if (o1.getDist() > o2.getDist())
                    return 1;
                return 0;
            }
        });
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
            if (curr == dest && isDest) {
                break;
            }

            // curr is settled
            added[curr] = true;
            result.add(nodeWithWeight);

            if(result.size() == dest && !isDest) {
                break;
            }
            List<Neighbor> neighbors = AdjListManager.readAdjListEntry(curr);

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

            if(reportPoints.contains(result.size())) {
                pr.println("report at "+result.size()+"----------------------------------------");
                pr.println("Time Pass: " + (System.currentTimeMillis() - start));
                pr.println("Priority Queue Pop: " + popCount + " Priority Queue Update: " + updateCount);
            }

            if (result.size() % 1000 == 0) {
                System.out.println("report at "+result.size()+"----------------------------------------");
                System.out.println("Time Pass: " + (System.currentTimeMillis() - start));
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
