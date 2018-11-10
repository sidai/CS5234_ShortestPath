package vo;

import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;

@Headers(sequence = {"id", "neighbors"})
public class AdjListEntry {

    @Parsed(field = "id")
    private int nodeId;

    @Parsed(field = "neighbors")
    private String neighborString;

    public AdjListEntry() {
    }

    public AdjListEntry(int nodeId, String neighborString) {
        this.nodeId = nodeId;
        this.neighborString = neighborString;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getNeighborString() {
        return neighborString;
    }

    public void setNeighborString(String neighborString) {
        this.neighborString = neighborString;
    }
}
