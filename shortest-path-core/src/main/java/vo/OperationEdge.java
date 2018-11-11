package vo;

import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;

@Headers(sequence = {"operation", "from_node, to_node", "value"})
public class OperationEdge {

    @Parsed
    @EnumOptions(customElement = "typeCode")
    private OpType operation;

    @Parsed(field = "from_node")
    private int fromNode;

    @Parsed(field = "to_node")
    private int toNode;

    @Parsed
    private Double value;

    public OperationEdge(OpType operation, int fromNode, int toNode) {
        this.operation = operation;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.value = 0.0;
    }

    public OperationEdge(OpType operation, int fromNode, int toNode, double value) {
        this.operation = operation;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.value = value;
    }

    public OpType getOperation() {
        return operation;
    }

    public void setOperation(OpType operation) {
        this.operation = operation;
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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public enum OpType {
        UPDATE('U'),
        DELETE('D');

        public final char typeCode;

        OpType(char typeCode) {
            this.typeCode = typeCode;
        }
    }
}
