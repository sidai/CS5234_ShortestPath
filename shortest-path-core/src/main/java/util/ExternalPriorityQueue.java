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

    private static int ENTRY_BLOCK_SIZE = ConfigManager.getMemorySize();
    private static String DIRECTORY = "./map-data/priority-queue/";
    private static String NAME_PATTERN = "EXTERNAL_PRIORITYQUEUE_ENTRY_[%d-%d).csv";

//    private static int ENTRY_BLOCK_SIZE = 5;

    private PQNode root;
    private int nodeCount = 0;

    public int IOReadCount = 0;
    public int IOWriteCount = 0;

    public long popTime = 0;
    public long insertTime = 0;
    public long updateTime = 0;
    public long retrieveTime = 0;

    private int bufStart = -1;
    private int bufEnd = -1;
    private List<PQNode> bufs = new ArrayList<>();
    private boolean cacheEnabled = false;

    public ExternalPriorityQueue(boolean cacheEnabled) throws Exception {
        Path pathToDirectory = Paths.get(DIRECTORY);
        if (!Files.exists(pathToDirectory)) {
            Files.createDirectories(pathToDirectory);
        }
        this.root = null;
        this.cacheEnabled = cacheEnabled;
    }


    private void minHeapify(int i) throws Exception{
        PQNode node = retrievePQNode(i);
        int left = left(i);
        int right = right(i);
        int smallest = -1;

        PQNode leftNode = retrievePQNode(left);
        PQNode rightNode = retrievePQNode(right);
        PQNode iNode = retrievePQNode(i);

        PQNode smallestNode = null;

        if(leftNode!=null && leftNode.compareTo(iNode)<0){
            smallestNode = leftNode;
        }else{
            smallestNode = iNode;
        }
        if(rightNode!=null && rightNode.compareTo(smallestNode)<0){
            smallestNode = rightNode;
        }

        if(smallestNode.getPqIndex() != iNode.getPqIndex()){
            swap(iNode,smallestNode);
            //System.out.println("heapify "+iNode.getPqIndex());
            minHeapify(iNode.getPqIndex());
        }

    }

    public PQNode pop() throws Exception{
        final long startTime = System.currentTimeMillis();

        if(root == null){
            throw new IllegalStateException("queue is empty");
        }else {
//            if(nodeCount>104) {
//                System.out.println("queue count " + nodeCount);
//            }
            PQNode min = root;

            root = removeLast();
            if(root!= null) {
                root.setPqIndex(0);

            }
            nodeCount --;

            if(nodeCount>1) {
                minHeapify(0);
            }
            final long endTime = System.currentTimeMillis();
            popTime += endTime - startTime;

            return min;
        }
    }
    public PQNode peak(){
        return root;
    }

    public void update(PQNode node) throws Exception{ // for update cost
        final long startTime = System.currentTimeMillis();
        int parentIndex = parent(node.getPqIndex());
        PQNode parent = retrievePQNode(parentIndex);
        boolean needSwap = false;
        while(!parent.equals(node) && node.compareTo(parent)<0){
            needSwap = true;
            swap(node,parent);
            parent = retrievePQNode(parent(node.getPqIndex()));
        }
        if(!needSwap){
            if(node.getPqIndex()!=0){
                updateToFile(node);
            }

        }
        final long endTime = System.currentTimeMillis();
        updateTime += endTime - startTime;
    }

    public PQNode retrieve(PQNode node) throws Exception{
        final long startTime = System.currentTimeMillis();
        for (int i=0;i<nodeCount;i++){
            PQNode retrievedNode = retrievePQNode(i);
            if(retrievedNode!=null && retrievedNode.equals(node)){
                final long endTime = System.currentTimeMillis();
                retrieveTime += endTime - startTime;
                return retrievedNode;
            }
        }
        final long endTime = System.currentTimeMillis();
        retrieveTime += endTime - startTime;
        return null;
    }

    public void insert(PQNode node) throws Exception{
        final long startTime = System.currentTimeMillis();
        node.setPqIndex(nodeCount);

        nodeCount++;
        if(nodeCount==1){
            root = node;
            return;
        }

        insertPQNode(node);
        int parentIndex = parent(node.getPqIndex());
        PQNode parent = retrievePQNode(parentIndex);
        while(parent!=null && !parent.equals(node) && node.compareTo(parent)<0){
            swap(node,parent);

            parent = retrievePQNode(parent(node.getPqIndex()));
        }
        final long endTime = System.currentTimeMillis();
        insertTime += endTime - startTime;
    }

    public boolean isEmpty(){
        return root == null;
    }


    private void swap(PQNode child, PQNode parent) throws Exception{
        int childPQIndex = child.getPqIndex();
        int parentPQIndex = parent.getPqIndex();

        parent.setPqIndex(childPQIndex);
        child.setPqIndex(parentPQIndex);

        if(parentPQIndex != 0){
            updateToFile(child);
        }else{
            root = child;
        }
        if(childPQIndex != 0){
            updateToFile(parent);
        }else{
            root = parent;
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

       storeToFile(file,nodes, false);
   }

   private PQNode removeLastFromFile() throws Exception{
        //only happen at the last file (last node as the new root)
       List<PQNode> nodes = retrieveWholeFile(nodeCount-1);
       if(nodes.size()==0){
           return null;
       }
       PQNode last =nodes.remove(nodes.size()-1);



       int fileId = (last.getPqIndex()-1)/ENTRY_BLOCK_SIZE;
       File file = new File(DIRECTORY + getMapFileName(NAME_PATTERN, fileId));

       storeToFile(file,nodes, false);

       return last;
   }

   private void updateToFile(PQNode node) throws Exception{
        // update node distance
       //System.out.println("update to file "+node.getNodeId()+" "+node.getPqIndex()+" "+newPQIndex);
       List<PQNode> nodes = retrieveWholeFile(node.getPqIndex());
       int replaceIndex = node.getPqIndex()-bufStart;

       nodes.set(replaceIndex,node);
       int fileId = (node.getPqIndex()-1)/ENTRY_BLOCK_SIZE;
       File file = new File(DIRECTORY + getMapFileName(NAME_PATTERN, fileId));
       storeToFile(file,nodes, false);
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
        return readFromFile(pqIndex);
    }

    private String getMapFileName(String pattern, int fileId) {
        int from = fileId * ENTRY_BLOCK_SIZE +1;
        int to = (fileId + 1) * ENTRY_BLOCK_SIZE +1;
        return String.format(pattern, from, to);
    }


//
    public List<PQNode> readFromFile(int pdIndex) throws Exception {
        if(cacheEnabled) {
            if (bufStart <= pdIndex && bufEnd > pdIndex) {
                return bufs;
            } else {
                File file = new File(DIRECTORY + String.format(NAME_PATTERN, bufStart, bufEnd));
                storeToFile(file, bufs, true);
            }
        }


        int fileId = (pdIndex-1)/ENTRY_BLOCK_SIZE;
        int from = fileId * ENTRY_BLOCK_SIZE +1;
        int to = (fileId + 1) * ENTRY_BLOCK_SIZE +1;
        File file = new File(DIRECTORY + String.format(NAME_PATTERN, from, to));
        bufStart=from;
        bufEnd=to;

        IOReadCount ++;
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
            bufs = processor.getBeans();
//            if(bufs.size()>0){
//                System.out.println("new buf "+bufs.get(0).getPqIndex()+" "+ bufs.get(0).getNodeId());
//            }
            return bufs;
        }
    }

    public void storeToFile(File file, List<PQNode> pqNodes, boolean force) throws Exception {
        bufs = pqNodes;

        if (!file.exists()) {
            file.createNewFile();
        }
        if(cacheEnabled){
            if(bufs.size() < ENTRY_BLOCK_SIZE && !force){
                return;
            }
        }

        IOWriteCount ++;
//        if(bufs.size()>0){
//            System.out.println("store buf "+bufs.get(0).getPqIndex()+" "+ bufs.get(0).getNodeId());
//        }
        try (Writer writer = new BufferedWriter(new FileWriter(file, false))) {
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

    public void clearAll() throws Exception{

        File dir = new File(DIRECTORY);
        for(File file: dir.listFiles())
            if (!file.isDirectory())
                file.delete();
    }
}
