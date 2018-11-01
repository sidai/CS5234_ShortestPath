package util;

//import com.graphhopper.GraphHopper;
//import com.graphhopper.reader.osm.GraphHopperOSM;
//import com.graphhopper.routing.util.AllEdgesIterator;
//import com.graphhopper.routing.util.CarFlagEncoder;
//import com.graphhopper.routing.util.EncodingManager;
//import com.graphhopper.storage.Graph;
//import com.graphhopper.storage.NodeAccess;

public class GraphUtil {

//    private GraphHopper hopper;
    private EdgeUtil edgeUtil;
    private NodeUtil nodeUtil;

//    public GraphUtil() {
//        CarFlagEncoder encoder = new CarFlagEncoder();
//        hopper = new GraphHopperOSM();
//        hopper.setWayPointMaxDistance(0);
//        hopper.forDesktop();
//        hopper.setEncodingManager(new EncodingManager(encoder));
//        hopper.getCHFactoryDecorator().setDisablingAllowed(true);
//    }
//
//    public void loadAndStore(String osmPath, String hopperLocation) {
//        hopper.setDataReaderFile(osmPath);
//        hopper.setGraphHopperLocation(hopperLocation);
//        System.out.println("Start to load graph");
//        hopper.importOrLoad();
//        System.out.println("Load graph completed");
//        initGraph();
//    }
//
//    private void initGraph() {
//        nodeUtil = new NodeUtil();
//        System.out.println("Start to load nodes");
//        initNodeUtil();
//        System.out.println("Load nodes completed");
//
//        edgeUtil = new EdgeUtil();
//        System.out.println("Start to load edges");
//        initEdgeUtil();
//        System.out.println("Load edges completed");
//    }
//
//    public void initNodeUtil() {
//        Graph graph = hopper.getGraphHopperStorage();
//        NodeAccess nodeAccess = graph.getNodeAccess();
//
//        for(int nodeId = 0; nodeId<graph.getNodes(); nodeId++) {
//            nodeUtil.addNode(nodeId, nodeAccess.getLatitude(nodeId), nodeAccess.getLongitude(nodeId));
//        }
//
//        System.out.println("Node: " + graph.getNodes() + ", " + nodeUtil.getNodeSize());
//    }
//
//    public void initEdgeUtil() {
//        Graph graph = hopper.getGraphHopperStorage();
//        AllEdgesIterator iter = graph.getAllEdges();
//
//        while(iter.next()) {
//            edgeUtil.addEdge(iter.getBaseNode(), iter.getAdjNode(), iter.getDistance());
//        }
//        System.out.println("Edge: " + iter.length() + ", " + edgeUtil.getEdgeSize());
//    }

    public NodeUtil getNodeUtil() {
        return nodeUtil;
    }

    public EdgeUtil getEdgeUtil() {
        return edgeUtil;
    }
}
