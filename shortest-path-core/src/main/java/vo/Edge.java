package vo;

import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;

@Headers(sequence = {"from_node", "to_node", "distance"})
public class Edge {

    @Parsed(field = "from_node")
    private int fromNode;

    @Parsed(field = "to_node")
    private int toNode;

    @Parsed
    private double distance;

    public Edge() {
    }

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
