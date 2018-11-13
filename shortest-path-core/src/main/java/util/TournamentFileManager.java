package util;

import vo.TournamentEdge;
import vo.TournamentNode;
import vo.OperationNode;
import vo.OperationEdge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.*;

public class TournamentFileManager {

    public static TournamentTreeNodeUtil nodeRoot;
    public static TournamentTreeEdgeUtil edgeRoot;
    private static int ENTRY_BLOCK_SIZE = 3200;
    private static int NODE_SIZE = 2675656;
    private static int EDGE_SIZE = 3602918;
    private static String EDGE_DIRECTORY = "./map-data/edge-pq/";
    private static String NODE_DIRECTORY = "./map-data/node-pq/";
    private static String RANGE_PATTERN = "%d-%d.csv";

    public static int IOEdgeReadCount = 0;
    public static int IOEdgeWriteCount = 0;

    public static int IONodeReadCount = 0;
    public static int IONodeWriteCount = 0;

    public static int nodeUpdateCount = 0;
    public static int nodeDeleteCount = 0;
    public static int nodePopCount = 0;

    public static int edgeUpdateCount = 0;
    public static int edgeDeleteCount = 0;
    public static int edgePopCount = 0;

    public static void initialize() throws IOException {
        Path pathToFile = Paths.get(EDGE_DIRECTORY);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile);
        }

        pathToFile = Paths.get(NODE_DIRECTORY);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile);
        }

        nodeRoot = new TournamentTreeNodeUtil(new File(NODE_DIRECTORY + String.format(RANGE_PATTERN, 0, NODE_SIZE)));
        edgeRoot = new TournamentTreeEdgeUtil(new File(EDGE_DIRECTORY + String.format(RANGE_PATTERN, 0, NODE_SIZE)));
    }

    public static void updateDistance(int fromNode, int toNode, double dist) throws Exception{
        nodeRoot.updateDistance(toNode, dist);
        nodeUpdateCount++;
        edgeRoot.updateDistance(fromNode, toNode, dist);
        edgeUpdateCount++;
    }

    public static void clearAll() {
        File dir = new File(NODE_DIRECTORY);
        for(File file: dir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        dir = new File(EDGE_DIRECTORY);
        for(File file: dir.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    public static TournamentNode extractMinNode() throws Exception{
        TournamentNode minNode = nodeRoot.findMin();
        nodePopCount++;
        TournamentEdge minEdge = edgeRoot.findMin();
        edgePopCount++;
        if (minNode.getDist() <= minEdge.getDist()) {
            nodeRoot.deleteElement(minNode.getNodeId());
            nodeDeleteCount++;
            return minNode;
        } else {
            edgeRoot.deleteElement(minEdge.getFromNode(), minEdge.getToNode());
            edgeDeleteCount++;
            nodeRoot.deleteElement(minEdge.getFromNode());
            nodeDeleteCount++;
            return extractMinNode();
        }
    }

    public static void empty(TournamentTreeNodeUtil tNode) throws Exception {
        String[] range = tNode.getFile().getName().split("\\.")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);
        int middle = (start + end)/2;

        Pair<TournamentTreeNodeUtil, Boolean> leftPair = getChildTreeNode(start, middle);
        TournamentTreeNodeUtil leftChild = leftPair.getKey();
        boolean leftCommit = (middle - start <= ENTRY_BLOCK_SIZE) || !leftPair.getValue();

        Pair<TournamentTreeNodeUtil, Boolean> rightPair = getChildTreeNode(middle, end);
        TournamentTreeNodeUtil rightChild = rightPair.getKey();
        boolean rightCommit = (end - middle <= ENTRY_BLOCK_SIZE) || !rightPair.getValue();

        Pair<List<OperationNode>, List<OperationNode>> ops = splitNodeOperation(tNode.getBuffer(), middle);

        List<OperationNode> leftOps = ops.getKey();
        if (!leftOps.isEmpty()) {
            if (!leftPair.getValue()) {
                leftChild.init();
            }
            process(leftChild, leftOps, leftCommit);
        }

        List<OperationNode> rightOps = ops.getValue();
        if (!rightOps.isEmpty()) {
            if (!rightPair.getValue()) {
                rightChild.init();
            }
            process(rightChild, rightOps, rightCommit);
        }

        if (leftChild.getFile().exists()) {
            leftChild.storeToFile();
            IOEdgeWriteCount += 1;
        }
        if (rightChild.getFile().exists()) {
            rightChild.storeToFile();
            IOEdgeWriteCount += 1;
        }
    }

    public static void process(TournamentTreeNodeUtil tNode, List<OperationNode> ops, boolean commit) throws Exception{
        if (commit) {
            for (OperationNode op : ops) {
                tNode.commitOp(op);
            }
        } else {
            for (OperationNode op : ops) {
                tNode.executeOp(op);
            }
        }
    }

    public static Pair<List<OperationNode>, List<OperationNode>> splitNodeOperation
            (Map<Integer, OperationNode> operations, int middle) {
        List<OperationNode> leftOps = new ArrayList<>();
        List<OperationNode> rightOps = new ArrayList<>();

        for (Map.Entry<Integer, OperationNode> entry : operations.entrySet()) {
            if (entry.getKey() < middle) {
                leftOps.add(entry.getValue());
            } else {
                rightOps.add(entry.getValue());
            }
        }
        return new Pair<>(leftOps, rightOps);
    }

    public static void empty(TournamentTreeEdgeUtil tEdge) throws Exception {
        String[] range = tEdge.getFile().getName().split("\\.")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);
        int middle = (start + end)/2;

        Pair<TournamentTreeEdgeUtil, Boolean> leftPair = getChildTreeEdge(start, middle);
        TournamentTreeEdgeUtil leftChild = leftPair.getKey();
        boolean leftCommit = (middle - start <= ENTRY_BLOCK_SIZE) || !leftPair.getValue();

        Pair<TournamentTreeEdgeUtil, Boolean> rightPair = getChildTreeEdge(middle, end);
        TournamentTreeEdgeUtil rightChild = rightPair.getKey();
        boolean rightCommit = (end - middle <= ENTRY_BLOCK_SIZE) || !rightPair.getValue();

        Pair<List<OperationEdge>, List<OperationEdge>> ops = splitEdgeOperation(tEdge.getBuffer(), middle);

        List<OperationEdge> leftOps = ops.getKey();
        if (!leftOps.isEmpty()) {
            if (!leftPair.getValue()) {
                leftChild.init();
            }
            process(leftChild, leftOps, leftCommit);
        }

        List<OperationEdge> rightOps = ops.getValue();
        if (!rightOps.isEmpty()) {
            if (!rightPair.getValue()) {
                rightChild.init();
            }
            process(rightChild, rightOps, rightCommit);
        }

        if (leftChild.getFile().exists()) {
            leftChild.storeToFile();
            IOEdgeWriteCount += 1;
        }
        if (rightChild.getFile().exists()) {
            rightChild.storeToFile();
            IOEdgeWriteCount += 1;
        }
    }

    public static void process(TournamentTreeEdgeUtil tEdge, List<OperationEdge> ops, boolean commit) throws Exception{
        if (commit) {
            for (OperationEdge op : ops) {
                tEdge.commitOp(op);
            }
        } else {
            for (OperationEdge op : ops) {
                tEdge.executeOp(op);
            }
        }
    }

    public static Pair<List<OperationEdge>, List<OperationEdge>> splitEdgeOperation
            (Map<Pair<Integer, Integer>, OperationEdge> operations, int middle) {
        List<OperationEdge> leftOps = new ArrayList<>();
        List<OperationEdge> rightOps = new ArrayList<>();

        for (Map.Entry<Pair<Integer, Integer>, OperationEdge> entry : operations.entrySet()) {
            if (entry.getKey().getKey() < middle) {
                leftOps.add(entry.getValue());
            } else {
                rightOps.add(entry.getValue());
            }
        }
        return new Pair<>(leftOps, rightOps);
    }

    public static void fillup(TournamentTreeEdgeUtil tEdge) throws Exception{
        String[] range = tEdge.getFile().getName().split("\\.")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);
        int middle = (start + end)/2;

        Pair<TournamentTreeEdgeUtil, Boolean> leftPair = getChildTreeEdge(start, middle);
        TournamentTreeEdgeUtil leftChild = leftPair.getKey();
        boolean leftCommit = (middle - start <= ENTRY_BLOCK_SIZE) || !leftPair.getValue();

        Pair<TournamentTreeEdgeUtil, Boolean> rightPair = getChildTreeEdge(middle, end);
        TournamentTreeEdgeUtil rightChild = rightPair.getKey();
        boolean rightCommit = (end - middle <= ENTRY_BLOCK_SIZE) || !rightPair.getValue();

        Pair<List<OperationEdge>, List<OperationEdge>> ops = splitEdgeOperation(tEdge.getBuffer(), middle);

        List<OperationEdge> leftOps = ops.getKey();
        if (!leftOps.isEmpty()) {
            if (!leftPair.getValue()) {
                leftChild.init();
            }
            process(leftChild, leftOps, leftCommit);
        }

        List<OperationEdge> rightOps = ops.getValue();
        if (!rightOps.isEmpty()) {
            if (!rightPair.getValue()) {
                rightChild.init();
            }
            process(rightChild, rightOps, rightCommit);
        }

        boolean isNotFull = true;
        int leftPointer = 0;
        int rightPointer = 0;
        List<TournamentEdge> leftElements = new ArrayList<>(leftChild.getElements());
        List<TournamentEdge> rightElements = new ArrayList<>(rightChild.getElements());
        if(leftElements.size() == 0 && !leftCommit) {
            fillup(leftChild);
            leftElements = new ArrayList<>(leftChild.getElements());
        }

        if(rightElements.size() == 0 && !rightCommit) {
            fillup(rightChild);
            rightElements = new ArrayList<>(rightChild.getElements());
        }

        Collections.sort(leftElements);
        Collections.sort(rightElements);

        while (isNotFull){
            if(leftPointer < leftElements.size() && rightPointer < rightElements.size()) {
                TournamentEdge left = leftElements.get(leftPointer);
                TournamentEdge right = rightElements.get(rightPointer);
                TournamentEdge next = null;
                if (left.compareTo(right) < 0) {
                    next = left;
                    leftPointer++;
                } else {
                    next = right;
                    rightPointer++;
                }
                isNotFull = tEdge.addElement(next);
            } else if(leftPointer < leftElements.size()){
                TournamentEdge next = leftElements.get(leftPointer);
                isNotFull = tEdge.addElement(next);
                leftPointer++;
            } else if(rightPointer < rightElements.size()){
                TournamentEdge next = rightElements.get(rightPointer);
                isNotFull = tEdge.addElement(next);
                rightPointer++;
            } else {
                break;
            }
        }

        tEdge.resetMinAmongChild();
        if (leftChild.getFile().exists()) {
            leftChild.storeToFile();
            IOEdgeWriteCount += 1;
        }
        if (rightChild.getFile().exists()) {
            rightChild.storeToFile();
            IOEdgeWriteCount += 1;
        }
    }

    public static void fillup(TournamentTreeNodeUtil tNode) throws Exception{
        String[] range = tNode.getFile().getName().split("\\.")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);
        int middle = (start + end)/2;

        Pair<TournamentTreeNodeUtil, Boolean> leftPair = getChildTreeNode(start, middle);
        TournamentTreeNodeUtil leftChild = leftPair.getKey();
        boolean leftCommit = (middle - start <= ENTRY_BLOCK_SIZE) || !leftPair.getValue();

        Pair<TournamentTreeNodeUtil, Boolean> rightPair = getChildTreeNode(middle, end);
        TournamentTreeNodeUtil rightChild = rightPair.getKey();
        boolean rightCommit = (end - middle <= ENTRY_BLOCK_SIZE) || !rightPair.getValue();

        Pair<List<OperationNode>, List<OperationNode>> ops = splitNodeOperation(tNode.getBuffer(), middle);

        List<OperationNode> leftOps = ops.getKey();
        if (!leftOps.isEmpty()) {
            if (!leftPair.getValue()) {
                leftChild.init();
            }
            process(leftChild, leftOps, leftCommit);
        }

        List<OperationNode> rightOps = ops.getValue();
        if (!rightOps.isEmpty()) {
            if (!rightPair.getValue()) {
                rightChild.init();
            }
            process(rightChild, rightOps, rightCommit);
        }

        boolean isNotFull = true;
        int leftPointer = 0;
        int rightPointer = 0;
        List<TournamentNode> leftElements = new ArrayList<>(leftChild.getElements());
        List<TournamentNode> rightElements = new ArrayList<>(rightChild.getElements());

        if(leftElements.size() == 0 && !leftCommit) {
            fillup(leftChild);
            leftElements = new ArrayList<>(leftChild.getElements());
        }

        if(rightElements.size() == 0 && !rightCommit) {
            fillup(rightChild);
            rightElements = new ArrayList<>(rightChild.getElements());
        }

        Collections.sort(leftElements);
        Collections.sort(rightElements);

        while (isNotFull){
            if(leftPointer < leftElements.size() && rightPointer < rightElements.size()) {
                TournamentNode left = leftElements.get(leftPointer);
                TournamentNode right = rightElements.get(rightPointer);
                TournamentNode next = null;
                if (left.compareTo(right) < 0) {
                    next = left;
                    leftPointer++;
                } else {
                    next = right;
                    rightPointer++;
                }
                isNotFull = tNode.addElement(next);
            } else if(leftPointer < leftElements.size()){
                TournamentNode next = leftElements.get(leftPointer);
                isNotFull = tNode.addElement(next);
                leftPointer++;
            } else if(rightPointer < rightElements.size()){
                TournamentNode next = rightElements.get(rightPointer);
                isNotFull = tNode.addElement(next);
                rightPointer++;
            } else {
                break;
            }
        }

        tNode.resetMinAmongChild();
        if (leftChild.getFile().exists()) {
            leftChild.storeToFile();
            IONodeWriteCount += 1;
        }
        if (rightChild.getFile().exists()) {
            rightChild.storeToFile();
            IONodeWriteCount += 1;
        }
    }

    private static Pair<TournamentTreeNodeUtil, Boolean> getChildTreeNode(int start, int end) throws Exception{
        File file = new File(NODE_DIRECTORY + String.format(RANGE_PATTERN, start, end));
        boolean exist;

        TournamentTreeNodeUtil node = new TournamentTreeNodeUtil(file);
        if (file.exists()) {
            node.readFromFile();
            IONodeReadCount++;
            exist = true;
        } else {
            exist = false;
        }

        return new Pair<>(node, exist);
    }

    private static Pair<TournamentTreeEdgeUtil, Boolean> getChildTreeEdge(int start, int end) throws Exception{
        File file = new File(EDGE_DIRECTORY + String.format(RANGE_PATTERN, start, end));
        boolean exist;

        TournamentTreeEdgeUtil edge = new TournamentTreeEdgeUtil(file);
        if (file.exists()) {
            edge.readFromFile();
            IOEdgeReadCount++;
            exist = true;
        } else {
            exist = false;
        }

        return new Pair<>(edge, exist);
    }
}
