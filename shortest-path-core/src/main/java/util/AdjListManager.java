package util;

import com.opencsv.CSVReader;
import vo.Neighbor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdjListManager {

    private static Map<Integer, List<Neighbor>> adjList = new HashMap<>();

    public static void addNeighbor(int nodeId, Neighbor neighbor) {
        if (!adjList.containsKey(nodeId)) {
            adjList.put(nodeId, new ArrayList<>());
        }
        adjList.get(nodeId).add(neighbor);
    }

    public static List<Neighbor> readAdjListEntry(int nodeId) throws Exception {
        if (!adjList.containsKey(nodeId)) {
            adjList.put(nodeId, new ArrayList<>());
        }

        return adjList.get(nodeId);
    }

    public static void loadFromFile(String edgeCSV) throws Exception {
        try (Reader reader = new BufferedReader(new FileReader(edgeCSV))) {
            CSVReader csvReader = new CSVReader(reader);
            csvReader.readNext(); //skip header
            String[] lines; //skip header
            while ((lines = csvReader.readNext()) != null) {
                int from = Integer.valueOf(lines[0]);
                int to = Integer.valueOf(lines[1]);
                double dist = Double.valueOf(lines[2]);
                addNeighbor(from, new Neighbor(to, dist));
            }
            System.out.println("loading complete");
            System.out.println(adjList.size());
        }
    }

}
