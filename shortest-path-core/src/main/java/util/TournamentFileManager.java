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
    private static int NODE_SIZE = 93149;
    private static int EDGE_SIZE = 232362;
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

    public static void fillup(TournamentTreeNodeUtil tNode) throws Exception {
        String[] range = tNode.getFile().getName().split("\\.")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);
        int middle = (start + end)/2;

        if(tNode.getBuffer().isEmpty() && hasNoChild(start, end)) {
            return;
        }

        //Empty Operation
        TournamentTreeNodeUtil leftChild = getChildTreeNode(start, middle);
        TournamentTreeNodeUtil rightChild = getChildTreeNode(middle, end);

        if (!tNode.getBuffer().isEmpty()) {
            //Flush operation to children
            Pair<List<OperationNode>, List<OperationNode>> ops = splitNodeOperation(tNode.getBuffer(), middle);
            List<OperationNode> leftOps = ops.getKey();
            if (!leftOps.isEmpty()) {
                process(leftChild, leftOps, isLeaf(start, middle) || leftChild.isEmpty());
            }

            List<OperationNode> rightOps = ops.getValue();
            if (!rightOps.isEmpty()) {
                process(rightChild, rightOps, isLeaf(middle, end) || rightChild.isEmpty());
            }
            tNode.resetBuffer();
        }

        //fill up until reach maximum size or no elements in children
        while (!tNode.isFull()){
            TournamentNode left = leftChild.findMin();
            TournamentNode right = rightChild.findMin();
            // no more elements in children
            if(left == null && right == null) {
                break;
            } else if(left == null) {
                tNode.addElement(rightChild.extractMin());
            } else if(right == null) {
                tNode.addElement(leftChild.extractMin());
            } else {
                if (left.getDist() <= right.getDist()) {
                    tNode.addElement(leftChild.extractMin());
                } else {
                    tNode.addElement(rightChild.extractMin());
                }
            }
        }
        tNode.resetMinAmongChild();

        if(leftChild.isEmpty()) {
            leftChild.destory();
        } else  {
            leftChild.storeToFile();
            IONodeWriteCount += 1;
        }

        if(rightChild.isEmpty()) {
            rightChild.destory();
        } else  {
            rightChild.storeToFile();
            IONodeWriteCount += 1;
        }
    }

    public static void fillup(TournamentTreeEdgeUtil tEdge) throws Exception {
        String[] range = tEdge.getFile().getName().split("\\.")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);
        int middle = (start + end)/2;

        if(tEdge.getBuffer().isEmpty() && hasNoChild(start, end)) {
            return;
        }

        //Empty Operation
        TournamentTreeEdgeUtil leftChild = getChildTreeEdge(start, middle);
        TournamentTreeEdgeUtil rightChild = getChildTreeEdge(middle, end);

        if (!tEdge.getBuffer().isEmpty()) {
            //Flush operation to children
            Pair<List<OperationEdge>, List<OperationEdge>> ops = splitEdgeOperation(tEdge.getBuffer(), middle);
            List<OperationEdge> leftOps = ops.getKey();
            if (!leftOps.isEmpty()) {
                process(leftChild, leftOps, isLeaf(start, middle) || leftChild.isEmpty());
            }

            List<OperationEdge> rightOps = ops.getValue();
            if (!rightOps.isEmpty()) {
                process(rightChild, rightOps, isLeaf(middle, end) || rightChild.isEmpty());
            }
            tEdge.resetBuffer();
        }

        //fill up until reach maximum size or no elements in children
        while (!tEdge.isFull()){
            TournamentEdge left = leftChild.findMin();
            TournamentEdge right = rightChild.findMin();
            // no more elements in children
            if(left == null && right == null) {
                break;
            } else if(left == null) {
                tEdge.addElement(rightChild.extractMin());
            } else if(right == null) {
                tEdge.addElement(leftChild.extractMin());
            } else {
                if (left.getDist() <= right.getDist()) {
                    tEdge.addElement(leftChild.extractMin());
                } else {
                    tEdge.addElement(rightChild.extractMin());
                }
            }
        }
        tEdge.resetMinAmongChild();

        if(leftChild.isEmpty()) {
            leftChild.destory();
        } else {
            leftChild.storeToFile();
            IOEdgeWriteCount += 1;
        }

        if(rightChild.isEmpty()) {
            rightChild.destory();
        } else {
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

//    public static void fillup(TournamentTreeNodeUtil tNode) throws Exception{
//        String[] range = tNode.getFile().getName().split("\\.")[0].split("-");
//        int start = Integer.parseInt(range[0]);
//        int end = Integer.parseInt(range[1]);
//        int middle = (start + end)/2;
//
//        TournamentTreeNodeUtil leftChild = getChildTreeNode(start, middle);
//        TournamentTreeNodeUtil rightChild = getChildTreeNode(middle, end);
//
//        boolean leftIsLeaf = hasNoChild(start, middle);
//        boolean rightIsLeaf = hasNoChild(middle, end);
//
//        if (tNode.getBuffer().isEmpty()) {
//            if (leftIsLeaf && rightIsLeaf){
//                return;
//            }
//        } else {
//            //Flush operation to children
//            Pair<List<OperationNode>, List<OperationNode>> ops = splitNodeOperation(tNode.getBuffer(), middle);
//            List<OperationNode> leftOps = ops.getKey();
//            if (!leftOps.isEmpty()) {
//                process(leftChild, leftOps, leftIsLeaf);
//            }
//
//            List<OperationNode> rightOps = ops.getValue();
//            if (!rightOps.isEmpty()) {
//                process(rightChild, rightOps, rightIsLeaf);
//            }
//            tNode.resetBuffer();
//        }
//
//        boolean isNotFull = true;
//        int leftPointer = 0;
//        int rightPointer = 0;
//        List<TournamentNode> leftElements = new ArrayList<>(leftChild.getElements());
//        List<TournamentNode> rightElements = new ArrayList<>(rightChild.getElements());
//
//        if(leftElements.size() == 0 && !leftCommit) {
//            fillup(leftChild);
//            leftElements = new ArrayList<>(leftChild.getElements());
//        }
//
//        if(rightElements.size() == 0 && !rightCommit) {
//            fillup(rightChild);
//            rightElements = new ArrayList<>(rightChild.getElements());
//        }
//
//        leftElements.sort(Comparator.comparingDouble(TournamentNode::getDist));
//        rightElements.sort(Comparator.comparingDouble(TournamentNode::getDist));
//
//        while (isNotFull){
//            if(leftPointer < leftElements.size() && rightPointer < rightElements.size()) {
//                TournamentNode left = leftElements.get(leftPointer);
//                TournamentNode right = rightElements.get(rightPointer);
//                TournamentNode next = null;
//                if (left.getDist() <= right.getDist()) {
//                    next = left;
//                    leftPointer++;
//                } else {
//                    next = right;
//                    rightPointer++;
//                }
//                isNotFull = tNode.addElement(next);
//            } else if(leftPointer < leftElements.size()){
//                TournamentNode next = leftElements.get(leftPointer);
//                isNotFull = tNode.addElement(next);
//                leftPointer++;
//            } else if(rightPointer < rightElements.size()){
//                TournamentNode next = rightElements.get(rightPointer);
//                isNotFull = tNode.addElement(next);
//                rightPointer++;
//            } else {
//                break;
//            }
//        }
//
//        tNode.resetMinAmongChild();
//        if (leftChild.getFile().exists()) {
//            leftChild.storeToFile();
//            IONodeWriteCount += 1;
//        }
//        if (rightChild.getFile().exists()) {
//            rightChild.storeToFile();
//            IONodeWriteCount += 1;
//        }
//    }

    private static TournamentTreeNodeUtil getChildTreeNode(int start, int end) throws Exception{
        File file = new File(NODE_DIRECTORY + String.format(RANGE_PATTERN, start, end));

        TournamentTreeNodeUtil node = new TournamentTreeNodeUtil(file);
        if (file.exists()) {
            node.readFromFile();
            IONodeReadCount++;
        }

        return node;

    }

    private static TournamentTreeEdgeUtil getChildTreeEdge(int start, int end) throws Exception{
        File file = new File(EDGE_DIRECTORY + String.format(RANGE_PATTERN, start, end));

        TournamentTreeEdgeUtil edge = new TournamentTreeEdgeUtil(file);
        if (file.exists()) {
            edge.readFromFile();
            IOEdgeReadCount++;
        }

        return edge;
    }

    private static boolean hasNoChild(int start, int end) {
        int middle = (start+end)/2;
        File leftChild = new File(EDGE_DIRECTORY + String.format(RANGE_PATTERN, start, middle));
        File rightChild = new File(EDGE_DIRECTORY + String.format(RANGE_PATTERN, middle, end));

        return !leftChild.exists() && !rightChild.exists();
    }

    private static boolean isLeaf(int start, int end) {
        return (end - start <= ConfigManager.getMemorySize());
    }
}
