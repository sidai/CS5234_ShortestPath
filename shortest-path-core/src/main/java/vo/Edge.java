package vo;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class Edge {

    @CsvBindByName (column = "id", required = true)
    @CsvBindByPosition(position = 0)
    private int edgeId;

    @CsvBindByName (column = "from_node", required = true)
    @CsvBindByPosition(position = 1)
    private int fromNode;

    @CsvBindByName (column = "to_node", required = true)
    @CsvBindByPosition(position = 2)
    private int toNode;

    @CsvBindByName (column = "distance")
    @CsvBindByPosition(position = 3)
    private double distance;

    @CsvBindByName (column = "name")
    @CsvBindByPosition(position = 4)
    private String name;

    @CsvBindByName (column = "cost")
    @CsvBindByPosition(position = 5)
    private double cost;

    public Edge(int edgeId, int fromNode, int toNode) {
        this.edgeId = edgeId;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    public Edge(int edgeId, int fromNode, int toNode, double distance, String name) {
        this.edgeId = edgeId;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.distance = distance;
        this.name = name.replace(",", "-").replace("\"", "");
    }

    public int getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(int edgeId) {
        this.edgeId = edgeId;
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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
