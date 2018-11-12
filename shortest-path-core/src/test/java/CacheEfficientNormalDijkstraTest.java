import org.junit.Test;
import sssp.CacheEfficientDijkstra;
import sssp.NormalDijkstra;
import util.TournamentFileManager;

public class CacheEfficientNormalDijkstraTest {

    @Test
    public void testDijkstra() throws Exception {
        CacheEfficientDijkstra process = new CacheEfficientDijkstra();
        process.dijkstra(2, 2641782);
        System.out.println(TournamentFileManager.edgeRoot.getElements().size());
        System.out.println(TournamentFileManager.edgeRoot.getElementsRef().size());
        System.out.println(TournamentFileManager.edgeRoot.getBuffer().size());
    }

    @Test
    public void testNormalDijkstra() throws Exception {
        NormalDijkstra process = new NormalDijkstra();
        process.dijkstra(2, 2641782);
    }
}

