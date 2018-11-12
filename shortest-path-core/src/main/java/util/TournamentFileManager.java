package util;

import com.sun.tools.javac.util.Assert;
import vo.TournamentEdge;
import vo.TournamentNode;
import vo.OperationNode;
import vo.OperationEdge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

import javafx.util.Pair;

public class TournamentFileManager {

    private static TournamentTreeNodeUtil nodeRoot;
    private static TournamentTreeEdgeUtil edgeRoot;
    private static int ENTRY_BLOCK_SIZE = 3200;
    private static int NODE_SIZE = 2675656;
    private static int EDGE_SIZE = 3602918;
    private static String EDGE_DIRECTORY = "./../map-data/edge-pq/";
    private static String NODE_DIRECTORY = "./../map-data/node-pq/";
    private static String RANGE_PATTERN = "%d-%d.csv";

    public static int IOEdgeReadCount = 0;
    public static int IOEdgeWriteCount = 0;

    public static int IONodeReadCount = 0;
    public static int IONodeWriteCount = 0;


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
        edgeRoot.updateDistance(fromNode, toNode, dist);
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
        TournamentEdge minEdge = edgeRoot.findMin();
        System.out.println(minEdge.getDist() +", " + minNode.getDist());
        if (minNode.getDist() <= minEdge.getDist()) {
            nodeRoot.deleteElement(minNode.getNodeId());
            System.out.println("Smaller");
            return minNode;
        } else {
            edgeRoot.deleteElement(minEdge.getFromNode(), minEdge.getToNode());
            nodeRoot.deleteElement(minEdge.getFromNode());
            return extractMinNode();
        }
    }


//    public static void empty(TournamentTreeNodeUtil tNode) throws Exception{
//        String[] range = tNode.getFileName().split(".")[0].split("-");
//        int start = Integer.parseInt(range[0]);
//        int end = Integer.parseInt(range[1]);
//
////        Map<Integer, TournamentNode> elements = tNode.getElementsRef();
//        Map<Integer, OperationNode> operations = tNode.getBuffer();
//
//        if(end - start > ENTRY_BLOCK_SIZE){
//
//            TournamentTreeNodeUtil leftTNode = getLeftChild(tNode);
//            TournamentTreeNodeUtil rightTNode = getRightChild(tNode);
//
////            int leftStart = start;
//            int leftEnd = start + (end-start)/2;
//
////            int rightStart = leftEnd;
////            int rightEnd = end;
//
////            Map<Integer, TournamentNode> leftElements = leftTNode.getElementsRef();
////            Map<Integer, TournamentNode> rightElements = rightTNode.getElementsRef();
////
////            Map<Integer, OperationNode> leftOperations = leftTNode.getBuffer();
////            Map<Integer, OperationNode> rightOperations = rightTNode.getBuffer();
//
////            for(Map.Entry<Integer, TournamentNode> entry: elements.entrySet()){
////                if(entry.getKey()<leftEnd){
////                    leftElements.put(entry.getKey(),entry.getValue());
////                }else{
////                    rightElements.put(entry.getKey(),entry.getValue());
////                }
////            }
//
////            for(Map.Entry<Integer, OperationNode> entry: operations.entrySet()){
////                if(entry.getKey()<leftEnd){
////                    leftOperations.put(entry.getKey(), entry.getValue());
////                }else{
////                    rightOperations.put(entry.getKey(), entry.getValue());
////                }
////            }
//
//            for (Map.Entry<Integer, OperationNode> entry: operations.entrySet()) {
//                if(entry.getKey() < leftEnd){
//                    leftTNode.executeOp(entry.getValue());
//                } else{
//                    rightTNode.executeOp(entry.getValue());
//                }
//            }
//
//            tNode.setBuffer(new HashMap<>()); // clear all operation
//
////            leftTNode.setElementsRef(leftElements);
////            leftTNode.setBuffer(leftOperations);
//
////            rightTNode.setElementsRef(rightElements);
////            rightTNode.setBuffer(rightOperations);
//
//            tNode.storeToFile();
//            IONodeWriteCount++;
//
//            if(leftTNode.isFull()){
//                empty(leftTNode);
//            }
//            if(rightTNode.isFull()){
//                empty(rightTNode);
//            }
//
//            leftTNode.storeToFile();
//            rightTNode.storeToFile();
//            IONodeWriteCount+=2;
//        }
//
//    }
//    public static void empty(TournamentTreeEdgeUtil tEdge) throws Exception{
//        String[] range = tEdge.getFileName().split(".")[0].split("-");
//        int start = Integer.parseInt(range[0]);
//        int end = Integer.parseInt(range[1]);
//
////        Map<Pair<Integer, Integer>, TournamentEdge> elements = tEdge.getElementsRef();
//        Map<Pair<Integer, Integer>, OperationEdge> operations = tEdge.getBuffer();
//
//        if(end-start > ENTRY_BLOCK_SIZE){
//
//            TournamentTreeEdgeUtil leftTEdge = getLeftChild(tEdge);
//            TournamentTreeEdgeUtil rightTEdge = getRightChild(tEdge);
//
////            int leftStart = start;
//            int leftEnd = start+ (end-start)/2;
////
////            int rightStart = leftEnd;
////            int rightEnd = end;
////
////            Map<Pair<Integer, Integer>, TournamentEdge> leftElements = leftTNode.getElementsRef();
////            Map<Pair<Integer, Integer>, TournamentEdge> rightElements = rightTNode.getElementsRef();
////
////            Map<Pair<Integer, Integer>, OperationEdge> leftOperations = leftTNode.getBuffer();
////            Map<Pair<Integer, Integer>, OperationEdge> rightOperations = rightTNode.getBuffer();
////
////            for(Map.Entry<Pair<Integer, Integer>, TournamentEdge> entry: elements.entrySet()){
////                if(entry.getKey().getKey()<leftEnd){
////                    leftElements.put(entry.getKey(),entry.getValue());
////                }else{
////                    rightElements.put(entry.getKey(),entry.getValue());
////                }
////            }
////
////            for(Map.Entry<Pair<Integer, Integer>, OperationEdge> entry: operations.entrySet()){
////                if(entry.getKey().getKey()<leftEnd){
////                    leftOperations.put(entry.getKey(), entry.getValue());
////                }else{
////                    rightOperations.put(entry.getKey(), entry.getValue());
////                }
////            }
//            for (Map.Entry<Pair<Integer, Integer>, OperationEdge> entry: operations.entrySet()) {
//                if(entry.getKey().getKey() < leftEnd){
//                    leftTEdge.executeOp(entry.getValue());
//                } else{
//                    rightTEdge.executeOp(entry.getValue());
//                }
//            }
//
//
//            tEdge.setBuffer(new HashMap<>()); // clear all operation
//
////            leftTNode.setElementsRef(leftElements);
////            leftTNode.setBuffer(leftOperations);
////
////            rightTNode.setElementsRef(rightElements);
////            rightTNode.setBuffer(rightOperations);
//
//            tEdge.storeToFile();
//            IOEdgeWriteCount++;
//
//            if(leftTEdge.isFull()){
//                empty(leftTEdge);
//            }
//            if(rightTEdge.isFull()){
//                empty(rightTEdge);
//            }
//
//            leftTEdge.storeToFile();
//            rightTEdge.storeToFile();
//            IOEdgeWriteCount+=2;
//
//        }
//    }

    public static void empty(TournamentTreeNodeUtil tNode, TournamentTreeNodeUtil leftChild,
                             TournamentTreeNodeUtil rightChild, int middle, boolean commit) throws Exception{
        Map<Integer, OperationNode> operations = tNode.getBuffer();
        if (commit) {
            for (Map.Entry<Integer, OperationNode> entry : operations.entrySet()) {
                if (entry.getKey() < middle) {
                    leftChild.commitOp(entry.getValue());
                } else {
                    rightChild.commitOp(entry.getValue());
                }
            }
        } else {
            for (Map.Entry<Integer, OperationNode> entry : operations.entrySet()) {
                if (entry.getKey() < middle) {
                    leftChild.executeOp(entry.getValue());
                } else {
                    rightChild.executeOp(entry.getValue());
                }
            }
        }

        tNode.setBuffer(new HashMap<>()); // clear all operation
    }

    public static void empty(TournamentTreeNodeUtil tNode) throws Exception {
        String[] range = tNode.getFileName().split(".")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);

        int middle = (start + end)/2;

        Pair<TournamentTreeNodeUtil, Boolean> leftPair = getChildTreeNode(start, middle);
        Pair<TournamentTreeNodeUtil, Boolean> rightPair = getChildTreeNode(middle, end);

        TournamentTreeNodeUtil leftChild = leftPair.getKey();
        TournamentTreeNodeUtil rightChild = rightPair.getKey();
        boolean commit = (end - start <= 2*ENTRY_BLOCK_SIZE) || leftPair.getValue();

        empty(tNode, leftChild, rightChild, middle, commit);

        leftChild.storeToFile();
        rightChild.storeToFile();
        IOEdgeWriteCount+=2;
    }

    public static void empty(TournamentTreeEdgeUtil tEdge, TournamentTreeEdgeUtil leftChild,
                             TournamentTreeEdgeUtil rightChild, int middle, boolean commit) throws Exception{
        Map<Pair<Integer, Integer>, OperationEdge> operations = tEdge.getBuffer();
        if (commit) {
            for (Map.Entry<Pair<Integer, Integer>, OperationEdge> entry : operations.entrySet()) {
                if (entry.getKey().getKey() < middle) {
                    leftChild.commitOp(entry.getValue());
                } else {
                    rightChild.commitOp(entry.getValue());
                }
            }
        } else {
            for (Map.Entry<Pair<Integer, Integer>, OperationEdge> entry : operations.entrySet()) {
                if (entry.getKey().getKey() < middle) {
                    leftChild.executeOp(entry.getValue());
                } else {
                    rightChild.executeOp(entry.getValue());
                }
            }
        }

        tEdge.setBuffer(new HashMap<>()); // clear all operation
    }

    public static void empty(TournamentTreeEdgeUtil tEdge) throws Exception {
        String[] range = tEdge.getFileName().split(".")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);
        int middle = (start + end)/2;

        Pair<TournamentTreeEdgeUtil, Boolean> leftPair = getChildTreeEdge(start, middle);
        Pair<TournamentTreeEdgeUtil, Boolean> rightPair = getChildTreeEdge(middle, end);

        TournamentTreeEdgeUtil leftChild = leftPair.getKey();
        TournamentTreeEdgeUtil rightChild = rightPair.getKey();
        boolean commit = (end - start <= 2*ENTRY_BLOCK_SIZE) || leftPair.getValue();

        empty(tEdge, leftChild, rightChild, middle, commit);

        leftChild.storeToFile();
        rightChild.storeToFile();
        IOEdgeWriteCount+=2;
    }

    public static void fillup(TournamentTreeEdgeUtil tEdge) throws Exception{
        String[] range = tEdge.getFileName().split("\\.")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);
        int middle = (start + end)/2;

        Pair<TournamentTreeEdgeUtil, Boolean> leftPair = getChildTreeEdge(start, middle);
        Pair<TournamentTreeEdgeUtil, Boolean> rightPair = getChildTreeEdge(middle, end);

        TournamentTreeEdgeUtil leftChild = leftPair.getKey();
        TournamentTreeEdgeUtil rightChild = rightPair.getKey();

        boolean commit = (end - start <= 2*ENTRY_BLOCK_SIZE) || leftPair.getValue();
        empty(tEdge, leftChild, rightChild, middle, commit);

        boolean isNotFull = true;
        int leftPointer = 0;
        int rightPointer = 0;
        List<TournamentEdge> leftElements = new ArrayList<>(leftChild.getElements());
        List<TournamentEdge> rightElements = new ArrayList<>(rightChild.getElements());
        if(leftElements.size()==0){
            if(!commit) {
                fillup(leftChild);
                leftElements = new ArrayList<>(leftChild.getElements());
            }
        }
        if(rightElements.size()==0){
            if(!commit) {
                fillup(rightChild);
                rightElements = new ArrayList<>(rightChild.getElements());
            }
        }
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
        leftChild.storeToFile();
        rightChild.storeToFile();
        IONodeWriteCount+=2;
    }

    public static void fillup(TournamentTreeNodeUtil tNode) throws Exception{
        String[] range = tNode.getFileName().split("\\.")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);
        int middle = (start + end)/2;

        Pair<TournamentTreeNodeUtil, Boolean> leftPair = getChildTreeNode(start, middle);
        Pair<TournamentTreeNodeUtil, Boolean> rightPair = getChildTreeNode(middle, end);

        TournamentTreeNodeUtil leftChild = leftPair.getKey();
        TournamentTreeNodeUtil rightChild = rightPair.getKey();

        boolean commit = (end - start <= 2*ENTRY_BLOCK_SIZE) || leftPair.getValue();
        empty(tNode, leftChild, rightChild, middle, commit);

        boolean isNotFull = true;
        int leftPointer = 0;
        int rightPointer = 0;
        List<TournamentNode> leftElements = new ArrayList<>(leftChild.getElements());
        List<TournamentNode> rightElements = new ArrayList<>(rightChild.getElements());
        if(leftElements.size()==0){
            if(!commit) {
                fillup(leftChild);
                leftElements = new ArrayList<>(leftChild.getElements());
            }
        }
        if(rightElements.size()==0){
            if(!commit) {
                fillup(rightChild);
                rightElements = new ArrayList<>(rightChild.getElements());
            }
        }
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
        leftChild.storeToFile();
        rightChild.storeToFile();
        IONodeWriteCount+=2;
    }

//    public static void fillup(TournamentTreeNodeUtil tNode) throws Exception{
//        empty(tNode);
//        TournamentTreeNodeUtil leftChild = getLeftChild(tNode);
//        TournamentTreeNodeUtil rightChild = getRightChild(tNode);
//
//        boolean isNotFull = true;
//        int leftPointer = 0;
//        int rightPointer = 0;
//        List<TournamentNode> leftElements = new ArrayList<>(leftChild.getElements());
//        List<TournamentNode> rightElements = new ArrayList<>(rightChild.getElements());
//        if(leftElements.size()==0){
//            String[] range = leftChild.getFileName().split(".")[0].split("-");
//            int start = Integer.parseInt(range[0]);
//            int end = Integer.parseInt(range[1]);
//            if(end-start > ENTRY_BLOCK_SIZE) {
//                fillup(leftChild);
//                leftElements = new ArrayList<>(leftChild.getElements());
//            }
//        }
//        if(rightElements.size()==0){
//            String[] range = rightChild.getFileName().split(".")[0].split("-");
//            int start = Integer.parseInt(range[0]);
//            int end = Integer.parseInt(range[1]);
//            if(end-start > ENTRY_BLOCK_SIZE) {
//                fillup(rightChild);
//                rightElements = new ArrayList<>(rightChild.getElements());
//            }
//        }
//        while (isNotFull){
//            if(leftPointer < leftElements.size() && rightPointer < rightElements.size()) {
//                TournamentNode left = leftElements.get(leftPointer);
//                TournamentNode right = rightElements.get(rightPointer);
//                TournamentNode next = null;
//                if (left.compareTo(right) < 0) {
//                    next = left;
//                    leftPointer++;
//                } else {
//                    next = right;
//                    rightPointer++;
//                }
//                isNotFull = tNode.addElement(next);
//            }else if(leftPointer < leftElements.size()){
//                TournamentNode next = leftElements.get(leftPointer);
//                isNotFull = tNode.addElement(next);
//                leftPointer++;
//            }else if(rightPointer < rightElements.size()){
//                TournamentNode next = rightElements.get(rightPointer);
//                isNotFull = tNode.addElement(next);
//                rightPointer++;
//            }
//
//        }
//        tNode.storeToFile();
//        IONodeWriteCount++;
//    }
//
//    public static void fillup(TournamentTreeEdgeUtil tEdge) throws Exception{
//        empty(tEdge);
//        TournamentTreeEdgeUtil leftChild = getLeftChild(tEdge);
//        TournamentTreeEdgeUtil rightChild = getRightChild(tEdge);
//
//
//        boolean isNotFull = true;
//        int leftPointer = 0;
//        int rightPointer = 0;
//        List<TournamentEdge> leftElements = new ArrayList<>(leftChild.getElements());
//        List<TournamentEdge> rightElements = new ArrayList<>(rightChild.getElements());
//        if(leftElements.size()==0){
//            String[] range = leftChild.getFileName().split(".")[0].split("-");
//            int start = Integer.parseInt(range[0]);
//            int end = Integer.parseInt(range[1]);
//            if(end-start > ENTRY_BLOCK_SIZE) {
//                fillup(leftChild);
//                leftElements = new ArrayList<>(leftChild.getElements());
//            }
//        }
//        if(rightElements.size()==0){
//            String[] range = rightChild.getFileName().split(".")[0].split("-");
//            int start = Integer.parseInt(range[0]);
//            int end = Integer.parseInt(range[1]);
//            if(end-start > ENTRY_BLOCK_SIZE) {
//                fillup(rightChild);
//                rightElements = new ArrayList<>(rightChild.getElements());
//            }
//        }
//        while (isNotFull){
//            if(leftPointer < leftElements.size() && rightPointer < rightElements.size()) {
//                TournamentEdge left = leftElements.get(leftPointer);
//                TournamentEdge right = rightElements.get(rightPointer);
//                TournamentEdge next = null;
//                if (left.compareTo(right) < 0) {
//                    next = left;
//                    leftPointer++;
//                } else {
//                    next = right;
//                    rightPointer++;
//                }
//                isNotFull = tEdge.addElement(next);
//            }else if(leftPointer < leftElements.size()){
//                TournamentEdge next = leftElements.get(leftPointer);
//                isNotFull = tEdge.addElement(next);
//                leftPointer++;
//            }else if(rightPointer < rightElements.size()){
//                TournamentEdge next = rightElements.get(rightPointer);
//                isNotFull = tEdge.addElement(next);
//                rightPointer++;
//            }
//
//        }
//        tEdge.storeToFile();
//        IOEdgeWriteCount++;
//    }

    private static Pair<TournamentTreeNodeUtil, Boolean> getChildTreeNode(int start, int end) throws Exception{
        File file = new File(NODE_DIRECTORY + String.format(RANGE_PATTERN, start, end));
        boolean firstInit;

        TournamentTreeNodeUtil node = new TournamentTreeNodeUtil(file);
        if (file.exists()) {
            node.readFromFile();
            IONodeReadCount++;
            firstInit = false;
        } else {
            file.createNewFile();
            firstInit = true;
        }

        return new Pair<>(node, firstInit);
    }

    private static Pair<TournamentTreeEdgeUtil, Boolean> getChildTreeEdge(int start, int end) throws Exception{
        File file = new File(EDGE_DIRECTORY + String.format(RANGE_PATTERN, start, end));
        boolean firstInit;

        TournamentTreeEdgeUtil edge = new TournamentTreeEdgeUtil(file);
        if (file.exists()) {
            edge.readFromFile();
            IOEdgeReadCount++;
            firstInit = false;
        } else {
            file.createNewFile();
            firstInit = true;
        }

        return new Pair<>(edge, firstInit);
    }

    private static TournamentTreeNodeUtil getLeftChild(TournamentTreeNodeUtil tNode) throws Exception{
        String[] range = tNode.getFileName().split(".")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);

        int leftStart = start;
        int leftEnd = start + (end-start)/2;

        File file = new File(NODE_DIRECTORY + String.format(RANGE_PATTERN, leftStart, leftEnd));

        TournamentTreeNodeUtil leftTNode = new TournamentTreeNodeUtil(file);
        if (file.exists()) {
            leftTNode.readFromFile();
            IONodeReadCount++;
        }

        return leftTNode;

    }
    private static TournamentTreeNodeUtil getRightChild(TournamentTreeNodeUtil tNode) throws Exception{
        String[] range = tNode.getFileName().split(".")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);

        int rightStart = start + (end-start)/2;
        int rightEnd = end;

        File file = new File(NODE_DIRECTORY + String.format(RANGE_PATTERN, rightStart, rightEnd));

        TournamentTreeNodeUtil rightTNode = new TournamentTreeNodeUtil(file);
        if (file.exists()) {
            rightTNode.readFromFile();
            IONodeReadCount++;
        }

        return rightTNode;
    }

    private static TournamentTreeEdgeUtil getLeftChild(TournamentTreeEdgeUtil tEdge) throws Exception{
        String[] range = tEdge.getFileName().split(".")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);

        int leftStart = start;
        int leftEnd = start+ (end-start)/2;

        File file = new File(NODE_DIRECTORY + String.format(RANGE_PATTERN, leftStart, leftEnd));

        TournamentTreeEdgeUtil leftTNode = new TournamentTreeEdgeUtil(file);
        if (file.exists()) {
            leftTNode.readFromFile();
            IOEdgeReadCount++;
        }

        return leftTNode;
    }

    private static TournamentTreeEdgeUtil getRightChild(TournamentTreeEdgeUtil tEdge) throws Exception{
        String[] range = tEdge.getFileName().split(".")[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);

        int rightStart = start+ (end-start)/2;
        int rightEnd = end;

        File file = new File(NODE_DIRECTORY + String.format(RANGE_PATTERN, rightStart, rightEnd));

        TournamentTreeEdgeUtil rightTNode = new TournamentTreeEdgeUtil(file);
        if (file.exists()) {
            rightTNode.readFromFile();
            IOEdgeReadCount++;
        }

        return rightTNode;
    }
}
