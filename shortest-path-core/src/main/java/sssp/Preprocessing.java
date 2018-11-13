package sssp;

import com.opencsv.CSVReader;
import util.AdjListEntryManager;
import util.EdgeUtil;
//import util.GraphUtil;
import util.GraphUtil;
import util.NodeUtil;
import vo.Neighbor;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Preprocessing {

    public static void main(String[] args) {
        Preprocessing preprocessing = new Preprocessing();
        preprocessing.run();
    }

    public void run() {
        try {
//            parseOSMMap();
            prepareEdgeList();
//            prepareAdjList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareAdjList() throws Exception {
        AdjListEntryManager manager = new AdjListEntryManager();
        String edgeCSV = "./map-data/sorted-graph/edge.csv";
        CSVReader reader = new CSVReader(new FileReader(edgeCSV));
        reader.readNext();
        System.out.println("Start");
        String [] lines; //skip header
        while ((lines = reader.readNext()) != null) {
            int from = Integer.valueOf(lines[0]);
            int to = Integer.valueOf(lines[1]);
            double dist = Double.valueOf(lines[2]);
            manager.addNeighbor(from, new Neighbor(to, dist));
        }
        manager.storeAllBlock();
        System.out.println(manager.getBlockMap().size());
    }

    private void prepareEdgeList() throws Exception {
        EdgeUtil edgeUtil = new EdgeUtil();
        String edgeCSV =  "./map-data/graph/sin/edge.csv";
//        String edgeStoreCSV = "./map-data/sorted-graph/sin/edge.csv";

        CSVReader reader = new CSVReader(new FileReader(edgeCSV));
        reader.readNext();
        System.out.println("Start");
        String [] lines; //skip header
        while ((lines = reader.readNext()) != null) {
//            int from = Integer.valueOf(lines[0]);
//            int to = Integer.valueOf(lines[1]);
//            double dist = Double.valueOf(lines[2]);

            edgeUtil.addEdge(0, 0, 0);
//            edgeUtil.addEdge(from, to, dist);
//            edgeUtil.addEdge(to, from, dist);
        }
//        edgeUtil.sort();
        System.out.println(edgeUtil.getEdgeSize());
//        edgeUtil.storeToCSV(edgeStoreCSV);
    }

    private void parseOSMMap() {
        String osmPath = "./map-data/osm/Singapore.osm.pbf";
        String hopperLocation = "./map-data/hopper/singapore";
        String nodeCSV =  "./map-data/graph/sin/node.csv";
        String edgeCSV =  "./map-data/graph/sin/edge.csv";
        GraphUtil graphUtil = getMapInfo(osmPath, hopperLocation);
        storeNodeEdgeInfo(graphUtil, nodeCSV, edgeCSV);
        System.out.println("Done with Singapore");
    }

    private void storeNodeEdgeInfo(GraphUtil graphUtil, String nodeCSV, String edgeCSV) {
        try {
            NodeUtil nodeUtil = graphUtil.getNodeUtil();
            nodeUtil.storeToCSV(nodeCSV);

            EdgeUtil edgeUtil = graphUtil.getEdgeUtil();
            edgeUtil.storeToCSV(edgeCSV);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GraphUtil getMapInfo(String osmPath, String hopperLocation) {
        GraphUtil graphUtil = new GraphUtil();
        graphUtil.loadAndStore(osmPath, hopperLocation);
        return graphUtil;
    }
}