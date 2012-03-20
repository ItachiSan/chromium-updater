package net.chromiumupdater;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author morth
 */
public class ChangeLogViewer {

    public int build;
    public byte platform;
    public String changeLog;

    public ChangeLogViewer(int build, byte platform) {
	this.build = build;
	this.platform = platform; //TODO: maybe load directly from settings instead?
	this.changeLog = "";
    }

    /**
     * @return will fetch the xml-change-log file according to platform and build number and save it into a String
     */
    public void parseXML() {
        try {
            StringBuilder output = new StringBuilder();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = null;
            try {
                doc = db.parse("http://commondatastorage.googleapis.com/chromium-browser-continuous/"+(platform==0?"Win":"Mac")+"/"+build+"/changelog.xml");
            } catch (IOException ex) {
                System.out.println("Error whilst getting the changelog.");
            }
            doc.getDocumentElement().normalize();
            output.append("CHANGELOG: ").append(System.getProperty("line.separator"));
            NodeList nodeList = doc.getElementsByTagName("logentry");

            for (int s = 0; s < nodeList.getLength(); s++) {
                Node node = nodeList.item(s);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    NodeList dateLst = element.getElementsByTagName("date");
                    Element date = (Element) dateLst.item(0);
                    NodeList dateNm = date.getChildNodes();
                    output.append("DATE: ").append(((Node) dateNm.item(0)).getNodeValue());
                    output.append(System.getProperty("line.separator"));
                    NodeList msgLst = element.getElementsByTagName("msg");
                    Element msg = (Element) msgLst.item(0);
                    NodeList msgNm = msg.getChildNodes();
                    output.append(((Node) msgNm.item(0)).getNodeValue().replaceAll("(?m)^[ \t]*\r?\n", ""));
                }
            }
            changeLog = output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
