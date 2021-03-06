package vo;

import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;

@Headers(sequence = {"pqIndex", "dist", "nodeId"})
public class PQNode implements Comparable<PQNode>
{
    public static int EMPTY_POINTER = -1;
    @Parsed
    private int nodeId;

    @Parsed
    private double dist;

    @Parsed(field = "pqIndex")
    private int pqIndex;

    public PQNode() {

    }

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

    public int getPqIndex() { return pqIndex; }

    public void setPqIndex(int pqIndex) {
        this.pqIndex = pqIndex;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }
}