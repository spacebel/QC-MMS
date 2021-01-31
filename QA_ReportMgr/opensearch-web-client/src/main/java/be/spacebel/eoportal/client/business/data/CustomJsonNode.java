/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.spacebel.eoportal.client.business.data;

import be.spacebel.eoportal.client.util.Utility;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mng
 */
public class CustomJsonNode {

    public static enum TYPE {
        VALUE, OBJECT, ARRAY
    };

    private TYPE type;
    private String key;
    private String value;
    private List<CustomJsonNode> children;
    private int indent;

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIndent() {
        return indent;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public List<CustomJsonNode> getChildren() {
        return children;
    }

    public void setChildren(List<CustomJsonNode> children) {
        this.children = children;
    }

    public void addChild(CustomJsonNode child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public String toDebug() {
        StringBuilder sb = new StringBuilder();
        
        String spaces = getSpaces(indent);

        switch (type) {
            case VALUE:
                if (StringUtils.isNotEmpty(key)) {
                    sb.append(spaces).append("1. ").append(toLabel(key));
                }
                sb.append(":").append(addLink(value)).append(" 1- > ").append(indent).append("\n");

                break;
            case OBJECT:
                if (StringUtils.isNotEmpty(key)) {
                    sb.append(spaces).append("+2. ").append(toLabel(key)).append(" 2- > ").append(indent).append("\n");
                }
                if (children != null && !children.isEmpty()) {
                    if (children.size() == 1) {
                        CustomJsonNode child = children.get(0);
                        child.setIndent(indent + Constants.INDENT_LEVEL);
                        if (StringUtils.isNotEmpty(key)) {
                            sb.append(spaces).append(" 2.1 ").append(child.toDebug()).append("\n");
                        } else {
                            sb.append(" 2.2 ").append(child.toDebug()).append("\n");
                        }

                    } else {
                        //sb.append(" Number of children ").append(children.size()).append("\n");

                        if (StringUtils.isNotEmpty(key)) {
                            sb.append(spaces).append("{").append("\n");
                            for (int i = 0; i < children.size(); i++) {
                                CustomJsonNode child = children.get(i);
                                child.setIndent(indent + Constants.INDENT_LEVEL);
                                sb.append(child.toDebug()).append("\n");
                            }
                            sb.append(spaces).append("}").append("\n");
                        } else {
                            moveValueNodeToTheFirst();

                            CustomJsonNode firstChild = children.get(0);
                            firstChild.setIndent(indent);
                            sb.append(spaces).append("+++").append(firstChild.toDebug()).append("{").append("\n");

                            for (int i = 1; i < children.size(); i++) {
                                CustomJsonNode child = children.get(i);
                                child.setIndent(indent + Constants.INDENT_LEVEL);
                                sb.append(child.toDebug()).append("\n");
                            }
                            sb.append(spaces).append("}").append("\n");
                        }
                    }
                }
                break;
            case ARRAY:
                if (children != null && !children.isEmpty()) {
                    if (isValueArray()) {
                        List<String> values = new ArrayList<>();
                        for (CustomJsonNode child : children) {
                            values.add(child.getValue());
                        }
                        String additionalSpaces = getSpaces(5);
                        if (StringUtils.isNotEmpty(key)) {
                            sb.append(spaces).append("3.1 ").append(toLabel(key)).append(" 4- > ").append(indent).append(":");
                        }
                        sb.append(spaces).append(additionalSpaces).append("3.1 ").append(addLink(StringUtils.join(values, ","))).append(" 5- > ").append(indent).append("\n");
                    } else {
                        if (StringUtils.isNotEmpty(key)) {
                            sb.append(spaces).append("+3.2 ").append(toLabel(key)).append(" 4- > ").append(indent).append("\n");
                        }
                        sb.append(spaces).append("[").append("\n");
                        for (CustomJsonNode child : children) {
                            child.setIndent(indent);
                            sb.append(child.toDebug());
                        }
                        sb.append(spaces).append("]").append("\n");
                    }
                }
                break;
        }
        return sb.toString();
    }

    public String toHTML() {
        StringBuilder sb = new StringBuilder();

        String style = getStyle(indent);

        String label = toLabel(key);

        switch (type) {
            case VALUE:
                if (StringUtils.isNotEmpty(key)) {
                    sb.append("<span class=\"details-text-label\" ").append(style).append(">")
                            .append(label)
                            .append(": </span>");
                }
                sb.append(addLink(value));
                sb.append("<br/>");
                break;
            case OBJECT:
                if (children != null && !children.isEmpty()) {
                    if (children.size() == 1) {
                        CustomJsonNode child = children.get(0);
                        child.setIndent(indent + Constants.INDENT_LEVEL);
                        
                        if (child.getType() == TYPE.VALUE) {
                            sb.append("<span class=\"details-text-label\" ").append(style).append(">")
                                    //.append("2.1").append(toLabel(child.getKey()))
                                    .append(toLabel(child.getKey()))
                                    .append(": </span>");
                            sb.append(addLink(child.getValue()));
                            sb.append("<br/>");
                        } else {
                            sb.append(" <i class=\"fa fa-fw fa-plus-square-o\"")
                                    .append(" onclick=\"fedeoclient_webapp_slideToggle(this)\"")
                                    .append(" ").append(style)
                                    .append(" title=\"View more details\"/>");
                            if (StringUtils.isNotEmpty(key)) {
                                sb.append("<span class=\"details-text-label\" ")
                                        .append(getStyle(Constants.INDENT_TEXT_SPACE)).append(">")
                                        //.append("2.2").append(label)
                                        .append(label)
                                        .append("</span>");
                                sb.append("<br/>");
                                sb.append("<div class=\"collapse-expand-contents\">");
                                sb.append(child.toHTML());
                                sb.append("</div>");
                            }
                        }

                    } else {
                        //sb.append(" Number of children ").append(children.size()).append("\n");

                        if (StringUtils.isNotEmpty(key)) {
                            sb.append(" <i class=\"fa fa-fw fa-plus-square-o\"")
                                    .append(" onclick=\"fedeoclient_webapp_slideToggle(this)\"")
                                    .append(" ").append(style)
                                    .append(" title=\"View more details\"/>");
                            sb.append("<span class=\"details-text-label\" ").append(getStyle(Constants.INDENT_TEXT_SPACE)).append(">")
                                    //.append("2.3").append(label)
                                    .append(label)
                                    .append("</span>");
                            sb.append("<br/>");

                            sb.append("<div class=\"collapse-expand-contents\">");
                            for (int i = 0; i < children.size(); i++) {
                                CustomJsonNode child = children.get(i);
                                child.setIndent(indent + Constants.INDENT_LEVEL);
                                sb.append(child.toHTML());
                            }
                            sb.append("</div>");
                        } else {
                            moveValueNodeToTheFirst();

                            CustomJsonNode firstChild = children.get(0);

                            sb.append(" <i class=\"fa fa-fw fa-plus-square-o\"")
                                    .append(" onclick=\"fedeoclient_webapp_slideToggle(this)\"")
                                    .append(" ").append(style)
                                    .append(" title=\"View more details\"/>");
                            sb.append("<span class=\"details-text-label\" ")
                                    .append(getStyle(Constants.INDENT_TEXT_SPACE)).append(">")
                                    //.append("2.4").append(toLabel(firstChild.getKey()))
                                    .append(toLabel(firstChild.getKey()))
                                    .append(": </span>");

                            if (firstChild.getType() == TYPE.VALUE) {
                                sb.append(addLink(firstChild.getValue()));
                            } else {
                                firstChild.setIndent(indent);
                                sb.append(firstChild.toHTML());
                            }
                            sb.append("<br/>");
                            sb.append("<div class=\"collapse-expand-contents\">");

                            for (int i = 1; i < children.size(); i++) {
                                CustomJsonNode child = children.get(i);
                                child.setIndent(indent + Constants.INDENT_ICON_WIDTH);
                                sb.append(child.toHTML());
                            }
                            sb.append("</div>");
                        }
                    }
                }
                break;
            case ARRAY:
                if (children != null && !children.isEmpty()) {
                    if (isValueArray()) {
                        List<String> values = new ArrayList<>();
                        for (CustomJsonNode child : children) {
                            values.add(child.getValue());
                        }

                        if (StringUtils.isNotEmpty(key)) {
                            sb.append("<span class=\"details-text-label\" ").append(style).append(">")
                                    //.append("3.1").append(label)
                                    .append(label)
                                    .append("</span>");
                            sb.append(addLink(StringUtils.join(values, ",")));
                        } else {
                            sb.append("<span ").append(style).append(">")
                                    //.append("3.1.1 --> ").append(addLink(StringUtils.join(values, ",")))
                                    .append(addLink(StringUtils.join(values, ",")))
                                    .append("</span>");
                        }

                        sb.append("<br/>");
                        //sb.append(spaces).append(additionalSpaces).append("3.1 ").append(addLink(StringUtils.join(values, ","))).append(" 5- > ").append(indent).append("\n");
                    } else {
                        if (StringUtils.isNotEmpty(key)) {
                            sb.append(" <i class=\"fa fa-fw fa-plus-square-o\"")
                                    .append(" onclick=\"fedeoclient_webapp_slideToggle(this)\"")
                                    .append(" ").append(style)
                                    .append(" title=\"View more details\"/>");
                            sb.append("<span class=\"details-text-label\" ")
                                    .append(getStyle(Constants.INDENT_TEXT_SPACE)).append(">")
                                    //.append("3.2").append(label)
                                    .append(label)
                                    .append("</span>");
                        }
                        sb.append("<br/>");
                        sb.append("<div class=\"collapse-expand-contents\">");
                        for (CustomJsonNode child : children) {
                            child.setIndent(indent + Constants.INDENT_LEVEL);
                            sb.append(child.toHTML());
                        }
                        sb.append("</div>");

                    }
                }
                break;
        }
        return sb.toString();
    }

    private boolean isValueArray() {
        boolean ok = true;
        for (CustomJsonNode child : children) {
            if (child.getType() != TYPE.VALUE
                    || StringUtils.isNotEmpty(child.getKey())) {
                return false;
            }
        }
        return ok;
    }

    private String toLabel(String str) {
        return Utility.toLabel(str);
    }

    private String addLink(String value) {
        return Utility.addLink(value);
    }

    private static String getSpaces(int numberOfSpaces) {
        String spaces = "";
        if (numberOfSpaces > 0) {
            for (int i = 0; i < numberOfSpaces; i++) {
                spaces += " ";
            }
        }
        return spaces;
    }

    private static String getStyle(int indent) {
        return "style=\"margin-left:" + indent + "px;\"";
    }

    private void moveValueNodeToTheFirst() {
        if (children != null && !children.isEmpty()) {
            List<Integer> foundIndexes = new ArrayList<>();

            for (int i = 0; i < children.size(); i++) {
                CustomJsonNode child = children.get(i);
                if (child.getType().equals(TYPE.VALUE)) {
                    foundIndexes.add(i);
                }
            }

            if (!foundIndexes.isEmpty()) {
                int foundIdx = -1;
                for (int i : foundIndexes) {
                    if (children.get(i) != null
                            && children.get(i).getKey() != null
                            && children.get(i).getKey().equalsIgnoreCase("id")) {
                        foundIdx = i;
                    }
                }
                if (foundIdx < 0) {
                    foundIdx = foundIndexes.get(0);
                }

                if (foundIdx > 0) {
                    CustomJsonNode child = children.get(foundIdx);
                    children.remove(foundIdx);
                    children.add(0, child);
                    //System.out.println("Found index: " + foundIdx);
                    //System.out.println("Found Child: " + child.getKey());
                }
            }
        }
    }
}
