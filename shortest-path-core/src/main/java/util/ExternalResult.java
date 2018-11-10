package util;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import vo.ResultNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;



public class ExternalResult {

    //private static int ENTRY_BLOCK_SIZE = 10000;

    private static String DIRECTORY = "./map-data/external-result/";
    private static String NAME_PATTERN = "EXTERNAL_RESULT_[%d-%d).csv";

    private static int ENTRY_BLOCK_SIZE = 10;

    private int resultCount = 0;

    public ExternalResult() throws Exception {
        Path pathToDirectory = Paths.get(DIRECTORY);
        if (!Files.exists(pathToDirectory)) {
            Files.createDirectories(pathToDirectory);
        }
    }


    public double retrieveCost(int nodeId) throws Exception{
        ResultNode node =  retrieveFromFile(nodeId);
        if (node == null) {

            return -1;
        }
        return node.getDist();

    }

    public void insertResult(int nodeId, double dist) throws Exception{

        insertToFile(new ResultNode(nodeId,dist));
        resultCount+=1;
        if(resultCount%1000==0){
            System.out.println(resultCount);
        }
    }

    //IO
    private ResultNode retrieveFromFile(int nodeId) throws Exception{
        Path pathToDirectory = Paths.get(DIRECTORY);
        if (!Files.exists(pathToDirectory)) {
            Files.createDirectories(pathToDirectory);
        }
        int fileId = (nodeId)/ENTRY_BLOCK_SIZE;
        File file = new File(DIRECTORY + getMapFileName(NAME_PATTERN, fileId));
        if (!file.exists()) {
            file.createNewFile();
        }
        List<ResultNode> nodes = retrieveWholeFile(nodeId);
        for(int i=0; i<nodes.size(); i++){
            ResultNode cur = nodes.get(i);
            if(cur.getNodeId() == nodeId){
                return cur;
            }
        }
        return null;
    }


    private List<ResultNode> retrieveWholeFile(int nodeId) throws Exception{
        int fileId = (nodeId)/ENTRY_BLOCK_SIZE;
        File file = new File(DIRECTORY + getMapFileName(NAME_PATTERN, fileId));
        return readFromFile(file);
    }

    private void insertToFile(ResultNode node) throws Exception{
        Path pathToDirectory = Paths.get(DIRECTORY);
        if (!Files.exists(pathToDirectory)) {
            Files.createDirectories(pathToDirectory);
        }
        int fileId = (node.getNodeId())/ENTRY_BLOCK_SIZE;
        File file = new File(DIRECTORY + getMapFileName(NAME_PATTERN, fileId));
        if (!file.exists()) {
            file.createNewFile();
        }

        int insertIndex = -1;
        List<ResultNode> nodes = retrieveWholeFile(node.getNodeId());
        for(int i=0; i<nodes.size(); i++){
            ResultNode cur = nodes.get(i);
            if(cur.getNodeId() > node.getNodeId()){
               // System.out.println("\nresult compare node id "+cur.getNodeId()+" "+node.getNodeId());
                insertIndex = i;
                break;
            }
        }
        if(insertIndex<0){
            insertIndex = nodes.size();
        }
        nodes.add(insertIndex,node);
//        System.out.println("insert to file");
//        for(int i = 0; i < nodes.size(); i++) {
//            System.out.println(nodes.get(i).getNodeId());
//        }

        storeToFile(file,nodes);
    }

    private String getMapFileName(String pattern, int fileId) {
        int from = fileId * ENTRY_BLOCK_SIZE;
        int to = (fileId + 1) * ENTRY_BLOCK_SIZE;
        return String.format(pattern, from, to);
    }

    public List<ResultNode> readFromFile(File file) throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        }

        try (Reader reader = new BufferedReader(new FileReader(file))) {
            CsvParserSettings parserSettings = new CsvParserSettings();
            parserSettings.setHeaderExtractionEnabled(true);
            BeanListProcessor<ResultNode> processor = new BeanListProcessor<>(ResultNode.class);
            parserSettings.setProcessor(processor);
            CsvParser parser = new CsvParser(parserSettings);

            parser.parse(reader);
            return processor.getBeans();
        }
    }


    public void storeToFile(File file, List<ResultNode> resultNodes) throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        }

        try (Writer writer = new BufferedWriter(new FileWriter(file, false))) {
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setQuoteAllFields(true);
            settings.setHeaderWritingEnabled(true);
            BeanWriterProcessor<ResultNode> processor = new BeanWriterProcessor<>(ResultNode.class);
            settings.setRowWriterProcessor(processor);
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            for (ResultNode node : resultNodes) {
                csvWriter.processRecord(node);
            }
        }
    }

    public void clearAll() throws Exception{

        File dir = new File(DIRECTORY);
        for(File file: dir.listFiles())
            if (!file.isDirectory())
                file.delete();
    }
}
