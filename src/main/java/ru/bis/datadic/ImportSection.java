package ru.bis.datadic;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Class for describe import file for scheme file
 */
public class ImportSection {
    private String nameSpace;
    private String file;

    public ImportSection(String nameSpace, String file) {
        this.nameSpace = nameSpace;
        this.file = file;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }


}
