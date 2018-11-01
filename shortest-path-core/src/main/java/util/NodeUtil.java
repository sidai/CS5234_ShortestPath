package util;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import vo.Node;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NodeUtil {
    private List<Node> nodeList = new ArrayList<>();

    public void addNode(Node node) {
        nodeList.add(node);
    }

    public void addNode(int nodeId, double latitude, double longtitute) {
        addNode(new Node(nodeId, latitude, longtitute));
    }

    public int getNodeSize() {
        return nodeList.size();
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void storeToCSV(String storagePath) throws IOException {
        Path pathToFile = Paths.get(storagePath);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(storagePath)))) {
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setQuoteAllFields(true);
            settings.setHeaderWritingEnabled(true);
            BeanWriterProcessor<Node> processor = new BeanWriterProcessor<>(Node.class);
            settings.setRowWriterProcessor(processor);
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            for(Node node: nodeList) {
                csvWriter.processRecord(node);
            }
        }
    }

    public List<Node> loadFromCSV(String storagePath) throws IOException {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setHeaderExtractionEnabled(true);
        BeanListProcessor<Node> processor = new BeanListProcessor<>(Node.class);
        parserSettings.setProcessor(processor);
        CsvParser parser = new CsvParser(parserSettings);

        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(storagePath)))) {
            parser.parse(reader);
            nodeList = processor.getBeans();
            return nodeList;
        }
    }
}
