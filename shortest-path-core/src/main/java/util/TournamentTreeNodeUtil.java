package util;

import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.common.processor.RowWriterProcessor;
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

    private File fileName;
    private TreeSet<TournamentNode> elements;
    private Map<Integer, TournamentNode> elementsRef;
    private Map<Integer, OperationNode> buffer;

    public TournamentTreeNodeUtil(File fileName) {
        this.fileName = fileName;
        elements = new TreeSet<>();
        elementsRef = new HashMap<>();
        buffer = new HashMap<>();
    }

    public void executeOp(OperationNode op) throws Exception {
        if(OpType.DELETE.equals(op.getOperation())) {
            deleteElement(op.getId());
        } else {
            updateDistance(op.getId(), op.getValue());
        }
    }

    public void commitOp(OperationNode op) throws Exception {
        if (elementsRef.containsKey(op.getId())) {
            if (OpType.DELETE.equals(op.getOperation())) {
                elements.remove(elementsRef.remove(op.getId()));
            } else if (OpType.UPDATE.equals(op.getOperation())) {
                TournamentNode node = elementsRef.get(op.getId());
                if (node.getDist() > op.getValue()) {
                    node.setDist(op.getValue());
                }
            }
        } else {
            if (OpType.UPDATE.equals(op.getOperation())) {
                TournamentNode node = new TournamentNode(op.getId(), op.getValue());
                elements.add(node);
                elementsRef.put(op.getId(), node);
            }
        }
    }

    public boolean isFull(){
        return elements.size()>=ConfigManager.getMemorySize();
    }

    public boolean addElement(TournamentNode element){
        elementsRef.put(element.getNodeId(), element);
        elements.add(element);
        return isFull();
    }

    public String getFileName() {
        return fileName.getName();
    }

    public Map<Integer, TournamentNode> getElementsRef() {
        return elementsRef;
    }

    public TreeSet<TournamentNode> getElements() {
        return elements;
    }

    public void setElementsRef(Map<Integer,TournamentNode> elementsRef){
        this.elementsRef = elementsRef;
        this.elements = new TreeSet<>(elementsRef.values());
    }

    public Map<Integer, OperationNode> getBuffer() {
        return buffer;
    }
    public void setBuffer(Map<Integer, OperationNode> buffer){
        this.buffer = buffer;
    }

    public TournamentNode findMin() throws Exception{
        if (elements.isEmpty()) {
            TournamentFileManager.fillup(this);
        }
        return elements.isEmpty() ? null : elements.first();
    }

    // findMin must be called before any deleteMin so that the elements is loaded.
    public void deleteMin() throws Exception{
        TournamentNode root = elements.pollFirst();
        elementsRef.remove(root.getNodeId());
        bufferDeleteOp(root.getNodeId());
    }

    public void updateDistance(int id, double dist) throws Exception{
        // elements contains the node, replace if dist decreases
        if (elementsRef.containsKey(id)) {
            TournamentNode duplicate = elementsRef.get(id);
            if(duplicate.getDist() > dist) {
                duplicate.setDist(dist);
            }
        }

        //elements doesn't contain the node, check if need to insert into elements.
        else {
            if (!elements.isEmpty() && elements.last().getDist() >= dist) {
                TournamentNode node = new TournamentNode(id, dist);
                elements.add(node);
                elementsRef.put(id, node);
                if (elements.size() > ConfigManager.getMemorySize()) {
                    TournamentNode toBuffer = elements.pollLast();
                    elementsRef.remove(toBuffer.getNodeId());
                    bufferUpdateOp(toBuffer.getNodeId(), toBuffer.getDist());
                }
            } else {
                bufferUpdateOp(id, dist);
            }
        }
    }

    private void bufferUpdateOp(int id, double dist) throws Exception{
        if (buffer.containsKey(id)) {
            OperationNode op = buffer.get(id);
            // ignore when exists DELETE (only extractMin can delete) or UPDATE with smaller value
            if (op.getOperation().equals(OpType.UPDATE) && op.getValue() > dist) {
                op.setValue(dist);
            }
        } else {
            buffer.put(id, new OperationNode(OpType.UPDATE, id, dist));
            if (buffer.size() == ConfigManager.getMemorySize()) {
                TournamentFileManager.empty(this);
            }
        }
    }

    public void deleteElement(int id) throws Exception{
        if (elementsRef.containsKey(id)) {
            elements.remove(elementsRef.remove(id));
        }
        bufferDeleteOp(id);
    }


    private void bufferDeleteOp(int id) throws Exception{
        // replace with DELETE operation
        buffer.put(id, new OperationNode(OpType.DELETE, id));
        if (buffer.size() == ConfigManager.getMemorySize()) {
            TournamentFileManager.empty(this);
        }
    }

    public void storeToFile() throws IOException {
        try (Writer writer = new BufferedWriter((new FileWriter(fileName)))) {
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setQuoteAllFields(true);
            //write elements
            RowWriterProcessor<TournamentNode> elementsProcessor = new BeanWriterProcessor<>(TournamentNode.class);
            settings.setRowWriterProcessor(elementsProcessor);
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            csvWriter.writeRowToString(String.valueOf(elements.size()), String.valueOf(buffer.size()));

            for(TournamentNode node: elements) {
                csvWriter.processRecordToString(node);
            }

            //write nodes
            RowWriterProcessor<OperationNode> operationProcessor = new BeanWriterProcessor<>(OperationNode.class);
            settings.setRowWriterProcessor(operationProcessor);
            csvWriter = new CsvWriter(writer, settings);
            for(OperationNode node: buffer.values()) {
                csvWriter.processRecordToString(node);
            }
        }
    }

    public void readFromFile() throws IOException {
        try (Reader reader = new BufferedReader(new FileReader(fileName))) {
            CsvParserSettings parserSettings = new CsvParserSettings();
            CsvParser parser = new CsvParser(parserSettings);
            parser.beginParsing(reader);
            String[] count = parser.parseNext();

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
            this.elements.addAll(elements);

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
