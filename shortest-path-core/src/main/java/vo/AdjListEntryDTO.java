package vo;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class AdjListEntryDTO {

    @CsvBindByName(column = "id", required = true)
    @CsvBindByPosition(position = 0)
    private int nodeId;

    @CsvBindByName (column = "neighbors", required = true)
    @CsvBindByPosition(position = 1)
    private String neighborString;

    public AdjListEntryDTO(int nodeId, String neighborString) {
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
