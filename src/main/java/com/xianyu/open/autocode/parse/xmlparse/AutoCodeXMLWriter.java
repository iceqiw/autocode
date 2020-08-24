package com.xianyu.open.autocode.parse.xmlparse;

import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author bailinan
 * @date 2018/7/29
 */
public class AutoCodeXMLWriter extends XMLWriter {

    private OutputFormat myFormat;

    public AutoCodeXMLWriter(OutputStream out, OutputFormat format) throws UnsupportedEncodingException {
        super(out, format);
        this.myFormat = format;
    }

    @Override
    protected void writeNodeText(Node node) throws IOException {
        int brother = node.getParent().content().size();
        String[] texts = node.getText().split("\n");
        for (String text: texts){
            writePrintln();
            indent(node);
            node.setText(text);
            super.writeNodeText(node);
        }
        if (brother == 1){
            writePrintln();
        }
        indent();
    }

    private void indent(Node node) throws IOException {
        String indent = myFormat.getIndent();
        int indentLevel=0;
        while (node.getParent()!=null){
            node = node.getParent();
            indentLevel++;
        }
        if ((indent != null) && (indent.length() > 0)) {
            for (int i = 0; i < indentLevel; i++) {
                writer.write(indent);
            }
        }
    }
    
    
}
