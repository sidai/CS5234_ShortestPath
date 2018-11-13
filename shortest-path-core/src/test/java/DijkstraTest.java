import sssp.CacheEfficientDijkstra;
import sssp.InMemoryDijkstra;
import sssp.NormalDijkstra;
import util.AdjListManager;
import java.util.*;

public class DijkstraTest {

    public static void main(String[] args) throws Exception {
        DijkstraTest test = new DijkstraTest();
        String edgeCSV = "./map-data/sorted-graph/edge.csv";
        AdjListManager.loadFromFile(edgeCSV);
        test.testDijkstra();
//        test.testInMemoryDijkstra();
//        test.testNormalDijkstra();
    }

    public void testDijkstra() throws Exception {
        List<Integer> reportPoitns = new ArrayList<>();
        reportPoitns.add(1000);
        reportPoitns.add(5000);
        reportPoitns.add(10000);
        CacheEfficientDijkstra process = new CacheEfficientDijkstra();
        process.dijkstra(2, 100, false, reportPoitns);
    }


    public void testNormalDijkstra() throws Exception {
        List<Integer> reportPoitns = new ArrayList<>();
        reportPoitns.add(1000);
        reportPoitns.add(5000);
        reportPoitns.add(10000);
        NormalDijkstra process = new NormalDijkstra();
        process.dijkstra(2, 100, false, reportPoitns);
    }

    public void testInMemoryDijkstra() throws Exception {
        List<Integer> reportPoitns = new ArrayList<>();
        reportPoitns.add(1000);
        reportPoitns.add(5000);
        reportPoitns.add(10000);
        InMemoryDijkstra process = new InMemoryDijkstra();
        process.dijkstra(2, 100, false, reportPoitns);
    }
}

