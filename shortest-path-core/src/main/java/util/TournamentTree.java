package util;

import javafx.collections.transformation.SortedList;
import vo.OperationNode;
import vo.TournamentNode;
import vo.OperationNode.OpType;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class TournamentTree {

    private SortedList<TournamentNode> elements;
    private Map<Integer, OperationNode> buffer;

    public TournamentNode extractMin() {
        TournamentNode root = elements.remove(0);
        if (elements.size() < ConfigManager.getMemorySize()/2) {
            fillup();
        }
        addDeleteOp(root.getNodeId());
        return root;
    }

    public void decreaseKey(int id, double dist) {
        addUpdateOp(id, dist);
    }

    public void executeOperation(OperationNode opNode) {
        boolean match = false;
        for(int i=0; i<elements.size(); i++) {
            TournamentNode node = elements.get(i);
            if (node.getNodeId() == opNode.getId()) {
                if (opNode.getOperation().equals(OpType.DELETE)) {
                    elements.remove(node);
                } else if (opNode.getOperation().equals(OpType.UPDATE)) {
                    if (node.getDist() > opNode.getValue()) {
                        node.setDist(opNode.getValue());
                    }
                }
            }
        }
    }

    private void executeDeleteOp(TournamentNode node) {
        elements.remove(node);
    }

    private void executeUpdateOp(TournamentNode node) {

    }

    private void addDeleteOp(int id) {
        // replace with DELETE operation
        buffer.put(id, new OperationNode(OpType.DELETE, id));
        if (buffer.size() == ConfigManager.getMemorySize()) {
            empty();
        }
    }

    private void addUpdateOp(int id, double dist) {
        if (buffer.containsKey(id)) {
            OperationNode op = buffer.get(id);
            // ignore when exists DELETE or UPDATE with smaller value
            if (op.getOperation().equals(OpType.UPDATE) && op.getValue() > dist) {
                op.setValue(dist);
            }
        } else {
            buffer.put(id, new OperationNode(OpType.UPDATE, id, dist));
            if (buffer.size() == ConfigManager.getMemorySize()) {
                empty();
            }
        }
    }

    private void empty() {

    }

    private void fillup() {

    }





}
