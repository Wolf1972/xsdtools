package ru.bis.datadic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SchemeFileList {
    Logger logger = LogManager.getLogger(SchemeFileList.class);

    private HashMap<Integer, SchemeFile> map;
    private Integer newId = 0;

    public SchemeFileList() {
        this.map = new HashMap<>();

        // Add base types as separate virtual scheme file (for simplify operation with types)
        String baseNameSpace = "http://www.w3.org/2001/XMLSchema";

        SchemeFile base = new SchemeFile(null, this);
        base.setTargetNs(baseNameSpace);

        base.getSimpleList().add(baseNameSpace, "string", "string", new DocList("Standard XML string"), null);
        base.getSimpleList().add(baseNameSpace, "integer", "integer", new DocList("Standard XML integer"), null);
        base.getSimpleList().add(baseNameSpace, "date", "date", new DocList("Standard XML date"), null);
        base.getSimpleList().add(baseNameSpace, "dateTime", "dateTime", new DocList("Standard XML date and time"), null);
        base.getSimpleList().add(baseNameSpace, "time", "time", new DocList("Standard XML time"), null);
        base.getSimpleList().add(baseNameSpace, "decimal", "decimal", new DocList("Standard XML decimal"), null);
        base.getSimpleList().add(baseNameSpace, "boolean", "boolean", new DocList("Standard XML boolean"), null);
        base.getSimpleList().add(baseNameSpace, "gYear", "gYear", new DocList("Standard XML year"), null);
        base.getSimpleList().add(baseNameSpace, "gYearMonth", "gYearMonth", new DocList("Standard XML year and month"), null);

        add(base);

    }

    public HashMap<Integer, SchemeFile> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, SchemeFile> map) {
        this.map = map;
    }

    public void add(SchemeFile file) {
        Integer id = ++newId;
        map.put(id, file);
    }

    /**
     * Check fpr invalid references to base types in restrictions (seacrhes in all scheme files)
     */
    public void checkRefTypes() {
        String baseNameSpace = "http://www.w3.org/2001/XMLSchema";
        int errors = 0;

        for (Map.Entry<Integer, SchemeFile> entry : map.entrySet()) {
            SchemeFile scheme = entry.getValue();
            for (Map.Entry<Integer, SimpleType> typeEntry : scheme.getSimpleList().getMap().entrySet()) {
                SimpleType simpleType = typeEntry.getValue();
                Restriction restriction = simpleType.getRestriction();
                if (restriction != null) {
                    String refType = restriction.getBase();
                    String sysName = refType.contains(":") ? refType.substring(refType.lastIndexOf(":") + 1) : refType;
                    String prefix = refType.contains(":") ? refType.substring(0, refType.lastIndexOf(":")) : refType;
                    String nameSpace = null;
                    for (Map.Entry<Integer, NameSpace> nameSpaceEntry : scheme.getNsList().getMap().entrySet()) {
                        if (nameSpaceEntry.getValue().getPrefix().equals(prefix)) {
                            nameSpace = nameSpaceEntry.getValue().getNameSpace();
                            break;
                        }
                    }
                    if (nameSpace == null) {
                        logger.error("0010: Unknown namespace prefix " + prefix);
                        errors++;
                        continue;
                    }
                    if (searchForType(sysName, nameSpace) == null) {
                        logger.error("0011: Unknown reference type " + refType + " (" + nameSpace + ")");
                        errors++;
                    }
                }
            }
        }
        if (errors == 0) logger.info("0000 There is no reference errors found in all processed schemes.");
    }

    /**
     * Function looks for specified simple type by name (specify with "ns:name")
     * @param typeName - type system name (with namespace prefix)
     * @param nameSpace - namespace (full name, not prefix!)
     * @return simple type or null
     */
    public SimpleType searchForType(String typeName, String nameSpace) {
        SimpleType result = null;

        SchemeFile targetFile = null;
        // Try to find scheme with specified target namespace
        for (Map.Entry<Integer, SchemeFile> entry : map.entrySet()) {
            if (entry.getValue().getTargetNs().equals(nameSpace)) {
                targetFile = entry.getValue();
                break;
            }
        }
        if (targetFile != null) {
            for (Map.Entry<Integer, SimpleType> entry : targetFile.getSimpleList().getMap().entrySet()) {
                if (entry.getValue().getSysName().equals(typeName)) {
                    result = entry.getValue();
                    break;
                }
            }
        }
        return result;
    }
}
