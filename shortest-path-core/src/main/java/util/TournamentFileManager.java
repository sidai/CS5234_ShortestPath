package util;

import com.sun.tools.javac.util.Assert;
import vo.TournamentEdge;
import vo.TournamentNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TournamentFileManager {

    private static TournamentTreeNodeUtil nodeRoot;
    private static TournamentTreeEdgeUtil edgeRoot;
    private static int ENTRY_BLOCK_SIZE = 3200;
    private static int NODE_SIZE = 2675656;
    private static int EDGE_SIZE = 3602918;
    private static String EDGE_DIRECTORY = "./map-data/edge-pq/";
    private static String NODE_DIRECTORY = "./map-data/node-pq/";
    private static String RANGE_PATTERN = "%d-%d.txt";
    private static int IOReadCount;
    private static int IOWriteCount;


    public static void initialize() throws IOException {
        Path pathToFile = Paths.get(EDGE_DIRECTORY);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
        }

        pathToFile = Paths.get(NODE_DIRECTORY);
        if(!Files.exists(pathToFile)) {
            Files.createDirectories(pathToFile.getParent());
        }

        nodeRoot = new TournamentTreeNodeUtil(createFile(NODE_DIRECTORY + String.format(RANGE_PATTERN, 0, 4194304)));
        edgeRoot = new TournamentTreeEdgeUtil(createFile(EDGE_DIRECTORY + String.format(RANGE_PATTERN, 0, 4194304)));
    }

    private static File createFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static void updateDistance(int fromNode, int toNode, double dist) {
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

    public static TournamentNode extractMinNode() {
        TournamentNode minNode = nodeRoot.findMin();
        TournamentEdge minEdge = edgeRoot.findMin();

        return nodeRoot.findMin();
    }


    public static void empty(TournamentTreeNodeUtil curr) {

    }
    public static void empty(TournamentTreeEdgeUtil curr) {

    }

    public static void fillup(TournamentTreeNodeUtil curr) {

    }

    public static void fillup(TournamentTreeEdgeUtil curr) {

    }

    public static int getIOReadCount() {
        return IOReadCount;
    }

    public static int getIOWriteCount() {
        return IOWriteCount;
    }
}
