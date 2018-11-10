package vo;

import com.univocity.parsers.annotations.EnumOptions;
import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.NullString;
import com.univocity.parsers.annotations.Parsed;

@Headers(sequence = {"operation", "id", "value"})
public class OperationNode {

    @Parsed
    @EnumOptions(customElement = "typeCode")
    private OpType operation;

    @Parsed
    private int id;

    @Parsed
    @NullString(nulls = {""})
    private Double value;

    public OperationNode(OpType operation, int id) {
        this.operation = operation;
        this.id = id;
        this.value = null;
    }

    public OperationNode(OpType operation, int id, double value) {
        this.operation = operation;
        this.id = id;
        this.value = value;
    }

    public OpType getOperation() {
        return operation;
    }

    public void setOperation(OpType operation) {
        this.operation = operation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
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
