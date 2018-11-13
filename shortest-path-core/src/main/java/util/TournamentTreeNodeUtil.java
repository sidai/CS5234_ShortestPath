package util;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import vo.OperationNode;
import vo.TournamentNode;
import vo.OperationNode.OpType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

public class TournamentTreeNodeUtil {
    private File file;
    private PriorityQueue<TournamentNode> minElements;
    private PriorityQueue<TournamentNode> maxElements;
    private Map<Integer, TournamentNode> elementsRef;
    private Map<Integer, OperationNode> buffer;
    private double minAmongChild;

    public TournamentTreeNodeUtil(File file) {
        this.file = file;
        minElements = new PriorityQueue<>(new Comparator<TournamentNode>() {
            @Override
            public int compare(TournamentNode o1, TournamentNode o2) {
                if (o1.getDist() < o2.getDist())
                    return -1;
                if (o1.getDist() > o2.getDist())
                    return 1;
                return 0;
            }
        });
        maxElements = new PriorityQueue<>(new Comparator<TournamentNode>() {
            @Override
            public int compare(TournamentNode o1, TournamentNode o2) {
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

    public void executeOp(OperationNode op) throws Exception {
        if(OpType.DELETE.equals(op.getOperation())) {
            deleteElement(op.getId());
        } else {
            updateDistance(op.getId(), op.getValue());
        }
    }

    public void removeElement(int id) {
        TournamentNode node = elementsRef.remove(id);
        minElements.remove(node);
        maxElements.remove(node);
    }

    public void addElement(int id, double dist) {
        TournamentNode node = new TournamentNode(id, dist);
        elementsRef.put(id, node);
        minElements.add(node);
        maxElements.add(node);
    }

    public void commitOp(OperationNode op) throws Exception {
        if (elementsRef.containsKey(op.getId())) {
            if (OpType.DELETE.equals(op.getOperation())) {
                removeElement(op.getId());
            } else if (OpType.UPDATE.equals(op.getOperation())) {
                TournamentNode node = elementsRef.get(op.getId());
                if (node.getDist() > op.getValue()) {
                    removeElement(node.getNodeId());
                    addElement(node.getNodeId(), node.getDist());
                }
            }
        } else {
            if (OpType.UPDATE.equals(op.getOperation())) {
                addElement(op.getId(), op.getValue());
            }
        }
    }

    public boolean isFull(){
        return minElements.size()>=ConfigManager.getMemorySize();
    }

    public boolean addElement(TournamentNode element) {
        addElement(element.getNodeId(), element.getDist());
        return isFull();
    }

    public File getFile() {
        return file;
    }

    public Map<Integer, TournamentNode> getElementsRef() {
        return elementsRef;
    }

    public PriorityQueue<TournamentNode> getElements() {
        return minElements;
    }

    public PriorityQueue<TournamentNode> getMaxElements() {
        return maxElements;
    }

    public Map<Integer, OperationNode> getBuffer() {
        return buffer;
    }

    public TournamentNode findMin() throws Exception{
        if (minElements.isEmpty()) {
            TournamentFileManager.fillup(this);
        }
        return minElements.isEmpty() ? null : minElements.peek();
    }

    // findMin must be called before any deleteMin so that the elements is loaded.
    public void deleteMin() throws Exception{
        TournamentNode root = minElements.poll();
        elementsRef.remove(root.getNodeId());
        maxElements.remove(root);
        bufferDeleteOp(root.getNodeId());
    }

    public void updateDistance(int id, double dist) throws Exception{
        // elements contains the node, replace if dist decreases
        // in this case there is never a operation of this node in the buffer
        if (elementsRef.containsKey(id)) {
            TournamentNode duplicate = elementsRef.get(id);
            if(duplicate.getDist() > dist) {
                removeElement(id);
                addElement(id, dist);
            }
            // in other case discards the operation since it is larger
        }

        //elements doesn't contain the node, check if need to insert into elements.
        else {
            if (buffer.containsKey(id)) {
                OperationNode node = buffer.get(id);
                //ignore since it has been removed.
                if (OpType.DELETE.equals(node.getOperation())) {
                    return;
                } else {
                    //need to update value
                    if(node.getValue() > dist) {
                        //directly insert and discard the old update
                        if (dist < minAmongChild) {
                            buffer.remove(id);
                            addElement(id, dist);
                            if (minElements.size() > ConfigManager.getMemorySize()) {
                                TournamentNode toBuffer = maxElements.peek();
                                removeElement(toBuffer.getNodeId());
                                bufferUpdateOp(toBuffer.getNodeId(), toBuffer.getDist());
                            }
                        } else {
                            node.setValue(dist);
                        }
                    }
                    // in other case discards the operation since it is larger
                }
            } else {
                if (minAmongChild >= dist) {
                    addElement(id, dist);
                    if (minElements.size() > ConfigManager.getMemorySize()) {
                        TournamentNode toBuffer = maxElements.peek();
                        removeElement(toBuffer.getNodeId());
                        bufferUpdateOp(toBuffer.getNodeId(), toBuffer.getDist());
                    }
                } else {
                    bufferUpdateOp(id, dist);
                }
            }
        }
    }

    private void bufferUpdateOp(int id, double dist) throws Exception{
        if (buffer.containsKey(id)) {
            throw new RuntimeException("Must ensure there is no operation associate for the node before insert a new update operation");
        }
        buffer.put(id, new OperationNode(OpType.UPDATE, id, dist));
        if (buffer.size() == ConfigManager.getMemorySize()) {
            TournamentFileManager.fillup(this);
        }
    }

    public void deleteElement(int id) throws Exception{
        if (elementsRef.containsKey(id)) {
            TournamentNode node = elementsRef.get(id);
            removeElement(node.getNodeId());
        }
        bufferDeleteOp(id);
    }


    private void bufferDeleteOp(int id) throws Exception{
        // replace with DELETE operation
        buffer.put(id, new OperationNode(OpType.DELETE, id));
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

            for(TournamentNode node: minElements) {
                csvWriter.writeRow(node.getString());
            }

            for(OperationNode node: buffer.values()) {
                csvWriter.writeRow(node.getString());
            }
        }
    }

    public void readFromFile() throws IOException {
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            CsvParserSettings parserSettings = new CsvParserSettings();
            CsvParser parser = new CsvParser(parserSettings);
            parser.beginParsing(reader);
            String[] count = parser.parseNext();

            minAmongChild = Double.parseDouble(count[2]);

            String[] nodeString;
            List<TournamentNode> elements = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(count[0]); i++) {
                nodeString = parser.parseNext();
                int id = Integer.parseInt(nodeString[0]);
                double dist = Double.parseDouble(nodeString[1]);
                TournamentNode node = new TournamentNode(id, dist);
                elements.add(node);
                elementsRef.put(id, node);
            }
            this.minElements.addAll(elements);
            this.maxElements.addAll(elements);

            for (int i = 0; i < Integer.parseInt(count[1]); i++) {
                nodeString = parser.parseNext();
                int id = Integer.parseInt(nodeString[1]);
                double dist = Double.parseDouble(nodeString[2]);
                OpType type = OpType.valueOf(nodeString[0]);
                OperationNode node = new OperationNode(type, id, dist);

                buffer.put(id, node);
            }
        }
    }
}
