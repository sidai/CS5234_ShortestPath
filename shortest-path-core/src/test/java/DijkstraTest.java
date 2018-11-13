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
//        test.testDijkstra();
//        test.testInMemoryDijkstra();

        test.testNormalDijkstraCache();
        test.testNormalDijkstra();
    }

    public void testDijkstra() throws Exception {
        List<Integer> reportPoitns = new ArrayList<>();
        reportPoitns.add(100);
        reportPoitns.add(200);
        reportPoitns.add(500);
        reportPoitns.add(1000);
        reportPoitns.add(2000);
        reportPoitns.add(4000);
        reportPoitns.add(6000);
        reportPoitns.add(8000);
        CacheEfficientDijkstra process = new CacheEfficientDijkstra();
        process.dijkstra(2, 340, false, reportPoitns);
    }


    public void testNormalDijkstra() throws Exception {
        List<Integer> reportPoitns = new ArrayList<>();
        reportPoitns.add(100);
        reportPoitns.add(200);
        reportPoitns.add(500);
        reportPoitns.add(1000);
        reportPoitns.add(2000);
        reportPoitns.add(4000);
        reportPoitns.add(6000);
        reportPoitns.add(8000);
        NormalDijkstra process = new NormalDijkstra();
        process.dijkstra(false,2, 10000, false, reportPoitns);
    }
    public void testNormalDijkstraCache() throws Exception {
        List<Integer> reportPoitns = new ArrayList<>();
        reportPoitns.add(100);
        reportPoitns.add(200);
        reportPoitns.add(500);
        reportPoitns.add(1000);
        reportPoitns.add(2000);
        reportPoitns.add(4000);
        reportPoitns.add(6000);
        reportPoitns.add(8000);
        NormalDijkstra process = new NormalDijkstra();
        process.dijkstra(true,2, 10000, false, reportPoitns);
    }

    public void testInMemoryDijkstra() throws Exception {
        List<Integer> reportPoitns = new ArrayList<>();
        reportPoitns.add(100);
        reportPoitns.add(200);
        reportPoitns.add(500);
        reportPoitns.add(1000);
        reportPoitns.add(2000);
        reportPoitns.add(4000);
        reportPoitns.add(6000);
        reportPoitns.add(8000);
        InMemoryDijkstra process = new InMemoryDijkstra();
        process.dijkstra(2, 10000, false, reportPoitns);
    }
}

