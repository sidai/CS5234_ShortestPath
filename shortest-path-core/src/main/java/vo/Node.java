package vo;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class Node {

    @CsvBindByName (column = "id", required = true)
    @CsvBindByPosition(position = 0)
    private int nodeId;

    @CsvBindByName (column = "latitude", required = true)
    @CsvBindByPosition(position = 1)
    private double latitude;

    @CsvBindByName (column = "longitude", required = true)
    @CsvBindByPosition(position = 2)
    private double longitude;

    public Node(int nodeId, double latitude, double longitude) {
        this.nodeId = nodeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
