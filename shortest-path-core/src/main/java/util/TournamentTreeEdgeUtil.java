package util;

import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.common.processor.RowWriterProcessor;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class TournamentTreeEdgeUtil {

    private File fileName;
    private TreeSet<TournamentEdge> elements;
    private Map<Pair<Integer, Integer>, TournamentEdge> elementsRef;
    private Map<Pair<Integer, Integer>, OperationEdge> buffer;

    public TournamentTreeEdgeUtil(File fileName) {
        this.fileName = fileName;
        elements = new TreeSet<>();
        elementsRef = new HashMap<>();
        buffer = new HashMap<>();
    }

    public boolean isFull(){
        return elements.size()>=ConfigManager.getMemorySize();
    }

    public boolean addElement(TournamentEdge element){

        elementsRef.put(new Pair<>(element.getFromNode(),element.getToNode()), element);
        elements.add(element);

        return isFull();
    }

    public String getFileName() {
        return fileName.getName();
    }

    public Map<Pair<Integer, Integer>, TournamentEdge> getElementsRef() {
        return elementsRef;
    }

    public TreeSet<TournamentEdge> getElements() {
        return elements;
    }

    public void setElementsRef(Map<Pair<Integer, Integer>, TournamentEdge> elementsRef){
        this.elementsRef = elementsRef;
        this.elements = new TreeSet<>(elementsRef.values());
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
            TournamentEdge edge = elementsRef.get(key);
            if (OpType.DELETE.equals(op.getOperation())) {
                elements.remove(elementsRef.remove(key));
            } else if (OpType.UPDATE.equals(op.getOperation())) {
                if (edge.getDist() > op.getValue()) {
                    edge.setDist(op.getValue());
                }
            }
        } else {
            if (OpType.UPDATE.equals(op.getOperation())) {
                TournamentEdge edge = new TournamentEdge(op.getFromNode(), op.getToNode(), op.getValue());
                elements.add(edge);
                elementsRef.put(key, edge);
            }
        }
    }

    public void setBuffer(Map<Pair<Integer, Integer>, OperationEdge> buffer){
        this.buffer = buffer;
    }

    public TournamentEdge findMin() throws Exception{
        if (elements.isEmpty()) {
            TournamentFileManager.fillup(this);
        }
        return elements.isEmpty() ? null : elements.first();
    }

    // findMin must be called before any deleteMin so that the elements is loaded.
    public void deleteMin() throws Exception{
        TournamentEdge root = elements.pollFirst();
        elementsRef.remove(new Pair<>(root.getFromNode(), root.getToNode()));
        bufferDeleteOp(root.getFromNode(), root.getToNode());
    }

    public void updateDistance(int fromNode, int toNode, double dist) throws Exception{
        // elements contains the node, replace if dist decreases
        Pair<Integer, Integer> key = new Pair<>(fromNode, toNode);
        if (elementsRef.containsKey(key)) {
            TournamentEdge duplicate = elementsRef.get(key);
            if(duplicate.getDist() > dist) {
                duplicate.setDist(dist);
            }
        }

        //elements doesn't contain the node, check if need to insert into elements.
        else {
            if (!elements.isEmpty() &&  elements.last().getDist() >= dist) {
                TournamentEdge node = new TournamentEdge(fromNode, toNode, dist);
                elements.add(node);
                elementsRef.put(key, node);
                if (elements.size() > ConfigManager.getMemorySize()) {
                    TournamentEdge toBuffer = elements.pollLast();
                    elementsRef.remove(new Pair<>(toBuffer.getFromNode(), toBuffer.getToNode()));
                    bufferUpdateOp(toBuffer.getFromNode(), toBuffer.getToNode(), toBuffer.getDist());
                }
            } else {
                bufferUpdateOp(fromNode, toNode, dist);
            }
        }
    }

    private void bufferUpdateOp(int fromNode, int toNode, double dist) throws Exception{
        Pair<Integer, Integer> key = new Pair<>(fromNode, toNode);
        if (buffer.containsKey(key)) {
            OperationEdge op = buffer.get(key);
            // ignore when exists DELETE (only extractMin can delete) or UPDATE with smaller value
            if (op.getOperation().equals(OpType.UPDATE) && op.getValue() > dist) {
                op.setValue(dist);
            }
        } else {
            buffer.put(key, new OperationEdge(OpType.UPDATE, fromNode, toNode, dist));
            if (buffer.size() == ConfigManager.getMemorySize()) {
                TournamentFileManager.empty(this);
            }
        }
    }

    public void deleteElement(int fromNode, int toNode) throws Exception{
        Pair<Integer, Integer> key = new Pair<>(fromNode, toNode);
        if (elementsRef.containsKey(key)) {
            elements.remove(elementsRef.get(key));
        }
        bufferDeleteOp(fromNode, toNode);
    }


    private void bufferDeleteOp(int fromNode, int toNode) throws Exception{
        // replace with DELETE operation
        buffer.put(new Pair<>(fromNode, toNode), new OperationEdge(OpType.DELETE, fromNode, toNode));
        if (buffer.size() == ConfigManager.getMemorySize()) {
            TournamentFileManager.empty(this);
        }
    }

    public void storeToFile() throws IOException {
        try (Writer writer = new BufferedWriter((new FileWriter(fileName)))) {
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setQuoteAllFields(true);
            //write elements
            RowWriterProcessor<TournamentEdge> elementsProcessor = new BeanWriterProcessor<>(TournamentEdge.class);
            settings.setRowWriterProcessor(elementsProcessor);
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            csvWriter.writeRowToString(String.valueOf(elements.size()), String.valueOf(buffer.size()));

            for(TournamentEdge node: elements) {
                csvWriter.processRecordToString(node);
            }

            //write nodes
            RowWriterProcessor<OperationEdge> operationProcessor = new BeanWriterProcessor<>(OperationEdge.class);
            settings.setRowWriterProcessor(operationProcessor);
            csvWriter = new CsvWriter(writer, settings);
            for(OperationEdge node: buffer.values()) {
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
            List<TournamentEdge> elements = new ArrayList<>();
            System.out.println(fileName);
            System.out.println(count);
            for (int i = 0; i < Integer.parseInt(count[0]); i++) {
                nodeString = parser.parseNext();
                int fromNode = Integer.parseInt(nodeString[0]);
                int toNode = Integer.parseInt(nodeString[1]);
                double dist = Double.parseDouble(nodeString[2]);
                TournamentEdge node = new TournamentEdge(fromNode, toNode, dist);
                elements.add(node);
                elementsRef.put(new Pair<>(fromNode, toNode), node);
            }
            this.elements.addAll(elements);

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
