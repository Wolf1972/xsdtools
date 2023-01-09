package ru.bis.datadic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for describe all attributes in complex type or element
 */
public class AttributeList {
    Logger logger = LogManager.getLogger(SimpleTypeList.class);

    private HashMap<Integer, Attribute> map;
    private Integer newId = 0;

    public AttributeList() {
        this.map = new HashMap<>();
    }

    public HashMap<Integer, Attribute> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, Attribute> map) {
        this.map = map;
    }

    public void add(Attribute attr) {
        Integer id = ++newId;
        map.put(id, attr);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        boolean first = true;
        for (Map.Entry<Integer, Attribute> item : map.entrySet()) {
            if (!first) str.append(", "); else first = false;
            str.append(item.getValue());
        }
        return str.toString();
    }

    public void log() {
        for (Map.Entry<Integer, Attribute> entry : map.entrySet()) {
            logger.info(entry.getValue().toString());
        }
    }

    /**
     * Outputs all attributes to XLS sheet
     * @param rowCount - current XLS sheet row number
     * @param sheet - XLS sheet
     * @return - current XLS sheet row number after output
     */
    public int outXLS(int rowCount, Sheet sheet) {
        for (Integer idSimple : map.keySet()) {
            rowCount = map.get(idSimple).outXLS(rowCount, sheet);
        }
        return rowCount;
    }
}
