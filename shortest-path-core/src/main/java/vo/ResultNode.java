package vo;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class ResultNode
{
    @CsvBindByName (column = "nodeId", required = true)
    @CsvBindByPosition(position = 0)
    private int nodeId;

    @CsvBindByName (column = "dist", required = true)
    @CsvBindByPosition(position = 1)
    private double dist;


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
        return nodeId;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }
}