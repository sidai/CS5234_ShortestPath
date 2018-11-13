package sssp;

import javafx.util.Pair;
import util.AdjListEntryManager;
import util.TournamentFileManager;
import vo.Neighbor;
import vo.TournamentNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CacheEfficientDijkstra {
    AdjListEntryManager adjListManager;
    PrintWriter pr;
    long start = System.currentTimeMillis();

    public CacheEfficientDijkstra() throws Exception {
        TournamentFileManager.initialize();
        adjListManager = new AdjListEntryManager();
        String path = "./map-data/result/cache-eff.txt";
        Path pathToFile = Paths.get(path);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }
        pr = new PrintWriter(new BufferedWriter(new FileWriter(path)));
    }

    public void dijkstra(int src, int dest) throws Exception {
        TournamentFileManager.clearAll();
        double currentDistance = 0.0;

        TournamentFileManager.updateDistance(src, src, currentDistance);

        List<Pair> result = new ArrayList<>();
        int resultCount = 0;
        while (true) {
            TournamentNode nextNode = TournamentFileManager.extractMinNode();
            if (nextNode == null) {
                pr.println("Not reachable");
            }

            src = nextNode.getNodeId();
            currentDistance = nextNode.getDist();
            result.add(new Pair(src, currentDistance));
            if (src == dest) {
                break;
            }

            List<Neighbor> neighbors = adjListManager.readAdjListEntry(src);

            for (Neighbor neighbor : neighbors) {
                TournamentFileManager.updateDistance(src, neighbor.getId(), currentDistance + neighbor.getDistance());
            }
            resultCount++;
//            if(result.size() % 1000 == 0) {
//                System.out.println("Count: " + resultCount + ", Time Pass: " + (System.currentTimeMillis() - start));
            if(result.size() == 4000) {
                break;
            }
//            }
        }
//        printNode();
        pr.println("-----------------------------------------------------------------------------------------");
        pr.println("Result count: " + resultCount);
        pr.println("Node Operation: " + TournamentFileManager.nodePopCount + " " + TournamentFileManager.nodeUpdateCount + " " + TournamentFileManager.nodeDeleteCount);
        pr.println("Edge Operation: " + TournamentFileManager.edgePopCount + " " + TournamentFileManager.edgeUpdateCount + " " + TournamentFileManager.edgeDeleteCount);
        pr.println("Priority Queue Read:"+TournamentFileManager.IOEdgeReadCount+" Priority Queue Write:"+TournamentFileManager.IOEdgeWriteCount);
        pr.println("Priority Queue Read:"+TournamentFileManager.IONodeReadCount+" Priority Queue Write:"+TournamentFileManager.IONodeWriteCount);
        pr.println("Total time Pass: " + (System.currentTimeMillis() - start));

        for(Pair pair: result) {
            pr.println(pair.getKey() + ", " + pair.getValue());
        }

        pr.close();
    }

    public void printNode() {
        List<TournamentNode> nodes = new ArrayList<>(TournamentFileManager.nodeRoot.getElements());
        List<TournamentNode> nodesRef = new ArrayList<>(TournamentFileManager.nodeRoot.getElementsRef().values());
        pr.println("---------------------------------Elements: " + TournamentFileManager.nodeRoot.getElements().size() + "----------------------------------------------");
        nodes.removeAll(nodesRef);
        Collections.sort(nodes);
        for(TournamentNode node: nodes) {
            pr.println(node.toString());
        }

        nodes = new ArrayList<>(TournamentFileManager.nodeRoot.getElements());
        pr.println("--------------------------------Elements Reference:  " + TournamentFileManager.nodeRoot.getElementsRef().size() + "-----------------------------------------");
        nodesRef.removeAll(nodes);
        Collections.sort(nodesRef);
        for(TournamentNode node: nodesRef) {
            pr.println(node.toString());
        }


        pr.println("--------------------------------Elements Buffer:  " + TournamentFileManager.nodeRoot.getBuffer().size() + "-----------------------------------------");
//        List<OperationNode> opNodes = new ArrayList<>(TournamentFileManager.nodeRoot.getBuffer().values());
//        for(OperationNode op: opNodes) {
//            pr.println(op.toString());
//        }

        pr.println("---------------------------------Elements: " + TournamentFileManager.edgeRoot.getElements().size() + "----------------------------------------------");
//        List<TournamentEdge> edges = new ArrayList<>(TournamentFileManager.edgeRoot.getElements());
//        Collections.sort(edges);
//        for(TournamentEdge edge: edges) {
//            pr.println(edge.toString());
//        }

        pr.println("--------------------------------Elements Reference:  " + TournamentFileManager.edgeRoot.getElementsRef().size() + "-----------------------------------------");
//        edges = new ArrayList<>(TournamentFileManager.edgeRoot.getElementsRef().values());
//        Collections.sort(edges);
//        for(TournamentEdge edge: edges) {
//            pr.println(edge.toString());
//        }

        pr.println("--------------------------------Elements Buffer:  " + TournamentFileManager.edgeRoot.getBuffer().size() + "-----------------------------------------");
//        List<OperationEdge> edgeOps = new ArrayList<>(TournamentFileManager.edgeRoot.getBuffer().values());
//        for(OperationEdge op: edgeOps) {
//            pr.println(op.toString());
//        }
    }
}
