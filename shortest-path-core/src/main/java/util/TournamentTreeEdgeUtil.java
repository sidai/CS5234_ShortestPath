package util;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import javafx.util.Pair;
import vo.OperationEdge;
import vo.OperationEdge.OpType;
import vo.TournamentEdge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class TournamentTreeEdgeUtil {
    private File file;
    private PriorityQueue<TournamentEdge> minElements;
    private PriorityQueue<TournamentEdge> maxElements;
    private Map<Pair<Integer, Integer>, TournamentEdge> elementsRef;
    private Map<Pair<Integer, Integer>, OperationEdge> buffer;
    private double minAmongChild;

    public TournamentTreeEdgeUtil(File file) {
        this.file = file;
        minElements = new PriorityQueue<>();
        maxElements = new PriorityQueue<>(new Comparator<TournamentEdge>() {
            @Override
            public int compare(TournamentEdge o1, TournamentEdge o2) {
                if (o2.getDist() < o1.getDist())
                    return -1;
                if (o2.getDist() > o1.getDist())
                    return 1;
                return 0;
            }
        });
        elementsRef = new HashMap<>();
        buffer = new HashMap<>();
        minAmongChild = Double.MAX_VALUE;
    }
    
    public void init() throws Exception {
        file.createNewFile();
    }

    public void resetMinAmongChild() {
        this.minAmongChild = maxElements.isEmpty() ? Double.MAX_VALUE : maxElements.peek().getDist();
    }

    public boolean isFull(){
        return minElements.size()>=ConfigManager.getMemorySize();
    }

    public void removeElement(Pair<Integer, Integer> key) {
        TournamentEdge edge = elementsRef.remove(key);
        minElements.remove(edge);
        maxElements.remove(edge);
    }

    public void addElement(int fromNode, int toNode, double dist) {
        TournamentEdge edge = new TournamentEdge(fromNode, toNode, dist);
        elementsRef.put(new Pair<>(fromNode, toNode), edge);
        minElements.add(edge);
        maxElements.add(edge);
    }

    public boolean addElement(TournamentEdge element){
        addElement(element.getFromNode(), element.getToNode(), element.getDist());
        return isFull();
    }

    public File getFile() {
        return file;
    }

    public Map<Pair<Integer, Integer>, TournamentEdge> getElementsRef() {
        return elementsRef;
    }

    public PriorityQueue<TournamentEdge> getElements() {
        return minElements;
    }

    public PriorityQueue<TournamentEdge> getMaxElements() {
        return maxElements;
    }

    public Map<Pair<Integer, Integer>, OperationEdge> getBuffer() {
        return buffer;
    }

    public void executeOp(OperationEdge op) throws Exception {
        if(OpType.DELETE.equals(op.getOperation())) {
            deleteElement(op.getFromNode(), op.getToNode());
        } else {
            updateDistance(op.getFromNode(), op.getToNode(), op.getValue());
        }
    }

    public void commitOp(OperationEdge op) throws Exception {
        Pair<Integer, Integer> key = new Pair<>(op.getFromNode(), op.getToNode());
        if (elementsRef.containsKey(key)) {
            if (OpType.DELETE.equals(op.getOperation())) {
                removeElement(key);
            } else if (OpType.UPDATE.equals(op.getOperation())) {
                TournamentEdge edge = elementsRef.get(key);
                if (edge.getDist() > op.getValue()) {
                    edge.setDist(op.getValue());
                }
            }
        } else {
            if (OpType.UPDATE.equals(op.getOperation())) {
                addElement(op.getFromNode(), op.getToNode(), op.getValue());
            }
        }
    }

    public void setBuffer(Map<Pair<Integer, Integer>, OperationEdge> buffer){
        this.buffer = buffer;
    }

    public TournamentEdge findMin() throws Exception{
        if (minElements.isEmpty()) {
            TournamentFileManager.fillup(this);
        }
        return minElements.isEmpty() ? null : minElements.peek();
    }

    // findMin must be called before any deleteMin so that the elements is loaded.
    public void deleteMin() throws Exception{
        TournamentEdge root = minElements.poll();
        elementsRef.remove(new Pair<>(root.getFromNode(), root.getToNode()));
        maxElements.remove(root);
        bufferDeleteOp(root.getFromNode(), root.getToNode());
    }

    public void updateDistance(int fromNode, int toNode, double dist) throws Exception{
        // elements contains the node, replace if dist decreases
        // in this case there is never a operation of this node in the buffer
        Pair<Integer, Integer> key = new Pair<>(fromNode, toNode);
        if (elementsRef.containsKey(key)) {
            TournamentEdge duplicate = elementsRef.get(key);
            if(duplicate.getDist() > dist) {
                duplicate.setDist(dist);
            }
            // in other case discards the operation since it is larger
        }

        //elements doesn't contain the node, check if need to insert into elements.
        else {
            if (buffer.containsKey(key)) {
                OperationEdge node = buffer.get(key);
                //ignore since it has been removed.
                if (OpType.DELETE.equals(node.getOperation())) {
                    return;
                } else {
                    //need to update value
                    if(node.getValue() > dist) {
                        //directly insert and discard the old update
                        if (dist < minAmongChild) {
                            buffer.remove(key);
                            addElement(fromNode, toNode, dist);
                            if (minElements.size() > ConfigManager.getMemorySize()) {
                                TournamentEdge toBuffer = maxElements.peek();
                                removeElement(key);
                                bufferUpdateOp(toBuffer.getFromNode(), toBuffer.getToNode(), toBuffer.getDist());
                            }
                        } else {
                            node.setValue(dist);
                        }
                    }
                    // in other case discards the operation since it is larger
                }
            } else {
                if (minAmongChild >= dist) {
                    addElement(fromNode, toNode, dist);
                    if (minElements.size() > ConfigManager.getMemorySize()) {
                        TournamentEdge toBuffer = maxElements.peek();
                        removeElement(key);
                        bufferUpdateOp(toBuffer.getFromNode(), toBuffer.getToNode(), toBuffer.getDist());
                    }
                } else {
                    bufferUpdateOp(fromNode, toNode, dist);
                }
            }
        }
    }

    private void bufferUpdateOp(int fromNode, int toNode, double dist) throws Exception{
        Pair<Integer, Integer> key = new Pair<>(fromNode, toNode);
        if (buffer.containsKey(key)) {
            throw new RuntimeException("Must ensure there is no operation associate for the edge before insert a new update operation");
        }
        buffer.put(key, new OperationEdge(OpType.UPDATE, fromNode, toNode, dist));
        if (buffer.size() == ConfigManager.getMemorySize()) {
            TournamentFileManager.fillup(this);
        }
    }

    public void deleteElement(int fromNode, int toNode) throws Exception{
        Pair<Integer, Integer> key = new Pair<>(fromNode, toNode);
        if (elementsRef.containsKey(key)) {
            removeElement(key);
        }
        bufferDeleteOp(fromNode, toNode);
    }


    private void bufferDeleteOp(int fromNode, int toNode) throws Exception{
        // replace with DELETE operation
        buffer.put(new Pair<>(fromNode, toNode), new OperationEdge(OpType.DELETE, fromNode, toNode));
        if (buffer.size() == ConfigManager.getMemorySize()) {
            TournamentFileManager.fillup(this);
        }
    }

    public void storeToFile() throws IOException {
        try (Writer writer = new BufferedWriter((new FileWriter(file)))) {
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setQuoteAllFields(true);
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            csvWriter.writeRow(String.valueOf(minElements.size()), String.valueOf(buffer.size()), String.valueOf(minAmongChild));

            for(TournamentEdge node: minElements) {
                csvWriter.writeRow(node.getString());
            }

            for(OperationEdge node: buffer.values()) {
                csvWriter.writeRow(node.getString());
            }
        }
    }

    public void readFromFile() throws IOException {
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            CsvParser parser = new CsvParser(new CsvParserSettings());
            parser.beginParsing(reader);
            String[] count = parser.parseNext();

            minAmongChild = Double.parseDouble(count[2]);

            String[] nodeString;
            List<TournamentEdge> elements = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(count[0]); i++) {
                nodeString = parser.parseNext();
                int fromNode = Integer.parseInt(nodeString[0]);
                int toNode = Integer.parseInt(nodeString[1]);
                double dist = Double.parseDouble(nodeString[2]);
                TournamentEdge node = new TournamentEdge(fromNode, toNode, dist);
                elements.add(node);
                elementsRef.put(new Pair<>(fromNode, toNode), node);
            }
            this.minElements.addAll(elements);
            this.maxElements.addAll(elements);

            for (int i = 0; i < Integer.parseInt(count[1]); i++) {
                nodeString = parser.parseNext();
                OpType type = OpType.valueOf(nodeString[0]);
                int fromNode = Integer.parseInt(nodeString[1]);
                int toNode = Integer.parseInt(nodeString[2]);
                double dist = Double.parseDouble(nodeString[3]);
                OperationEdge node = new OperationEdge(type, fromNode, toNode, dist);

                buffer.put(new Pair<>(fromNode, toNode), node);
            }
        }
    }
}
