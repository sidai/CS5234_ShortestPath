import org.junit.Test;
import sssp.CacheEfficientDijkstra;
import sssp.InMemoryDijkstra;
import sssp.NormalDijkstra;

public class DijkstraTest {

    @Test
    public void testDijkstra() throws Exception {
        CacheEfficientDijkstra process = new CacheEfficientDijkstra();
        process.dijkstra(2, 100000);
    }

    @Test
    public void testNormalDijkstra() throws Exception {
        NormalDijkstra process = new NormalDijkstra();
        process.dijkstra(2, 100000);
    }
    @Test
    public void testInMemoryDijkstra() throws Exception {
        InMemoryDijkstra process = new InMemoryDijkstra();
        process.dijkstra(2, 100000);
    }
}

