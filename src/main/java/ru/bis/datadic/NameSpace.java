package ru.bis.datadic;

import java.util.Objects;

/**
 * Class for namespace definition
 */
public class NameSpace {
    private String prefix;
    private String nameSpace;

    public NameSpace(String prefix, String nameSpace) {
        this.prefix = prefix;
        this.nameSpace = nameSpace;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameSpace nameSpace1 = (NameSpace) o;
        return Objects.equals(prefix, nameSpace1.prefix) &&
                nameSpace.equals(nameSpace1.nameSpace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, nameSpace);
    }
}
