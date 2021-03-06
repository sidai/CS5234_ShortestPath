package vo;

import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;

@Headers(sequence = {"id", "dist"})
public class ResultNode
{
    @Parsed(field = "id")
    private int nodeId;

    @Parsed
    private double dist;

    public ResultNode() {

    }
    
    public ResultNode(int node, double dist)
    {
        this.nodeId = node;
        this.dist = dist;
    }


    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }
}