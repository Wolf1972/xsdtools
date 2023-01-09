package ru.bis.datadic;

import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for store all commentaries for element
 */
public class CommentList {
    private HashMap<Integer, String> map;
    private Integer newId = 0;

    public CommentList() {
        this.map = new HashMap<>();
    }

    public CommentList(String str) {
        this();
        add(str);
    }

    public HashMap<Integer, String> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, String> map) {
        this.map = map;
    }

    public void add(String doc) {
        Integer id = ++newId;
        map.put(id, doc);
    }

    /**
     * Reads all commentaries from specified element
     * @param node - element
     */
    public void readCommentList(Node node) {
        Node element = node.getPreviousSibling();
        while (element != null && element.getNodeType() == Node.COMMENT_NODE) {
            add(element.getNodeValue()); // Get comment is placed before the specified node
            element = element.getPreviousSibling();
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("");
        for (Map.Entry<Integer, String> item : map.entrySet()) {
            str.append(item.getValue());
        }
        return str.toString();
    }
}
