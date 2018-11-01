package vo;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class PQNode implements Comparable<PQNode>
{
    public static int EMPTY_POINTER = -1;
    @CsvBindByName (column = "nodeId", required = true)
    @CsvBindByPosition(position = 0)
    private int nodeId;

    @CsvBindByName (column = "dist", required = true)
    @CsvBindByPosition(position = 1)
    private double dist;

    @CsvBindByName (column = "pqIndex", required = true)
    @CsvBindByPosition(position = 2)
    private int pqIndex;

    public PQNode(int node, double dist)
    {
        this.nodeId = node;
        this.dist = dist;
    }
    public PQNode(int pqIndex, int node, double dist)
    {
        this.pqIndex = pqIndex;
        this.nodeId = node;
        this.dist = dist;
    }

    @Override
    public int compareTo(PQNode other)
    {
        if (this.dist < other.getDist())
            return -1;
        if (this.dist > other.getDist())
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!PQNode.class.isAssignableFrom(obj.getClass())){
            return false;
        }
        final PQNode other = (PQNode) obj;
        if(this.nodeId!=other.getNodeId()){
            return false;
        }
        return true;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getPqIndex() { return nodeId; }

    public void setPqIndex(int pqIndex) {
        this.pqIndex = pqIndex;
    }

    public double getDist() {
        return nodeId;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }
}