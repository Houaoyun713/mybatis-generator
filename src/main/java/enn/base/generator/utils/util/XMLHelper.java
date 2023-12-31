//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package enn.base.generator.utils.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLHelper {
	public XMLHelper() {
	}

	public static LinkedHashMap<String, String> attrbiuteToMap(NamedNodeMap attributes) {
		if (attributes == null) {
			return new LinkedHashMap();
		} else {
			LinkedHashMap<String, String> result = new LinkedHashMap();

			for(int i = 0; i < attributes.getLength(); ++i) {
				result.put(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
			}

			return result;
		}
	}

	public static String getTextValue(Element valueEle) {
		if (valueEle == null) {
			throw new IllegalArgumentException("Element must not be null");
		} else {
			StringBuilder sb = new StringBuilder();
			NodeList nl = valueEle.getChildNodes();

			for(int i = 0; i < nl.getLength(); ++i) {
				Node item = nl.item(i);
				if ((!(item instanceof CharacterData) || item instanceof Comment) && !(item instanceof EntityReference)) {
					if (item instanceof Element) {
						sb.append(getTextValue((Element)item));
					}
				} else {
					sb.append(item.getNodeValue());
				}
			}

			return sb.toString();
		}
	}

	public static String getNodeValue(Node node) {
		if (node instanceof Comment) {
			return null;
		} else if (node instanceof CharacterData) {
			return ((CharacterData)node).getData();
		} else if (node instanceof EntityReference) {
			return node.getNodeValue();
		} else {
			return node instanceof Element ? getTextValue((Element)node) : node.getNodeValue();
		}
	}

	public NodeData parseXML(InputStream in) throws SAXException, IOException {
		Document doc = SimpleXmlParser.getLoadingDoc(in);
		new SimpleXmlParser();
		return SimpleXmlParser.treeWalk(doc.getDocumentElement());
	}

	public NodeData parseXML(File file) throws SAXException, IOException {
		FileInputStream in = new FileInputStream(file);

		NodeData var3;
		try {
			var3 = this.parseXML((InputStream)in);
		} finally {
			in.close();
		}

		return var3;
	}

	public static String getXMLEncoding(InputStream inputStream) throws UnsupportedEncodingException, IOException {
		return getXMLEncoding(IOHelper.toString("UTF-8", inputStream));
	}

	public static String getXMLEncoding(String s) {
		if (s == null) {
			return null;
		} else {
			Pattern p = Pattern.compile("<\\?xml.*encoding=[\"'](.*)[\"']\\?>");
			Matcher m = p.matcher(s);
			return m.find() ? m.group(1) : null;
		}
	}

	public static String removeXmlns(File file) throws IOException {
		InputStream forEncodingInput = new FileInputStream(file);
		String encoding = getXMLEncoding((InputStream)forEncodingInput);
		forEncodingInput.close();
		InputStream input = new FileInputStream(file);
		String xml = IOHelper.toString(encoding, input);
		xml = removeXmlns(xml);
		input.close();
		return xml;
	}

	public static String removeXmlns(String s) {
		if (s == null) {
			return null;
		} else {
			s = s.replaceAll("(?s)xmlns=['\"].*?['\"]", "");
			s = s.replaceAll("(?s)\\w*:schemaLocation=['\"].*?['\"]", "");
			return s;
		}
	}

	public static LinkedHashMap<String, String> parse2Attributes(String attributes) {
		LinkedHashMap result = new LinkedHashMap();
		Pattern p = Pattern.compile("(\\w+?)=['\"](.*?)['\"]");
		Matcher m = p.matcher(attributes);

		while(m.find()) {
			result.put(m.group(1), StringHelper.unescapeXml(m.group(2)));
		}

		return result;
	}

	public static void main(String[] args) throws FileNotFoundException, SAXException, IOException {
		String file = "D:/dev/workspaces/alipay/ali-generator/generator/src/table_test.xml";
		NodeData nd = (new XMLHelper()).parseXML((InputStream)(new FileInputStream(new File(file))));
		LinkedHashMap table = nd.attributes;
		List columns = nd.childs;
		System.out.println(table);
		System.out.println(columns);
	}

	public static class SimpleXmlParser {
		public SimpleXmlParser() {
		}

		public static Document getLoadingDoc(String file) throws FileNotFoundException, SAXException, IOException {
			return getLoadingDoc((InputStream)(new FileInputStream(file)));
		}

		static Document getLoadingDoc(InputStream in) throws SAXException, IOException {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(false);
			dbf.setValidating(false);
			dbf.setCoalescing(false);
			dbf.setIgnoringComments(false);

			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				db.setEntityResolver(new EntityResolver() {
					public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
						InputSource is = new InputSource(new StringReader(""));
						is.setSystemId(systemId);
						return is;
					}
				});
				InputSource is = new InputSource(in);
				return db.parse(is);
			} catch (ParserConfigurationException var4) {
				throw new Error(var4);
			}
		}

		private static NodeData treeWalk(Element elm) {
			NodeData nodeData = new NodeData();
			nodeData.attributes = XMLHelper.attrbiuteToMap(elm.getAttributes());
			nodeData.nodeName = elm.getNodeName();
			nodeData.childs = new ArrayList();
			nodeData.innerXML = childsAsText(elm, new StringBuffer(), true).toString();
			nodeData.outerXML = nodeAsText(elm, new StringBuffer(), true).toString();
			nodeData.nodeValue = XMLHelper.getNodeValue(elm);
			NodeList childs = elm.getChildNodes();

			for(int i = 0; i < childs.getLength(); ++i) {
				Node node = childs.item(i);
				if (node.getNodeType() == 1) {
					nodeData.childs.add(treeWalk((Element)node));
				}
			}

			return nodeData;
		}

		private static StringBuffer childsAsText(Element elm, StringBuffer sb, boolean ignoreComments) {
			NodeList childs = elm.getChildNodes();

			for(int i = 0; i < childs.getLength(); ++i) {
				Node child = childs.item(i);
				nodeAsText(child, sb, ignoreComments);
			}

			return sb;
		}

		private static StringBuffer nodeAsText(Node elm, StringBuffer sb, boolean ignoreComments) {
			if (elm.getNodeType() == 4) {
				CDATASection cdata = (CDATASection)elm;
				sb.append("<![CDATA[");
				sb.append(cdata.getData());
				sb.append("]]>");
				return sb;
			} else if (elm.getNodeType() == 8) {
				if (ignoreComments) {
					return sb;
				} else {
					Comment c = (Comment)elm;
					sb.append("<!--");
					sb.append(c.getData());
					sb.append("-->");
					return sb;
				}
			} else if (elm.getNodeType() == 3) {
				Text t = (Text)elm;
				sb.append(StringHelper.escapeXml(t.getData(), "<&"));
				return sb;
			} else {
				NodeList childs = elm.getChildNodes();
				sb.append("<" + elm.getNodeName());
				attributes2String(elm, sb);
				if (childs.getLength() > 0) {
					sb.append(">");

					for(int i = 0; i < childs.getLength(); ++i) {
						Node child = childs.item(i);
						nodeAsText(child, sb, ignoreComments);
					}

					sb.append("</" + elm.getNodeName() + ">");
				} else {
					sb.append("/>");
				}

				return sb;
			}
		}

		private static void attributes2String(Node elm, StringBuffer sb) {
			NamedNodeMap attributes = elm.getAttributes();
			if (attributes != null && attributes.getLength() > 0) {
				sb.append(" ");

				for(int j = 0; j < attributes.getLength(); ++j) {
					sb.append(String.format("%s=\"%s\"", attributes.item(j).getNodeName(), StringHelper.escapeXml(attributes.item(j).getNodeValue(), "<&\"")));
					if (j < attributes.getLength() - 1) {
						sb.append(" ");
					}
				}
			}

		}
	}

	public static class NodeData {
		public String nodeName;
		public String nodeValue;
		public String innerXML;
		public String outerXML;
		public LinkedHashMap<String, String> attributes = new LinkedHashMap();
		public List<NodeData> childs = new ArrayList();

		public NodeData() {
		}

		public String toString() {
			return "nodeName=" + this.nodeName + ",attributes=" + this.attributes + " nodeValue=" + this.nodeValue + " child:\n" + this.childs;
		}

		public LinkedHashMap<String, String> nodeNameAsAttributes(String nodeNameKey) {
			LinkedHashMap map = new LinkedHashMap();
			map.putAll(this.attributes);
			map.put(nodeNameKey, this.nodeName);
			return map;
		}

		public List<LinkedHashMap<String, String>> childsAsListMap() {
			List<LinkedHashMap<String, String>> result = new ArrayList();
			Iterator i$ = this.childs.iterator();

			while(i$.hasNext()) {
				NodeData c = (NodeData)i$.next();
				LinkedHashMap map = new LinkedHashMap();
				map.put(c.nodeName, c.nodeValue);
				result.add(map);
			}

			return result;
		}
	}
}
