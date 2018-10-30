package util;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import vo.Edge;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EdgeUtil {

    private List<Edge> edgeList = new ArrayList<>();

    public void addEdge(Edge edge) {
        edgeList.add(edge);
    }

    public void addEdge(int edgeId, int fromNode, int toNode) {
        addEdge(new Edge(edgeId, fromNode, toNode));
    }

    public void addEdge(int edgeId, int fromNode, int toNode, double distance, String name) {
        addEdge(new Edge(edgeId, fromNode, toNode, distance, name));
    }


    public int getEdgeSize() {
        return edgeList.size();
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    public void storeToCSV(String storagePath) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

        Path pathToFile = Paths.get(storagePath);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }

        try (Writer writer = Files.newBufferedWriter(pathToFile)) {
            StatefulBeanToCsv<Edge> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withMappingStrategy(getStrategy())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();

            beanToCsv.write(edgeList);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void loadFromCSV(String storagePath) throws IOException {

        Path pathToFile = Paths.get(storagePath);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }

        try (Reader reader = Files.newBufferedReader(pathToFile)) {
            CsvToBean<Edge> csvToBean = new CsvToBeanBuilder(reader)
                    .withMappingStrategy(getStrategy())
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            edgeList = csvToBean.parse();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private ColumnPositionMappingStrategy<Edge> getStrategy() {
        ColumnPositionMappingStrategy<Edge> strategy = new CustomMappingStrategy<>();
        strategy.setType(Edge.class);

        return strategy;
    }
}
