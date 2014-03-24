package org.tch.fc;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tch.fc.model.EventType;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.ForecastItem;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SWPConnector implements ConnectorInterface {

  private Map<String, ForecastItem> familyMapping = new HashMap<String, ForecastItem>();
  private Software software = null;

  private static final Map<String, String> cvxOut = new HashMap<String, String>();
  private static final Map<String, String> cvxIn = new HashMap<String, String>();
  static {
    cvxOut.put("01", "1");
    cvxOut.put("02", "2");
    cvxOut.put("03", "3");
    cvxOut.put("04", "4");
    cvxOut.put("05", "5");
    cvxOut.put("06", "6");
    cvxOut.put("07", "7");
    cvxOut.put("08", "8");
    cvxOut.put("09", "9");
    cvxIn.put("1", "01");
    cvxIn.put("2", "02");
    cvxIn.put("3", "03");
    cvxIn.put("4", "04");
    cvxIn.put("5", "05");
    cvxIn.put("6", "06");
    cvxIn.put("7", "07");
    cvxIn.put("8", "08");
    cvxIn.put("9", "08");
  }

  private static final Map<String, String[]> groupMapping = new HashMap<String, String[]>();
  static {
    groupMapping.put("01", new String[] {"7"});
    groupMapping.put("02", new String[] {"20"});
    groupMapping.put("03", new String[] {"17"});
    groupMapping.put("04", new String[] {"37"});
    groupMapping.put("05", new String[] {"37"});
    groupMapping.put("06", new String[] {"37"});
    groupMapping.put("07", new String[] {"37"});
    groupMapping.put("08", new String[] {"12"});
    groupMapping.put("09", new String[] {"40"});
    groupMapping.put("10", new String[] {"20"});
    groupMapping.put("11", new String[] {"7"});
    groupMapping.put("15", new String[] {"9"});
    groupMapping.put("16", new String[] {"9"});
    groupMapping.put("17", new String[] {"13"});
    groupMapping.put("18", new String[] {"21"});
    groupMapping.put("20", new String[] {"7"});
    groupMapping.put("21", new String[] {"26"});

    groupMapping.put("22", new String[] {"7", "13"});
    groupMapping.put("28", new String[] {"7"});
    groupMapping.put("31", new String[] {"11"});
    groupMapping.put("32", new String[] {"16"});
    groupMapping.put("33", new String[] {"34"});
    groupMapping.put("35", new String[] {"40"});
    groupMapping.put("38", new String[] {"37"});
    groupMapping.put("40", new String[] {"21"});
    groupMapping.put("42", new String[] {"12"});
    groupMapping.put("43", new String[] {"12"});
    groupMapping.put("44", new String[] {"12"});
    groupMapping.put("45", new String[] {"12"});
    groupMapping.put("46", new String[] {"13"});
    groupMapping.put("47", new String[] {"13"});
    groupMapping.put("48", new String[] {"13"});
    groupMapping.put("49", new String[] {"13"});

    groupMapping.put("50", new String[] {"7", "13"});

    groupMapping.put("51", new String[] {"12", "13"});
    groupMapping.put("52", new String[] {"11"});
    groupMapping.put("62", new String[] {"38"});
    groupMapping.put("74", new String[] {"23"});
    groupMapping.put("83", new String[] {"11"});
    groupMapping.put("84", new String[] {"11"});
    groupMapping.put("85", new String[] {"11"});
    groupMapping.put("88", new String[] {"9"});
    groupMapping.put("89", new String[] {"20"});
    groupMapping.put("90", new String[] {"21"});

    groupMapping.put("94", new String[] {"17", "26"});
    groupMapping.put("100", new String[] {"19"});


    groupMapping.put("102", new String[] {"7", "12", "13"});

    groupMapping.put("104", new String[] {"11", "12"});
    groupMapping.put("106", new String[] {"7"});
    groupMapping.put("107", new String[] {"7"});
    groupMapping.put("108", new String[] {"16"});
    groupMapping.put("109", new String[] {"19"});


    groupMapping.put("110", new String[] {"7", "12", "20"});
    groupMapping.put("111", new String[] {"9"});
    groupMapping.put("112", new String[] {"40"});
    groupMapping.put("114", new String[] {"16"});
    groupMapping.put("115", new String[] {"40"});
    groupMapping.put("116", new String[] {"23"});
    groupMapping.put("118", new String[] {"38"});
    groupMapping.put("119", new String[] {"23"});


    groupMapping.put("120", new String[] {"7", "13", "20"});
    groupMapping.put("121", new String[] {"39"});
    groupMapping.put("122", new String[] {"23"});
    groupMapping.put("125", new String[] {"69"});
    groupMapping.put("126", new String[] {"69"});

    groupMapping.put("127", new String[] {"69", "69"});
    groupMapping.put("128", new String[] {"41"});

    groupMapping.put("130", new String[] {"7", "20"});



    groupMapping.put("132", new String[] {"7", "12", "13", "20"});
    groupMapping.put("133", new String[] {"19"});
    groupMapping.put("135", new String[] {"9"});
    groupMapping.put("136", new String[] {"16"});
    groupMapping.put("137", new String[] {"38"});
    groupMapping.put("140", new String[] {"9"});
    groupMapping.put("141", new String[] {"9"});
    groupMapping.put("142", new String[] {"40"});
    groupMapping.put("144", new String[] {"13"});
  }

  public SWPConnector(Software software, List<ForecastItem> forecastItemList) {
    this.software = software;
    addForcastItem(forecastItemList, "Hib", 6);
    addForcastItem(forecastItemList, "HepB", 5);
    addForcastItem(forecastItemList, "DTP", 2);
    addForcastItem(forecastItemList, "Td/Tdap", 15);
    addForcastItem(forecastItemList, "Polio", 11);
    addForcastItem(forecastItemList, "HepA", 4);
    addForcastItem(forecastItemList, "MMR", 9);
    addForcastItem(forecastItemList, "Rotavirus", 12);
    addForcastItem(forecastItemList, "Varicella", 13);
    addForcastItem(forecastItemList, "Influenza", 3);
    addForcastItem(forecastItemList, "MCV4", 8);
    addForcastItem(forecastItemList, "HPV", 7);
    addForcastItem(forecastItemList, "HerpesZoster", 14);
    addForcastItem(forecastItemList, "Pneumo-Poly", 10);
    addForcastItem(forecastItemList, "Pneumonia", 10);
  }

  private void addForcastItem(List<ForecastItem> forecastItemList, String familyName, int forecastItemId) {
    for (ForecastItem forecastItem : forecastItemList) {
      if (forecastItem.getForecastItemId() == forecastItemId) {
        familyMapping.put(familyName, forecastItem);
        return;
      }
    }
  }

  public List<ForecastActual> queryForForecast(TestCase testCase) throws Exception {
    StringWriter sw = new StringWriter();
    PrintWriter logOut = new PrintWriter(sw);

    URLConnection urlConn;
    DataOutputStream printOut;
    URL url = new URL(software.getServiceUrl());
    urlConn = url.openConnection();
    urlConn.setDoInput(true);
    urlConn.setDoOutput(true);
    urlConn.setUseCaches(true);
    urlConn.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
    printOut = new DataOutputStream(urlConn.getOutputStream());
    StringBuilder sb = new StringBuilder();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:swp=\"http://swpartners.com\"> \n");
    sb.append("  <soapenv:Header/>  \n");
    sb.append("  <soapenv:Body>  \n");
    sb.append("    <swp:executeVFM>  \n");
    sb.append("      <swp:vfm>  \n");
    sb.append("         <swp:patient dob=\"" + sdf.format(testCase.getPatientDob()) + "\" gender=\""
        + testCase.getPatientSex() + "\">  \n");
    for (TestEvent testEvent : testCase.getTestEventList()) {
      if (testEvent.getEvent().getEventType() == EventType.VACCINE) {
        String cvx = testEvent.getEvent().getVaccineCvx();
        String[] groupArray = groupMapping.get(cvx);
        if (cvxOut.get(cvx) != null) {
          cvx = cvxOut.get(cvx);
        }
        if (groupArray != null) {
            logOut.println(" sending vaccine " + cvx + " given " + sdf.format(testEvent.getEventDate()));
          for (String groupId : groupArray) {
            logOut.println(" sending vaccine " + cvx + " given " + sdf.format(testEvent.getEventDate()) + " with group id = " + groupId);
            sb.append("            <swp:dose cvx=\"" + cvx + "\" ");
            sb.append("date=\"" + sdf.format(testEvent.getEventDate()) + "\" ");
            sb.append("groupid=\"" + groupId + "\"/> \n");
          }
        }
      }
    }
    sb.append("         </swp:patient>  \n");
    sb.append("       </swp:vfm>  \n");
    sb.append("     </swp:executeVFM>  \n");
    sb.append("   </soapenv:Body>  \n");
    sb.append("</soapenv:Envelope> \n");
    printOut.writeBytes(sb.toString());
    logOut.println("Querying SWP software for forecast");
    logOut.println(sb);
    printOut.flush();
    printOut.close();

    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    Document doc = docBuilder.parse(urlConn.getInputStream());

    List<ForecastActual> list = null;
    doc.getDocumentElement().normalize();
    NodeList nodes = doc.getChildNodes();
    if (nodes.getLength() == 1) {
      list =  parseEnvelope(nodes.item(0).getChildNodes(), logOut);
    }

    logOut.close();
    for (ForecastActual forecastActual : list)
    {
      forecastActual.setLogText(sw.toString());
    }
    return list;
  }

  private List<ForecastActual> parseEnvelope(NodeList nodes, PrintWriter logOut) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        logOut.println("  <" + node.getNodeName());
        if ("S:Body".equals(node.getNodeName())) {
          return parseBody(node.getChildNodes(), logOut);
        }
      }
    }
    return null;
  }

  private List<ForecastActual> parseBody(NodeList nodes, PrintWriter logOut) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        logOut.println("    <" + node.getNodeName());
        if ("executeVFM".equals(node.getNodeName())) {
          return parseExecute(node.getChildNodes(), logOut);
        }
      }
    }
    return null;
  }

  private List<ForecastActual> parseExecute(NodeList nodes, PrintWriter logOut) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        logOut.println("      <" + node.getNodeName());
        if ("vfm".equals(node.getNodeName())) {
          return parsePatient(node.getChildNodes(), logOut);
        }
      }
    }
    return null;
  }

  private List<ForecastActual> parsePatient(NodeList nodes, PrintWriter logOut) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        logOut.println("        <" + node.getNodeName());
        if ("patient".equals(node.getNodeName())) {
          return parseForecast(node.getChildNodes(), logOut);
        }
      }
    }
    return null;
  }

  private List<ForecastActual> parseForecast(NodeList nodes, PrintWriter logOut) {
    List<ForecastActual> list = new ArrayList<ForecastActual>();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        NamedNodeMap map = node.getAttributes();
        logOut.print("          <" + node.getNodeName());
        for (int j = 0; j < map.getLength(); j++) {
          Node n2 = map.item(j);
          if (n2.getNodeType() == Node.ATTRIBUTE_NODE) {
            logOut.print(" " + n2.getNodeName() + "=\"" + n2.getNodeValue() + "\"");
          }
        }
        logOut.println(">");
        if ("forecast".equals(node.getNodeName())) {
          String overduedate = safe(map.getNamedItem("overduedate"));
          // recommendeddate="2006-04-01" mindate="2006-04-01"
          // maxdate="2025-03-31" dosenum="1" cvx="45" family="HepB"
          // groupid="12"
          String recommendeddate = safe(map.getNamedItem("recommendeddate"));
          String mindate = safe(map.getNamedItem("mindate"));
          String maxdate = safe(map.getNamedItem("maxdate"));
          String dosenum = safe(map.getNamedItem("dosenum"));
          String cvx = safe(map.getNamedItem("cvx"));
          String family = safe(map.getNamedItem("family"));
          // String groupid = safe(map.getNamedItem("groupid"));
          ForecastItem forecastItem = familyMapping.get(family);

          if (forecastItem != null) {
            ForecastActual forecastActual = new ForecastActual();
            forecastActual.setForecastItem(forecastItem);
            forecastActual.setDoseNumber(dosenum);
            forecastActual.setValidDate(parseDate(mindate));
            forecastActual.setDueDate(parseDate(recommendeddate));
            forecastActual.setOverdueDate(parseDate(overduedate));
            forecastActual.setFinishedDate(parseDate(maxdate));
            list.add(forecastActual);
          }
        } 
      }
    }
    return list;
  }

  private Date parseDate(String s) {
    Date date = null;

    if (s.length() > 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      try {
        date = sdf.parse(s);
      } catch (ParseException pe) {
        // ignore
      }
    }
    return date;
  }

  private static String safe(Node n) {
    if (n == null) {
      return "";
    }
    String s = n.getNodeValue();
    if (s == null) {
      return "";
    }
    return s;
  }

}
