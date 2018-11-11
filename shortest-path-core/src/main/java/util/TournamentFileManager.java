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



    public static class AdjListEntryIndex {
        private Map<Integer, File> ENTRY_INDEX;

        public AdjListEntryIndex() {
            ENTRY_INDEX = new HashMap<>();
        }

        public File getBlockIdentifier(int nodeId) {
            int fileId = getMapIdInt(nodeId);
            Assert.check(ENTRY_INDEX.containsKey(fileId), nodeId);

            return ENTRY_INDEX.get(fileId);
        }

        public void initEntryIndex(int nodeSize, int entryBlockSize, String directory, String pattern) throws Exception {

            Path pathToDirectory = Paths.get(directory);
            if (!Files.exists(pathToDirectory)) {
                Files.createDirectories(pathToDirectory);
            }

            for (int fileId = 0; fileId <= Math.ceil(nodeSize / entryBlockSize); fileId++) {
                File file = new File(directory + getMapFileName(pattern, fileId));
                if (!file.exists()) {
                    file.createNewFile();
                }
                ENTRY_INDEX.put(fileId, file);
            }
        }

        private int getMapIdInt(int nodeId) {
            Assert.check(nodeId < NODE_SIZE);

            return nodeId / ENTRY_BLOCK_SIZE;
        }

        private String getMapFileName(String pattern, int fileId) {
            int from = fileId * ENTRY_BLOCK_SIZE;
            int to = (fileId + 1) * ENTRY_BLOCK_SIZE;
            return String.format(pattern, from, to);
        }
    }
}
