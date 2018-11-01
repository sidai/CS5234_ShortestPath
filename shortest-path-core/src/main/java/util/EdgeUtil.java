package util;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import vo.Edge;

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
import java.util.Comparator;
import java.util.List;

public class EdgeUtil {

    private List<Edge> edgeList = new ArrayList<>();

    public void addEdge(Edge edge) {
        edgeList.add(edge);
    }

    public void addEdge(int fromNode, int toNode, double distance) {
        addEdge(new Edge(fromNode, toNode, distance));
    }

    public void sort() {
        edgeList.sort(Comparator.comparingInt(Edge::getFromNode));
    }

    public int getEdgeSize() {
        return edgeList.size();
    }

    public List<Edge> getEdgeList() {
        return edgeList;
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
            BeanWriterProcessor<Edge> processor = new BeanWriterProcessor<>(Edge.class);
            settings.setRowWriterProcessor(processor);
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            for(Edge edge: edgeList) {
                csvWriter.processRecord(edge);
            }
        }
    }

    public List<Edge> loadFromCSV(String storagePath) throws IOException {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setHeaderExtractionEnabled(true);
        BeanListProcessor<Edge> processor = new BeanListProcessor<>(Edge.class);
        parserSettings.setProcessor(processor);
        CsvParser parser = new CsvParser(parserSettings);

        try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(storagePath)))) {
            parser.parse(reader);
            edgeList = processor.getBeans();
            return edgeList;
        }
    }
}
