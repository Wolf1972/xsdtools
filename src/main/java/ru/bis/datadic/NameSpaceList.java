package ru.bis.datadic;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class for list of all namespaces definitions
 */
public class NameSpaceList {
    private LinkedHashMap<Integer, NameSpace> map;
    private Integer newId = 0;

    public NameSpaceList() {
        this.map = new LinkedHashMap<>();
    }

    public LinkedHashMap<Integer, NameSpace> getMap() {
        return map;
    }

    public void setMap(LinkedHashMap<Integer, NameSpace> map) {
        this.map = map;
    }

    public void add(String prefix, String nameSpace) {
        Integer id = ++newId;
        map.put(id, new NameSpace(prefix, nameSpace));
    }

    public NameSpace getNameSpace(String nameSpaceToFind) {
        for (Map.Entry<Integer, NameSpace> entry : map.entrySet()) {
            if (entry.getValue().getNameSpace().equals(nameSpaceToFind)) return entry.getValue();
        }
        return null;
    }

}
