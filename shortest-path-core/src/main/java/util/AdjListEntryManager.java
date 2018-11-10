package util;

import com.sun.tools.javac.util.Assert;
import vo.Neighbor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class AdjListEntryManager {

//    private static int ENTRY_BLOCK_SIZE = 3200;
//    private static int NODE_SIZE = 2675656;
//    private static String DIRECTORY = "./map-data/adjacency-list/";
//    private static String NAME_PATTERN = "ADJACENCY_LIST_ENTRY_[%d-%d).csv";


    private static int ENTRY_BLOCK_SIZE = 10;
    private static int NODE_SIZE = 27;
    private static String DIRECTORY = "./map-data/test-list/";
    private static String NAME_PATTERN = "ADJACENCY_LIST_ENTRY_[%d-%d).csv";

    Map<File, AdjListEntryBlockUtil> blockMap;
    AdjListEntryIndex adjListEntryIndex;

    public AdjListEntryManager() throws Exception {
        blockMap = new HashMap<>();
        adjListEntryIndex = new AdjListEntryIndex();
        adjListEntryIndex.initEntryIndex(NODE_SIZE, ENTRY_BLOCK_SIZE, DIRECTORY, NAME_PATTERN);
    }

    public void addNeighbor(int nodeId, Neighbor neighbor) {
        File identifier = adjListEntryIndex.getBlockIdentifier(nodeId);
        if (!blockMap.containsKey(identifier)) {
            blockMap.put(identifier, new AdjListEntryBlockUtil());
        }
        blockMap.get(identifier).addNeighbors(nodeId, neighbor);
    }

    public void storeAllBlock() throws Exception {
        for(File identifier: blockMap.keySet()) {
            blockMap.get(identifier).storeToFile(identifier);
        }
    }

    public List<Neighbor> readAdjListEntry(int nodeId) throws Exception {
        File identifier = adjListEntryIndex.getBlockIdentifier(nodeId);
        if (!blockMap.containsKey(identifier)) {
            blockMap.put(identifier, new AdjListEntryBlockUtil());
        }

        return blockMap.get(identifier).readFromFile(identifier).get(nodeId);
    }

    public Map<File, AdjListEntryBlockUtil> getBlockMap() {
        return blockMap;
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
