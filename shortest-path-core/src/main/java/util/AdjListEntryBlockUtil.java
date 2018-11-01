package util;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import vo.AdjListEntry;
import vo.Neighbor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdjListEntryBlockUtil {

    private Map<Integer, List<Neighbor>> adjListEntryMap;

    private static final String DECODE_PATTERN = "([0-9]+),(([0-9]*[.])?[0-9]+)";
    private static final String ENCODE_PATTERN = "[%d, %f]";

    public AdjListEntryBlockUtil() {
        adjListEntryMap = new TreeMap<>();
    }

    public void addAdjListEntry(AdjListEntry adjListEntry) {
        adjListEntryMap.put(adjListEntry.getNodeId(), decodeNeighborString(adjListEntry.getNeighborString()));
    }

    public void addAdjListEntry(List<AdjListEntry> adjListEntryList) {
        for(AdjListEntry entry: adjListEntryList) {
            addAdjListEntry(entry);
        }
    }

    public void addNeighbors(int id, Neighbor neighbor) {
        if (!adjListEntryMap.containsKey(id)) {
            ArrayList<Neighbor> neighbors = new ArrayList<>();
            adjListEntryMap.put(id, neighbors);
        }

        adjListEntryMap.get(id).add(neighbor);
    }

    public Map<Integer, List<Neighbor>> getAdjListEntryMap() {
        return adjListEntryMap;
    }

    public List<AdjListEntry> getAdjListEntryList() {
        List<AdjListEntry> adjListEntryList = new ArrayList<>();
        adjListEntryMap.forEach((id, neighbors) -> adjListEntryList.add(new AdjListEntry(id, decodeNeighborList(neighbors))));
        return adjListEntryList;
    }


    public void storeToFile(File file) throws IOException {

        try (Writer writer = new BufferedWriter((new FileWriter(file)))) {
            CsvWriterSettings settings = new CsvWriterSettings();
            settings.setQuoteAllFields(true);
            settings.setHeaderWritingEnabled(true);
            BeanWriterProcessor<AdjListEntry> processor = new BeanWriterProcessor<>(AdjListEntry.class);
            settings.setRowWriterProcessor(processor);
            CsvWriter csvWriter = new CsvWriter(writer, settings);
            for(AdjListEntry entry: getAdjListEntryList()) {
                csvWriter.processRecord(entry);
            }
        }
    }

    public Map<Integer, List<Neighbor>> readFromFile(File file) throws IOException {

        try (Reader reader = new BufferedReader(new FileReader(file))) {
            CsvParserSettings parserSettings = new CsvParserSettings();
            parserSettings.setHeaderExtractionEnabled(true);
            BeanListProcessor<AdjListEntry> processor = new BeanListProcessor<>(AdjListEntry.class);
            parserSettings.setProcessor(processor);
            CsvParser parser = new CsvParser(parserSettings);
            parser.parse(reader);
            addAdjListEntry(processor.getBeans());
            return adjListEntryMap;
        }
    }


    public static List<Neighbor> decodeNeighborString(String neighborString) {
        List<Neighbor> neighborList = new ArrayList<>();

        Matcher m = Pattern.compile(DECODE_PATTERN).matcher(neighborString);

        while (m.find()) {
            String[] neighbor = m.group().split(",");
            neighborList.add(new Neighbor(Integer.valueOf(neighbor[0]), Double.valueOf(neighbor[1])));
        }

        return neighborList;
    }

    public static String decodeNeighborList(List<Neighbor> neighbors) {
        String neighborString = "[";
        for(Neighbor neighbor: neighbors) {
            neighborString = neighborString + String.format(ENCODE_PATTERN, neighbor.getId(), neighbor.getDistance()) + ",";
        }
        neighborString = neighborString.substring(0, neighborString.length()-1) + "]";
        return neighborString;
    }
}
