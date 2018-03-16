package com.routon.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class xmltools {
    // 创建xml文件
    public static void createXML(String serveruuid, List<xmlBean> list) {

        Document doc;
        Element information;
        Element serverUuid;
        Element character;
        Element uuid;
        Element data;

        try {
            // 得到dom解析器的工厂实例
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            // 从dom工厂实例获得dom解析器
            DocumentBuilder builder = factory.newDocumentBuilder();
            // 创建文档树模型对象
            doc = builder.newDocument();
            // 如果创建的文档树模型不为空
            if (doc != null) {
                // 创建元素
                information = doc.createElement("infomation");

                serverUuid = doc.createElement("serveruuid");
                serverUuid.appendChild(doc.createTextNode(serveruuid));
                information.appendChild(serverUuid);
                for (int i = 0; i < list.size(); i++) {
                    String xmlUuid = list.get(i).uuid;
                    String xmlData = list.get(i).data;
                    character = doc.createElement("character");
                    uuid = doc.createElement("uuid");
                    uuid.appendChild(doc.createTextNode(xmlUuid));

                    data = doc.createElement("data");
                    data.appendChild(doc.createTextNode(xmlData));

                    character.appendChild(uuid);
                    character.appendChild(data);
                    information.appendChild(character);
                }


                // 将infomation元素作为根元素添加到xml文档树中
                doc.appendChild(information);

                // 将内存中的文档树保存为traffic.xml文档
                Transformer transformer = TransformerFactory.newInstance()
                        .newTransformer();// 得到转换器
                // 设置换行
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                // 写入文件
                transformer.transform(new DOMSource(doc), new StreamResult(
                        new File(xmlConstant.filepath)));
//				transformer.transform(new DOMSource(doc), new StreamResult(
//						new File("data.xml")));
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //
    // // 保存xml文件
    // public static void saveXML(Document doc) {
    //
    // }
    // 读取xml文件中的serveruuid
    public static String readServerUUID() {
        String serveruuid = "";
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            //parse方法加载xml文件到当前项目
            Document dom = db.parse(new File(xmlConstant.filepath));
            Element information = (Element) dom.getFirstChild();

            NodeList serveruuids = information
                    .getElementsByTagName("serveruuid");
            serveruuid = serveruuids.item(0).getTextContent();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return serveruuid;
    }

    // 读取xml文件特征值列表以及对应的指令
    public static ArrayList<xmlBean> readCharacter() {

        ArrayList<xmlBean> al = new ArrayList<xmlBean>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document dom = db.parse(new File(xmlConstant.filepath));

            Element information = (Element) dom.getFirstChild();

            NodeList items = information.getElementsByTagName("character");

            for (int i = 0; i < items.getLength(); i++) {

                Element character = (Element) items.item(i);
                Element uuid = (Element) character.getElementsByTagName("uuid")
                        .item(0);
                Element data = (Element) character.getElementsByTagName("data")
                        .item(0);

                al.add(new xmlBean(uuid.getTextContent(), data.getTextContent()));
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return al;
    }

    // 读取xml文件第i个特征值列表以及对应的指令
    public static xmlBean readCharacter(int index) {

        xmlBean xb = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document dom = db.parse(new File(xmlConstant.filepath));

            Element information = (Element) dom.getFirstChild();

            NodeList items = information.getElementsByTagName("character");
            if (index < items.getLength()) {
                Element character = (Element) items.item(index);
                Element uuid = (Element) character.getElementsByTagName("uuid")
                        .item(0);
                Element data = (Element) character.getElementsByTagName("data")
                        .item(0);
                xb = new xmlBean(uuid.getTextContent(), data.getTextContent());
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return xb;
    }

    // 添加节点
    public static void insertXML(String uuid, String data) {

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document doc = db.parse(new File(xmlConstant.filepath));
            Element information = (Element) doc.getFirstChild();

            Element character = doc.createElement("character");
            information.appendChild(character);

            Element uuidElement = doc.createElement("uuid");
            uuidElement.appendChild(doc.createTextNode(uuid));

            Element dataElement = doc.createElement("data");
            dataElement.appendChild(doc.createTextNode(data));

            character.appendChild(uuidElement);
            character.appendChild(dataElement);

            TransformerFactory tfFactory = TransformerFactory.newInstance();
            Transformer tf = tfFactory.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(doc), new StreamResult(new File(
                    xmlConstant.filepath)));
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 更新serverUUid
    public static boolean updateServerUUID(String serveruuid) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document dom = db.parse(new File(xmlConstant.filepath));
            Element information = (Element) dom.getFirstChild();
            NodeList items = information.getElementsByTagName("serveruuid");
            Element serverUuid = (Element) items.item(0);
            serverUuid.setTextContent(serveruuid);

            TransformerFactory tfFactory = TransformerFactory.newInstance();
            Transformer tf = tfFactory.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(dom), new StreamResult(new File(
                    xmlConstant.filepath)));
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    // 更新character
    public static boolean updateXML(String uuid, String data) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document dom = db.parse(new File(xmlConstant.filepath));
            Element information = (Element) dom.getFirstChild();
            NodeList items = information.getElementsByTagName("character");

            for (int i = 0; i < items.getLength(); i++) {
                Element character = (Element) items.item(i);
                String uuid_temp = character.getElementsByTagName("character")
                        .item(0).getTextContent();
                if (uuid_temp.equals(uuid)) {
                    Element data_element = (Element) character
                            .getElementsByTagName("data").item(0);
                    data_element.setTextContent(data);
                }
            }

            TransformerFactory tfFactory = TransformerFactory.newInstance();
            Transformer tf = tfFactory.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(dom), new StreamResult(new File(
                    xmlConstant.filepath)));
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public static int characterLength() {
        int length = 0;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document dom = db.parse(new File(xmlConstant.filepath));
            Element information = (Element) dom.getFirstChild();
            NodeList items = information.getElementsByTagName("character");
            length = items.getLength();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return length;
    }

    public static int commandLength() {
        int length = 0;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document dom = db.parse(new File(xmlConstant.filepath));
            Element information = (Element) dom.getFirstChild();
            NodeList items = information.getElementsByTagName("character");
            for (int i = 0; i < items.getLength(); i++) {
                Element character = (Element) items.item(i);
                Element data_element = (Element) character
                        .getElementsByTagName("data").item(0);
                if (data_element.getTextContent().equals("04 ff 01 80 12")) {
                    length=i;
                    return length;
                }
            }
            length=items.getLength();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return length;
    }

    public static boolean updateXML(int index, String data) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document dom = db.parse(new File(xmlConstant.filepath));
            Element information = (Element) dom.getFirstChild();
            NodeList items = information.getElementsByTagName("character");

            Element character = (Element) items.item(index);

            Element data_element = (Element) character
                    .getElementsByTagName("data").item(0);
            data_element.setTextContent(data);


            TransformerFactory tfFactory = TransformerFactory.newInstance();
            Transformer tf = tfFactory.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(dom), new StreamResult(new File(
                    xmlConstant.filepath)));
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

}
