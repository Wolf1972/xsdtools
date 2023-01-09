package ru.bis.datadic;

import org.w3c.dom.Node;

/**
* Contains miscellaneous static functions
 */
public class Helper {
    /** Function returns node name without namespace prefix (e.g. node "xs:integer" returns "integer" only)
     *
     * @param node - XML node
     * @return - node name without namespace prefix
     */
    static String getSimpleNodeName(Node node) {
        String str = node.getNodeName();
        if (str.contains(":")) {
            str = str.substring(str.lastIndexOf(":") + 1);
        }
        return str;
    }

    /** Function returns namespace prefix (e.g. node "xs:integer" returns "xs" only)
     *
     * @param node - XML node
     * @return - namespace prefix
     */
    static String getPrefix(Node node) {
        String str = node.getNodeName();
        if (str.contains(":")) {
            str = str.substring(0, str.lastIndexOf(":"));
        }
        return str;
    }
}
