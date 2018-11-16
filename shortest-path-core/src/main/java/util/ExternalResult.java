package util;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import vo.PQNode;
import vo.ResultNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;



public class ExternalResult {

    private static int ENTRY_BLOCK_SIZE = ConfigManager.getMemorySize();


    private static String NAME_PATTERN = "EXTERNAL_RESULT_[%d-%d).csv";

//    private static int ENTRY_BLOCK_SIZE = 10;

    public int resultCount = 0;

    public int IOReadCount = 0;
    public int IOWriteCount = 0;

    public long insertTime = 0;
    public long retrieveTime = 0;

    private String DIRECTORY;

    private int bufStart = -1;
    private int bufEnd = -1;
    private List<ResultNode> bufs = new ArrayList<>();
    private boolean cacheEnabled = false;
    private boolean dirty = false;

    public ExternalResult(String directory,boolean cacheEnabled) throws Exception {
        DIRECTORY = directory;
        this.cacheEnabled = cacheEnabled;
        Path pathToDirectory = Paths.get(DIRECTORY);
        if (!Files.exists(pathToDirectory)) {
            Files.createDirectories(pathToDirectory);
        }
    }


    public double retrieveCost(int nodeId) throws Exception{
        final long startTime = System.currentTimeMillis();
        ResultNode node =  retrieveFromFile(nodeId);
        final long endTime = System.currentTimeMillis();
        retrieveTime += endTime - startTime;
        if (node == null) {

            return -1;
        }
        return node.getDist();

    }

    public void insertResult(int nodeId, double dist) throws Exception{
        final long startTime = System.currentTimeMillis();
        insertToFile(new ResultNode(nodeId,dist));
        resultCount+=1;
        final long endTime = System.currentTimeMillis();
        insertTime += endTime - startTime;
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
        return readFromFile(nodeId);
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
                insertIndex = i;
                break;
            }
        }
        if(insertIndex<0){
            insertIndex = nodes.size();
        }
        nodes.add(insertIndex,node);

        storeToFile(file,nodes, false);
    }

    private String getMapFileName(String pattern, int fileId) {
        int from = fileId * ENTRY_BLOCK_SIZE;
        int to = (fileId + 1) * ENTRY_BLOCK_SIZE;
        return String.format(pattern, from, to);
    }

    public List<ResultNode> readFromFile(int resultIndex) throws Exception {
        if(cacheEnabled) {
            if (bufStart <= resultIndex && bufEnd > resultIndex) {
                return bufs;
            } else if(dirty){
                File file = new File(DIRECTORY + String.format(NAME_PATTERN, bufStart, bufEnd));
                storeToFile(file, bufs, true);
            }
        }

        int fileId = (resultIndex)/ENTRY_BLOCK_SIZE;
        int from = fileId * ENTRY_BLOCK_SIZE ;
        int to = (fileId + 1) * ENTRY_BLOCK_SIZE;
        File file = new File(DIRECTORY + getMapFileName(NAME_PATTERN, fileId));


        bufStart=from;
        bufEnd=to;

        IOReadCount++;
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
            bufs = processor.getBeans();
            return bufs;
        }
    }


    public void storeToFile(File file, List<ResultNode> resultNodes, boolean force) throws Exception {
        bufs = resultNodes;
        dirty= true;

        if (!file.exists()) {
            file.createNewFile();
        }
        if(cacheEnabled){
            if(bufs.size() < ENTRY_BLOCK_SIZE && !force){
                return;
            }
        }
        IOWriteCount++;
        dirty = false;
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
