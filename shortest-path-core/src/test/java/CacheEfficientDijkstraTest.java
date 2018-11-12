import org.junit.Test;
import sssp.CacheEfficientDijkstra;

public class CacheEfficientDijkstraTest {

    @Test
    public void testDijkstra() throws Exception {
        CacheEfficientDijkstra process = new CacheEfficientDijkstra();
        process.dijkstra(2, 1485271);
    }
}
