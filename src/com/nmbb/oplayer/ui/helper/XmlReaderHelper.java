package com.nmbb.oplayer.ui.helper;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;

import com.nmbb.oplayer.po.OnlineVideo;

/** 从XML读取电视台节目 */
public class XmlReaderHelper {

	/** 获取所有电视分类 */
	public static ArrayList<OnlineVideo> getAllCategory(final Context context) {
		ArrayList<OnlineVideo> result = new ArrayList<OnlineVideo>();
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			// xml file 放到 assets目录中的
			doc = docBuilder.parse(context.getResources().getAssets()
					.open("online.xml"));
			// root element
			Element root = doc.getDocumentElement();
			NodeList nodeList = root.getElementsByTagName("category");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);// category
				OnlineVideo ov = new OnlineVideo();
				NamedNodeMap attr = node.getAttributes();
				ov.title = attr.getNamedItem("name").getNodeValue();
				ov.id = attr.getNamedItem("id").getNodeValue();
				ov.category = 1;
				ov.level = 2;
				ov.is_category = true;
				result.add(ov);
				// Read Node
			}
		} catch (IOException e) {
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
		} finally {
			doc = null;
			docBuilder = null;
			docBuilderFactory = null;
		}
		return result;
	}

	/** 读取分类下所有电视地址 */
	public static ArrayList<OnlineVideo> getVideos(final Context context,
			String categoryId) {
		ArrayList<OnlineVideo> result = new ArrayList<OnlineVideo>();
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			// xml file 放到 assets目录中的
			doc = docBuilder.parse(context.getResources().getAssets()
					.open("online.xml"));
			// root element
			Element root = doc.getElementById(categoryId);
			if (root != null) {
				NodeList nodeList = root.getChildNodes();
				for (int i = 0, j = nodeList.getLength(); i < j; i++) {
					Node baseNode = nodeList.item(i);

					if (!"item".equals(baseNode.getNodeName()))
						continue;
					String id = baseNode.getFirstChild().getNodeValue();
					if (id == null)
						continue;
					OnlineVideo ov = new OnlineVideo();
					ov.id = id;

					Element el = doc.getElementById(ov.id);
					if (el != null) {
						ov.title = el.getAttribute("title");
						ov.icon_url = el.getAttribute("image");
						ov.level = 3;
						ov.category = 1;
						NodeList nodes = el.getChildNodes();
						for (int m = 0, n = nodes.getLength(); m < n; m++) {
							Node node = nodes.item(m);
							if (!"ref".equals(node.getNodeName()))
								continue;
							String href = node.getAttributes()
									.getNamedItem("href").getNodeValue();
							if (ov.url == null) {
								ov.url = href;
							} else {
								if (ov.backup_url == null)
									ov.backup_url = new ArrayList<String>();
								ov.backup_url.add(href);
							}
						}
						if (ov.url != null)
							result.add(ov);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			doc = null;
			docBuilder = null;
			docBuilderFactory = null;
		}
		return result;
	}
}
