package vo;

import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;

@Headers(sequence = {"id", "latitude", "longitude"})
public class Node {

    @Parsed(field = "id")
    private int nodeId;

    @Parsed
    private double latitude;

    @Parsed
    private double longitude;

    public Node() {

    }

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
