package util;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import vo.PQNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;



public class ExternalPriorityQueue {

    private static int ENTRY_BLOCK_SIZE = 10000;
    private static int NODE_SIZE = 2675656;
    private static String DIRECTORY = "./map-data/priority-queue/";
    private static String NAME_PATTERN = "EXTERNAL_PRIORITYQUEUE_ENTRY_[%d-%d).csv";


    private PQNode root;
    private int nodeCount = 0;

    public ExternalPriorityQueue() throws Exception {
        Path pathToDirectory = Paths.get(DIRECTORY);
        if (!Files.exists(pathToDirectory)) {
            Files.createDirectories(pathToDirectory);
        }
        this.root = null;
    }


    private void minHeapify(int i) throws Exception{
        PQNode node = retrievePQNode(i);
        int left = left(i);
        int right = right(i);
        int smallest = -1;
        if(left!=PQNode.EMPTY_POINTER && retrievePQNode(left).compareTo(retrievePQNode(i))<0){
            smallest = left;
        }else{
            smallest = i;
        }
        if(right!=PQNode.EMPTY_POINTER && retrievePQNode(right).compareTo(retrievePQNode(smallest))<0){
            smallest = right;
        }
        if(smallest!=i){
            PQNode iNode = retrievePQNode(i);
            PQNode smallestNode = retrievePQNode(smallest);
            swap(iNode,smallestNode);

            minHeapify(smallest);
        }

    }

    public PQNode pop() throws Exception{
        if(root == null){
            throw new IllegalStateException("queue is empty");
        }else {

            PQNode min = root;

            root = removeLast();

            nodeCount --;

            minHeapify(0);

            return min;
        }
    }
    public PQNode peak(){
        return root;
    }

    public void update(PQNode node) throws Exception{ // for update cost


        int parentIndex = parent(node.getPqIndex());
        PQNode parent = retrievePQNode(parentIndex);
        while(!parent.equals(node) && node.compareTo(parent)<0){
            swap(node,parent);

//            parentIndex = parent.getPqIndex();
//            parent.setPqIndex(node.getPqIndex());
//            node.setPqIndex(parentIndex);

            parent = retrievePQNode(parent(node.getPqIndex()));
        }
    }

    public PQNode retrieve(PQNode node) throws Exception{
        for (int i=0;i<nodeCount;i++){
            PQNode retrievedNode = retrievePQNode(i);
            if(retrievedNode.equals(node)){
                return retrievedNode;
            }
        }
        return null;
    }

    public void insert(PQNode node) throws Exception{
        nodeCount++;

        node.setPqIndex(nodeCount);
        if(nodeCount==1){
            root = node;
            return;
        }

        insertPQNode(node);
        int parentIndex = parent(node.getPqIndex());
        PQNode parent = retrievePQNode(parentIndex);
        while(!parent.equals(node) && node.compareTo(parent)<0){
            swap(node,parent);

//            parentIndex = parent.getPqIndex();
//            parent.setPqIndex(node.getPqIndex());
//            node.setPqIndex(parentIndex);

            parent = retrievePQNode(parent(node.getPqIndex()));
        }
    }

    public boolean isEmpty(){
        return root == null;
    }


    private void swap(PQNode child, PQNode parent) throws Exception{
        int childPQIndex = child.getPqIndex();
        int parentPQIndex = parent.getPqIndex();
        child.setPqIndex(parentPQIndex);
        parent.setPqIndex(childPQIndex);

        if(child.getPqIndex() == 0){
            root = child;
        }else{
            updateToFile(child);
        }
        if(parent.getPqIndex() == 0){
            root = parent;
        }else{
            updateToFile(parent);
        }
    }

    private PQNode retrievePQNode(int i) throws Exception{
        if(i==0){
            return root;
        }

        return retrieveFromFile(i);
    }

    private void insertPQNode(PQNode node) throws Exception{
        insertToFile(node);
    }

    private PQNode removeLast() throws Exception{
        return removeLastFromFile();
    }


    private int right(int i) {
        return 2 * i + 2;
    }

    private int left(int i) {
        return 2 * i + 1;
    }

    private int parent(int i) {
        if (i % 2 == 1) {
            return i / 2;
        }
        return (i - 1) / 2;
    }

   private void insertToFile (PQNode node) throws Exception{
        //insert to last file
       int fileId = (node.getPqIndex()-1)/ENTRY_BLOCK_SIZE;
       File file = new File(DIRECTORY + getMapFileName(NAME_PATTERN, fileId));

       List<PQNode> nodes = retrieveWholeFile(node.getPqIndex());
       nodes.add(node);

       storeToFile(file,nodes);
   }

   private PQNode removeLastFromFile() throws Exception{
        //only happen at the last file (last node as the new root)
       List<PQNode> nodes = retrieveWholeFile(nodeCount);
       PQNode last =nodes.remove(nodes.size()-1);

       return last;
   }

   private void updateToFile(PQNode node) throws Exception{
        // update node distance
       List<PQNode> nodes = retrieveWholeFile(node.getPqIndex());
       int replaceIndex = -1;
       for(int i=0; i<nodes.size(); i++) {
           PQNode cur = nodes.get(i);
           if(cur.getPqIndex() == node.getPqIndex()){
               replaceIndex = i;
               break;
           }
       }
       nodes.set(replaceIndex,node);
       int fileId = (node.getPqIndex()-1)/ENTRY_BLOCK_SIZE;
       File file = new File(DIRECTORY + getMapFileName(NAME_PATTERN, fileId));
       storeToFile(file,nodes);
   }


   private PQNode retrieveFromFile(int pqIndex) throws Exception{
       List<PQNode> nodes = retrieveWholeFile(pqIndex);
       for(int i=0; i<nodes.size(); i++){
           PQNode cur = nodes.get(i);
           if(cur.getPqIndex() == pqIndex){
               return cur;
           }
       }
       return null;
   }

    private List<PQNode> retrieveWholeFile(int pqIndex) throws Exception{
        int fileId = (pqIndex-1)/ENTRY_BLOCK_SIZE;
        File file = new File(DIRECTORY + getMapFileName(NAME_PATTERN, fileId));
        return readFromFile(file);
    }

//    public File getBlockIdentifier(int pqIndexId) {
//        int fileId = getMapIdInt(pqIndexId);
//        Assert.check(ENTRY_INDEX.containsKey(fileId), pqIndexId);
//
//        return ENTRY_INDEX.get(fileId);
//    }
//    private int getMapIdInt(int pqIndexId) {
//        Assert.check(pqIndexId < nodeCount);
//
//        return (pqIndexId-1) / ENTRY_BLOCK_SIZE; //root in memory
//    }
    private String getMapFileName(String pattern, int fileId) {
        int from = fileId * ENTRY_BLOCK_SIZE +1;
        int to = (fileId + 1) * ENTRY_BLOCK_SIZE +1;
        return String.format(pattern, from, to);
    }


//
    public List<PQNode> readFromFile(File file) throws Exception {

        if (!file.exists()) {
            file.createNewFile();
        }

        try (Reader reader = new BufferedReader(new FileReader(file))) {
            CsvParserSettings parserSettings = new CsvParserSettings();
            parserSettings.setHeaderExtractionEnabled(true);
            BeanListProcessor<PQNode> processor = new BeanListProcessor<>(PQNode.class);
            parserSettings.setProcessor(processor);
            CsvParser parser = new CsvParser(parserSettings);

            parser.parse(reader);
            return processor.getBeans();
        }
    }

    public void storeToFile(File file, List<PQNode> pqNodes) throws Exception {

        if (!file.exists()) {
            file.createNewFile();
        }

        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setQuoteAllFields(true);
            settings.setHeaderWritingEnabled(true);
            BeanWriterProcessor<PQNode> processor = new BeanWriterProcessor<>(PQNode.class);
            settings.setRowWriterProcessor(processor);
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            for(PQNode node: pqNodes) {
                csvWriter.processRecord(node);
            }
        }
    }
}
