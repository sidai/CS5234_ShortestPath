package sssp;

import util.AdjListManager;
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
import java.util.List;

public class CacheEfficientDijkstra {
    PrintWriter pr;

    public CacheEfficientDijkstra() throws Exception {
        TournamentFileManager.initialize();
        String path = "./map-data/result/cache-eff.txt";
        Path pathToFile = Paths.get(path);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }
        pr = new PrintWriter(new BufferedWriter(new FileWriter(path)));
    }

    public void dijkstra(int src, int dest, boolean isDest, List<Integer> reportPoints) throws Exception {
        long start = System.currentTimeMillis();
        TournamentFileManager.clearAll();
        double currentDistance = 0.0;

        TournamentFileManager.updateDistance(src, src, currentDistance);

        List<TournamentNode> result = new ArrayList<>();
        while (true) {
            TournamentNode nextNode = TournamentFileManager.extractMinNode();
            if (nextNode == null) {
                pr.println("Not reachable");
            }

            src = nextNode.getNodeId();
            currentDistance = nextNode.getDist();
            result.add(new TournamentNode(src, currentDistance));
            if (src == dest && isDest) {
                break;
            }
            List<Neighbor> neighbors = AdjListManager.readAdjListEntry(src);
            for (Neighbor neighbor : neighbors) {
                TournamentFileManager.updateDistance(src, neighbor.getId(), currentDistance + neighbor.getDistance());
            }
            if(reportPoints.contains(result.size())) {
                pr.println("report at "+result.size()+"----------------------------------------");
                pr.println("Time Pass: " + (System.currentTimeMillis() - start));
                pr.println("Edge Priority Queue Read:"+TournamentFileManager.IOEdgeReadCount+" Edge Priority Queue Write:"+TournamentFileManager.IOEdgeWriteCount);
                pr.println("Node Priority Queue Read:"+TournamentFileManager.IONodeReadCount+" Node Priority Queue Write:"+TournamentFileManager.IONodeWriteCount);
            }
            if (result.size() == dest && !isDest) {
                break;
            }
            if (result.size() % 1000 == 0) {
                System.out.println("report at "+result.size()+"----------------------------------------");
                System.out.println("Time Pass: " + (System.currentTimeMillis() - start));
            }
        }
//        printNode();
        pr.println("-----------------------------------------------------------------------------------------");
        pr.println("Result count: " + result.size());
        pr.println("Node Operation: " + TournamentFileManager.nodePopCount + " " + TournamentFileManager.nodeUpdateCount + " " + TournamentFileManager.nodeDeleteCount);
        pr.println("Edge Operation: " + TournamentFileManager.edgePopCount + " " + TournamentFileManager.edgeUpdateCount + " " + TournamentFileManager.edgeDeleteCount);
        pr.println("Priority Queue Read:"+TournamentFileManager.IOEdgeReadCount+" Priority Queue Write:"+TournamentFileManager.IOEdgeWriteCount);
        pr.println("Priority Queue Read:"+TournamentFileManager.IONodeReadCount+" Priority Queue Write:"+TournamentFileManager.IONodeWriteCount);
        pr.println("Total time Pass: " + (System.currentTimeMillis() - start));

        for(TournamentNode node: result) {
            pr.println(node.getNodeId() + ", " + node.getDist());
        }

        pr.close();
    }

    public void printNode() {
        List<TournamentNode> nodes = new ArrayList<>(TournamentFileManager.nodeRoot.getElements());
        List<TournamentNode> nodesRef = new ArrayList<>(TournamentFileManager.nodeRoot.getElementsRef().values());
        pr.println("---------------------------------Node Elements: " + TournamentFileManager.nodeRoot.getElements().size() + "----------------------------------------------");
//        nodes.removeAll(nodesRef);
//        Collections.sort(nodes);
//        for(TournamentNode node: nodes) {
//            pr.println(node.toString());
//        }

        List<TournamentNode> maxNode = new ArrayList<>(TournamentFileManager.nodeRoot.getMaxElements());
        pr.println("---------------------------------Node Max Elements: " + TournamentFileManager.nodeRoot.getMaxElements().size() + "----------------------------------------------");
//        maxNode.removeAll(nodesRef);
//        Collections.sort(maxNode);
//        for(TournamentNode node: maxNode) {
//            pr.println(node.toString());
//        }

//        nodes = new ArrayList<>(TournamentFileManager.nodeRoot.getElements());
        pr.println("--------------------------------Node Elements Reference:  " + TournamentFileManager.nodeRoot.getElementsRef().size() + "-----------------------------------------");
//        nodesRef.removeAll(nodes);
//        Collections.sort(nodesRef);
//        for(TournamentNode node: nodesRef) {
//            pr.println(node.toString());
//        }


        pr.println("--------------------------------Node Elements Buffer:  " + TournamentFileManager.nodeRoot.getBuffer().size() + "-----------------------------------------");
//        List<OperationNode> opNodes = new ArrayList<>(TournamentFileManager.nodeRoot.getBuffer().values());
//        for(OperationNode op: opNodes) {
//            pr.println(op.toString());
//        }

        pr.println("---------------------------------Edge Elements: " + TournamentFileManager.edgeRoot.getElements().size() + "----------------------------------------------");
//        List<TournamentEdge> edges = new ArrayList<>(TournamentFileManager.edgeRoot.getElements());
//        Collections.sort(edges);
//        for(TournamentEdge edge: edges) {
//            pr.println(edge.toString());
//        }

        pr.println("---------------------------------Edge Max Elements: " + TournamentFileManager.edgeRoot.getMaxElements().size() + "----------------------------------------------");


        pr.println("--------------------------------Edge Elements Reference:  " + TournamentFileManager.edgeRoot.getElementsRef().size() + "-----------------------------------------");
//        edges = new ArrayList<>(TournamentFileManager.edgeRoot.getElementsRef().values());
//        Collections.sort(edges);
//        for(TournamentEdge edge: edges) {
//            pr.println(edge.toString());
//        }

        pr.println("--------------------------------Edge Elements Buffer:  " + TournamentFileManager.edgeRoot.getBuffer().size() + "-----------------------------------------");
//        List<OperationEdge> edgeOps = new ArrayList<>(TournamentFileManager.edgeRoot.getBuffer().values());
//        for(OperationEdge op: edgeOps) {
//            pr.println(op.toString());
//        }
    }
}
