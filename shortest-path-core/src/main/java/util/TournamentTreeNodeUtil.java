package util;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.common.processor.MultiBeanListProcessor;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.common.processor.RowWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import com.univocity.parsers.tsv.TsvWriter;
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
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class TournamentTreeNodeUtil {

    private String range;
    private TreeSet<TournamentNode> elements;
    private Map<Integer, TournamentNode> elementsRef;
    private Map<Integer, OperationNode> buffer;

    public TournamentTreeNodeUtil(String range) {
        this.range = range;
    }

    public TournamentNode extractMin() {
        if (elements.isEmpty()) {
            fillup();
        }
        TournamentNode root = elements.pollFirst();
        bufferDeleteOp(root.getNodeId());
        return root;
    }

    public void updateDistance(int id, double dist) {
        // elements contains the node, replace if dist decreases
        if (elementsRef.containsKey(id)) {
            TournamentNode duplicate = elementsRef.get(id);
            if(duplicate.getDist() > dist) {
                duplicate.setDist(dist);
            }
        }

        //elements doesn't contain the node, check if need to insert into elements.
        else {
            if (elements.last().getDist() >= dist) {
                elements.add(new TournamentNode(id, dist));
                if (elements.size() > ConfigManager.getMemorySize()) {
                    TournamentNode toBuffer = elements.pollLast();
                    bufferUpdateOp(toBuffer.getNodeId(), toBuffer.getDist());
                }
            } else {
                bufferUpdateOp(id, dist);
            }
        }
    }

    private void bufferUpdateOp(int id, double dist) {
        if (buffer.containsKey(id)) {
            OperationNode op = buffer.get(id);
            // ignore when exists DELETE (only extractMin can delete) or UPDATE with smaller value
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

    public void deleteElement(int id) {
        if (elementsRef.containsKey(id)) {
            elements.remove(elementsRef.get(id));
        }
        bufferDeleteOp(id);
    }


    private void bufferDeleteOp(int id) {
        // replace with DELETE operation
        buffer.put(id, new OperationNode(OpType.DELETE, id));
        if (buffer.size() == ConfigManager.getMemorySize()) {
            empty();
        }
    }

    private void empty() {

    }

    private void fillup() {

    }

    public void storeToFile(File file) throws IOException {
        try (Writer writer = new BufferedWriter((new FileWriter(file)))) {
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setQuoteAllFields(true);
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            csvWriter.writeRowToString(String.valueOf(elements.size()), String.valueOf(buffer.size()));

            //write elements
            RowWriterProcessor<TournamentNode> elementsProcessor = new BeanWriterProcessor<>(TournamentNode.class);
            settings.setRowWriterProcessor(elementsProcessor);
            for(TournamentNode node: elements) {
                csvWriter.processRecordToString(node);
            }

            //write elements
            RowWriterProcessor<OperationNode> operationProcessor = new BeanWriterProcessor<>(OperationNode.class);
            settings.setRowWriterProcessor(operationProcessor);
            for(OperationNode node: buffer.values()) {
                csvWriter.processRecordToString(node);
            }
        }
    }

    public void readFromFile(File file) throws IOException {
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            CsvParserSettings parserSettings = new CsvParserSettings();
            CsvParser parser = new CsvParser(parserSettings);
            String[] count = parser.parseNext();
        }
    }





}
