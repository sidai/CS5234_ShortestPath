package vo;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class Edge {

    @CsvBindByName (column = "from_node", required = true)
    @CsvBindByPosition(position = 0)
    private int fromNode;

    @CsvBindByName (column = "to_node", required = true)
    @CsvBindByPosition(position = 1)
    private int toNode;

    @CsvBindByName (column = "distance", required = true)
    @CsvBindByPosition(position = 2)
    private double distance;

    public Edge(int fromNode, int toNode, double distance) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.distance = distance;
    }

    public int getFromNode() {
        return fromNode;
    }

    public void setFromNode(int fromNode) {
        this.fromNode = fromNode;
    }

    public int getToNode() {
        return toNode;
    }

    public void setToNode(int toNode) {
        this.toNode = toNode;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
