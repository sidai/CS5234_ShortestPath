package util;

public class ConfigManager {

    private static int MEMORY_SIZE = 200;
    private static int NODE_SIZE = 2675656;

    public static int getMemorySize() {
        return MEMORY_SIZE;
    }

    public static void setMemorySize(int memorySize) {
        MEMORY_SIZE = memorySize;
    }

    public static int getNodeSize() {
        return NODE_SIZE;
    }

    public static void setNodeSize(int nodeSize) {
        NODE_SIZE = nodeSize;
    }
}
