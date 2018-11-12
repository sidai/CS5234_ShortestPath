import org.junit.Test;
import sssp.CacheEfficientDijkstra;
import sssp.NormalDijkstra;

public class CacheEfficientNormalDijkstraTest {

    @Test
    public void testDijkstra() throws Exception {
        CacheEfficientDijkstra process = new CacheEfficientDijkstra();
        process.dijkstra(2, 1);
    }

    @Test
    public void testNormalDijkstra() throws Exception {
        NormalDijkstra process = new NormalDijkstra();
        process.dijkstra(2, 2641782);
    }
}

