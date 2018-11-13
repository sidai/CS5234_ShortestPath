import sssp.CacheEfficientDijkstra;
import sssp.InMemoryDijkstra;
import sssp.NormalDijkstra;
import util.AdjListManager;

public class DijkstraTest {

    public static void main(String[] args) throws Exception {
        DijkstraTest test = new DijkstraTest();
        String edgeCSV = "./map-data/sorted-graph/edge.csv";
        AdjListManager.loadFromFile(edgeCSV);
//        test.testDijkstra();
        test.testInMemoryDijkstra();
//        test.testNormalDijkstra();
    }

    public void testDijkstra() throws Exception {
        CacheEfficientDijkstra process = new CacheEfficientDijkstra();
        process.dijkstra(2, 100000);
    }

    public void testNormalDijkstra() throws Exception {
        NormalDijkstra process = new NormalDijkstra();
        process.dijkstra(2, 100000);
    }

    public void testInMemoryDijkstra() throws Exception {
        InMemoryDijkstra process = new InMemoryDijkstra();
        process.dijkstra(2, 100000);
    }
}

