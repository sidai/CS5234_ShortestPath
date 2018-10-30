package util;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import vo.Node;

import java.io.IOException;
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

    public void storeToCSV(String storagePath) throws IOException,
            CsvDataTypeMismatchException,
            CsvRequiredFieldEmptyException {

        Path pathToFile = Paths.get(storagePath);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
            Files.createFile(pathToFile);
        }

        try (Writer writer = Files.newBufferedWriter(pathToFile)) {
            StatefulBeanToCsv<Node> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withMappingStrategy(getStrategy())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();

            beanToCsv.write(nodeList);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void loadFromCSV(String storagePath) throws IOException {

        Path pathToFile = Paths.get(storagePath);

        try (Reader reader = Files.newBufferedReader(pathToFile)) {
            CsvToBean<Node> csvToBean = new CsvToBeanBuilder(reader)
                    .withMappingStrategy(getStrategy())
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            nodeList = csvToBean.parse();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private ColumnPositionMappingStrategy<Node> getStrategy() {
        ColumnPositionMappingStrategy<Node> strategy = new CustomMappingStrategy<>();
        strategy.setType(Node.class);

        return strategy;
    }
}
