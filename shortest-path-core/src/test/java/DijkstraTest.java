import sssp.CacheEfficientDijkstra;
import sssp.InMemoryDijkstra;
import sssp.NormalDijkstra;
import util.AdjListManager;

public class DijkstraTest {
    public static void main(String[] args) throws Exception {
        DijkstraTest test = new DijkstraTest();
        String edgeCSV = "./map-data/sorted-graph/edge.csv";
        AdjListManager.loadFromFile(edgeCSV);
        test.testDijkstra();
//        test.testInMemoryDijkstra();
        test.testNormalDijkstra();
    }

    public void testDijkstra() throws Exception {
        String path = "./map-data/result/cache-eff.txt";
        CacheEfficientDijkstra process = new CacheEfficientDijkstra(path);
        process.dijkstra(2, 100000);
    }

    public void testNormalDijkstra() throws Exception {
        String path = "./map-data/result/normal.txt";
        NormalDijkstra process = new NormalDijkstra(path);
        process.dijkstra(2, 100000);
    }

    public void testInMemoryDijkstra() throws Exception {
        String path = "./map-data/result/in-memory.txt";
        InMemoryDijkstra process = new InMemoryDijkstra(path);
        process.dijkstra(2, 100000);
    }
}

