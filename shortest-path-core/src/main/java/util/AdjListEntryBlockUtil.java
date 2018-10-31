package util;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import vo.AdjListEntryDTO;
import vo.Neighbor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdjListEntryBlockUtil {

    private Map<Integer, List<Neighbor>> adjListEntryMap;
    private File file;

    private static final String DECODE_PATTERN = "([0-9]+),(([0-9]*[.])?[0-9]+)";
    private static final String ENCODE_PATTERN = "[%d, %f]";

    public AdjListEntryBlockUtil() {
        adjListEntryMap = new TreeMap<>();
    }

    public void addAdjListEntry(AdjListEntryDTO adjListEntry) {
        adjListEntryMap.put(adjListEntry.getNodeId(), decodeNeighborString(adjListEntry.getNeighborString()));
    }

    public void addAdjListEntry(List<AdjListEntryDTO> adjListEntryList) {
        for(AdjListEntryDTO entry: adjListEntryList) {
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

    public List<AdjListEntryDTO> getAdjListEntryList() {
        List<AdjListEntryDTO> adjListEntryDTOList = new ArrayList<>();
        adjListEntryMap.forEach((id, neighbors) -> adjListEntryDTOList.add(new AdjListEntryDTO(id, decodeNeighborList(neighbors))));
        return adjListEntryDTOList;
    }

    public Map<Integer, List<Neighbor>> readFromFile(File file) throws Exception {
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            CsvToBean<AdjListEntryDTO> csvToBean = new CsvToBeanBuilder(reader)
                    .withMappingStrategy(getStrategy())
                    .withQuoteChar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            addAdjListEntry(csvToBean.parse());
            return adjListEntryMap;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void storeToFile(File file) throws Exception {
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            StatefulBeanToCsv<AdjListEntryDTO> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withMappingStrategy(getStrategy())
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .build();

            beanToCsv.write(getAdjListEntryList());
        } catch (Exception ex) {
            throw ex;
        }
    }

    private ColumnPositionMappingStrategy<AdjListEntryDTO> getStrategy() {
        ColumnPositionMappingStrategy<AdjListEntryDTO> strategy = new CustomMappingStrategy<>();
        strategy.setType(AdjListEntryDTO.class);

        return strategy;
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
