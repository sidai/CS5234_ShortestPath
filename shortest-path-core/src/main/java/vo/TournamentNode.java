package vo;

import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;

@Headers(sequence = {"id", "dist"})
public class TournamentNode {
    @Parsed(field = "id")
    private int nodeId;

    @Parsed
    private double dist;

    public TournamentNode() {

    }

    public TournamentNode(int node, double dist)
    {
        this.nodeId = node;
        this.dist = dist;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!TournamentNode.class.isAssignableFrom(obj.getClass())){
            return false;
        }
        final TournamentNode other = (TournamentNode) obj;
        if(this.nodeId != other.getNodeId()){
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

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public String[] getString() {
        return new String[]{String.valueOf(nodeId), String.valueOf(dist)};
    }

    @Override
    public String toString() {
        return String.valueOf(nodeId) + ", " + String.valueOf(dist);
    }
}