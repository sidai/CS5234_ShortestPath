package util;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import vo.ResultNode;

import javax.xml.transform.Result;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;



public class ExternalResult {

    private static int ENTRY_BLOCK_SIZE = 10000;
    private static int NODE_SIZE = 2675656;
    private static String DIRECTORY = "./map-data/external-result/";
    private static String NAME_PATTERN = "EXTERNAL_RESULT_[%d-%d).csv";


    public ExternalResult() throws Exception {

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
                insertIndex = i;
                break;
            }
        }
        nodes.add(insertIndex,node);

        storeToFile(file,nodes);
    }

    private String getMapFileName(String pattern, int fileId) {
        int from = fileId * ENTRY_BLOCK_SIZE +1;
        int to = (fileId + 1) * ENTRY_BLOCK_SIZE +1;
        return String.format(pattern, from, to);
    }

    public List<ResultNode> readFromFile(File file) throws Exception {
//        try (Reader reader = new BufferedReader(new FileReader(file))) {
//            CsvToBean<PQNode> csvToBean = new CsvToBeanBuilder(reader)
//                    .withMappingStrategy(getStrategy())
//                    .withQuoteChar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
//                    .withIgnoreLeadingWhiteSpace(true)
//                    .build();
//
//            addAdjListEntry(csvToBean.parse());
//            return adjListEntryMap;
//        } catch (Exception ex) {
//            throw ex;
//        }
        return new ArrayList<>();
    }


    public void storeToFile(File file,List<ResultNode> resultNodes) throws Exception {
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            StatefulBeanToCsv<ResultNode> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withMappingStrategy(getStrategy())
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .build();

            beanToCsv.write(resultNodes);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private ColumnPositionMappingStrategy<ResultNode> getStrategy() {
        ColumnPositionMappingStrategy<ResultNode> strategy = new CustomMappingStrategy<>();
        strategy.setType(ResultNode.class);

        return strategy;
    }

}
