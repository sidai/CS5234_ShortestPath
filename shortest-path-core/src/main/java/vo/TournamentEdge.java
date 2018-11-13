package vo;

import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;

import java.util.Arrays;

@Headers(sequence = {"from_node", "to_node", "dist"})
public class TournamentEdge implements Comparable<TournamentEdge> {
    @Parsed(field = "from_node")
    private int fromNode;

    @Parsed(field = "to_node")
    private int toNode;

    @Parsed
    private double dist;

    public TournamentEdge() {

    }

    public TournamentEdge(int fromNode, int toNode, double dist)
    {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.dist = dist;
    }

    @Override
    public int compareTo(TournamentEdge other)
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
        if(!TournamentEdge.class.isAssignableFrom(obj.getClass())){
            return false;
        }
        final TournamentEdge other = (TournamentEdge) obj;
        return (this.fromNode == other.getFromNode() && this.toNode == other.getToNode());
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

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public String[] getString() {
        return new String[]{String.valueOf(fromNode), String.valueOf(toNode), String.valueOf(dist)};
    }

    @Override
    public String toString() {
        return String.valueOf(fromNode) + ", " + String.valueOf(toNode) + ", " + String.valueOf(dist);
    }
}