package org.tch.fc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Software;
import org.tch.fc.model.SoftwareResult;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;
import org.tch.fc.model.VaccineGroup;
import org.tch.fc.util.FakePatient;

public class IISConnector implements ConnectorInterface
{
  private Software software = null;
  private boolean logText = false;
  private String uniqueId;

  public IISConnector(Software software, List<VaccineGroup> forecastItemList) {
    this.software = software;
  }

  public List<ForecastActual> queryForForecast(TestCase testCase, SoftwareResult softwareResult) throws Exception {
    List<ForecastActual> forecastActualList = new ArrayList<ForecastActual>();
    StringWriter sw = new StringWriter();
    PrintWriter logOut = logText ? new PrintWriter(sw) : null;

    if (logText) {
      logOut.println("This service will attempt to send a fake VXU with the vaccination history and then request the forecast back using a QBP. ");
    }
    try {
      uniqueId = "" + System.currentTimeMillis() + nextIncrement();

      FakePatient fakePatient = new FakePatient(testCase, uniqueId);
      String vxu = buildVXU(testCase, fakePatient);
      if (logText) {
        logOut.println();
        logOut.println("VXU SENT: ");
        logOut.println(vxu);
      }
      String ack = sendRequest(vxu);
      if (logText) {
        logOut.println();
        logOut.println("ACK received back ...");
        logOut.println(ack);
      }

      uniqueId = "" + System.currentTimeMillis() + nextIncrement();
      String qbp = buildQBP(fakePatient);
      if (logText) {
        logOut.println();
        logOut.println("Sending QBP ...");
        logOut.println(qbp);
      }
      String rsp = sendRequest(qbp);
      if (logText) {
        logOut.println();
        logOut.println("RSP received back ...");
        logOut.println(rsp);
      }

      readRSP(forecastActualList, rsp);
    } catch (Exception e) {
      if (logOut != null) {
        logOut.println("Unable to get forecast results from IIS");
        e.printStackTrace(logOut);
      } else {
        e.printStackTrace();
      }
      throw new Exception("Unable to get forecast results", e);
    } finally {
      if (logOut != null) {
        logOut.close();
        softwareResult.setLogText(sw.toString());
      }
    }
    return forecastActualList;
  }

  private void readRSP(List<ForecastActual> forecastActualList, String rsp) throws IOException, ParseException {
    BufferedReader in = new BufferedReader(new StringReader(rsp));
    String line;
    boolean lookingForDummy = true;
    ForecastActual forecastActual = null;
    while ((line = in.readLine()) != null) {
      String[] f = line.split("\\|");
      if (f != null && f.length > 1 && f[0] != null && f[0].length() == 3) {
        String segmentName = f[0];
        if (lookingForDummy) {
          if (segmentName.equals("RXA")) {
            if (f.length > 5 && f[5].startsWith("998")) {
              lookingForDummy = false;
            }
          }
        } else {
          if (segmentName.equals("OBX") && f.length > 5) {
            String obsCode = readValue(f, 3);
            String obsValue = readValue(f, 5);

            if (obsCode.equals("30956-7")) {
              VaccineGroup vaccineGroup = null;
              try {
                vaccineGroup = VaccineGroup.getForecastItem(Integer.parseInt(obsValue));
              } catch (NumberFormatException nfe) {
                // ignore
              }
              if (vaccineGroup != null) {
                forecastActual = new ForecastActual();
                forecastActualList.add(forecastActual);
                forecastActual.setVaccineGroup(vaccineGroup);
                forecastActual.setVaccineCvx(obsValue);
              }
            } else if (obsCode.equals("59783-1")) {
              if (forecastActual != null) {
                forecastActual.setAdminStatus(obsValue);
              }
            } else if (obsCode.equals("30981-5")) {
              if (forecastActual != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                if (obsValue.length() == 8) {
                  forecastActual.setValidDate(sdf.parse(obsValue));
                }
              }
            } else if (obsCode.equals("30980-7")) {
              if (forecastActual != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                if (obsValue.length() == 8) {
                  forecastActual.setDueDate(sdf.parse(obsValue));
                }
              }
            }
          }

        }
        if (segmentName.equals("")) {

        }
      }
    }
  }

  private String readValue(String[] f, int pos) {
    String s = f[pos];
    int i = s.indexOf("^");
    if (i > 0) {
      s = s.substring(0, i);
    }
    String obsCode = s;
    return obsCode;
  }

  private static Integer increment = new Integer(1);

  private static int nextIncrement() {
    synchronized (increment) {
      if (increment < Integer.MAX_VALUE) {
        increment = increment + 1;
      } else {
        increment = 1;
      }
      return increment;
    }
  }

  private void createMSHforVXU(StringBuilder sb) {
    String sendingApp = software.getServiceFacilityid();
    String sendingFac = software.getServiceFacilityid();
    String receivingApp = "";
    String receivingFac = "";
    String sendingDateString;
    {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmssZ");
      sendingDateString = simpleDateFormat.format(new Date());
    }
    // build MSH
    sb.append("MSH|^~\\&|");
    sb.append(receivingApp + "|");
    sb.append(receivingFac + "|");
    sb.append(sendingApp + "|");
    sb.append(sendingFac + "|");
    sb.append(sendingDateString + "|");
    sb.append("|");
    sb.append("VXU^V04^VXU_V04|");
    sb.append(uniqueId + "|");
    sb.append("P|");
    sb.append("2.5.1|");
    sb.append("|");
    sb.append("|");
    sb.append("ER|");
    sb.append("AL|");
    sb.append("|");
    sb.append("|");
    sb.append("|");
    sb.append("|");
    sb.append("Z22^CDCPHINVS\r");
  }

  private void createMSHforQBP(StringBuilder sb) {
    String sendingApp = software.getServiceFacilityid();
    String sendingFac = software.getServiceFacilityid();
    String receivingApp = "";
    String receivingFac = "";
    String sendingDateString;
    {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmssZ");
      sendingDateString = simpleDateFormat.format(new Date());
    }
    // build MSH
    sb.append("MSH|^~\\&|");
    sb.append(receivingApp + "|");
    sb.append(receivingFac + "|");
    sb.append(sendingApp + "|");
    sb.append(sendingFac + "|");
    sb.append(sendingDateString + "|");
    sb.append("|");
    sb.append("QBP^Q11^QBP_Q11|");
    sb.append(uniqueId + "|");
    sb.append("P|");
    sb.append("2.5.1|");
    sb.append("|");
    sb.append("|");
    sb.append("ER|");
    sb.append("AL|");
    sb.append("|");
    sb.append("|");
    sb.append("|");
    sb.append("|");
    sb.append("Z44^CDCPHINVS\r");
  }

  public void printORC(StringBuilder sb, int count) {
    sb.append("ORC");
    // ORC-1
    sb.append("|RE");
    // ORC-2
    sb.append("|");
    // ORC-3
    sb.append("|");
    sb.append(uniqueId + "." + count + "^FITS");
    sb.append("\r");
  }

  public String buildVXU(TestCase testCase, FakePatient fakePatient) {

    StringBuilder sb = new StringBuilder();
    createMSHforVXU(sb);
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      // PID
      sb.append("PID");
      // PID-1
      sb.append("|1");
      // PID-2
      sb.append("|");
      // PID-3
      sb.append("|" + fakePatient.getMrn() + "^^^FITS^MR");
      // PID-4
      sb.append("|");
      // PID-5
      sb.append("|" + fakePatient.getNameLast() + "^" + fakePatient.getNameFirst() + "^" + fakePatient.getNameMiddle()
          + "^^^^L");
      // PID-6
      sb.append("|" + fakePatient.getMaidenLast() + "^" + fakePatient.getMaidenFirst() + "^^^^^M");
      // PID-7
      sb.append("|" + sdf.format(fakePatient.getPatientDob()));
      {
        // PID-8
        sb.append("|" + fakePatient.getPatientSex());
        // PID-9
        sb.append("|");
        // PID-10
        sb.append("|");
        // PID-11
        sb.append("|" + fakePatient.getAddressLine1() + "^^" + fakePatient.getAddressCity() + "^"
            + fakePatient.getAddressState() + "^" + fakePatient.getAddressZip() + "^USA");
        // PID-12
        sb.append("|");
        // PID-13
        sb.append("|");
        if (fakePatient.getPhone().length() == 10) {
          sb.append(
              "^PRN^PH^^^" + fakePatient.getPhone().substring(0, 3) + "^" + fakePatient.getPhone().substring(3, 10));
        }
      }
      sb.append("\r");

      sb.append("NK1");
      sb.append("|1");
      sb.append("|" + fakePatient.getMotherLast() + "^" + fakePatient.getMotherFirst() + "^^^^^L");
      sb.append("|MTH^Mother^HL70063");
      sb.append("\r");
      int count = 0;
      for (TestEvent testEvent : testCase.getTestEventList()) {
        count++;
        printORC(sb, count);
        sb.append("RXA");
        // RXA-1
        sb.append("|0");
        // RXA-2
        sb.append("|1");
        // RXA-3
        sb.append("|" + sdf.format(testEvent.getEventDate()));
        // RXA-4
        sb.append("|");
        // RXA-5
        sb.append("|" + testEvent.getEvent().getVaccineCvx() + "^" + testEvent.getEvent().getLabel() + "^CVX");
        {
          // RXA-6
          sb.append("|");
          sb.append("999");
          // RXA-7
          sb.append("|");
        }

        // RXA-8
        sb.append("|");
        // RXA-9
        sb.append("|");
        sb.append("01");
        // RXA-10
        sb.append("|");
        // RXA-11
        sb.append("|");
        // RXA-12
        sb.append("|");
        // RXA-13
        sb.append("|");
        // RXA-14
        sb.append("|");
        // RXA-15
        sb.append("|");
        // RXA-16
        sb.append("|");
        // RXA-17
        sb.append("|");
        sb.append(testEvent.getEvent().getVaccineMvx() + "^" + testEvent.getEvent().getVaccineMvx() + "^MVX");
        // RXA-18
        sb.append("|");
        // RXA-19
        sb.append("|");
        // RXA-20
        sb.append("|");
        // RXA-21
        sb.append("|A");
        sb.append("\r");
      }
    }

    return sb.toString();
  }

  public String buildQBP(FakePatient fakePatient) {

    StringBuilder sb = new StringBuilder();
    createMSHforQBP(sb);
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      // QPD
      sb.append("QPD");
      // QPD-1
      sb.append("|Z44^Request Evaluated History and Forecast^CDCPHINVS");
      // QPD-2
      sb.append("|" + uniqueId);
      // QPD-3
      sb.append("|" + fakePatient.getMrn() + "^^^FITS^MR");
      // QPD-4
      sb.append("|" + fakePatient.getNameLast() + "^" + fakePatient.getNameFirst() + "^" + fakePatient.getNameMiddle()
          + "^^^^L");
      // QPD-5
      sb.append("|" + fakePatient.getMaidenLast() + "^" + fakePatient.getMaidenFirst() + "^^^^^M");
      // QPD-6
      sb.append("|" + sdf.format(fakePatient.getPatientDob()));
      // QPD-7
      sb.append("|" + fakePatient.getPatientSex());
      // PID-8
      sb.append("|" + fakePatient.getAddressLine1() + "^^" + fakePatient.getAddressCity() + "^"
          + fakePatient.getAddressState() + "^" + fakePatient.getAddressZip() + "^USA^P");
      sb.append("\r");
      sb.append("RCP|I|1^RD&Records&HL70126");
      sb.append("\r");
    }
    return sb.toString();
  }

  public boolean isLogText() {
    return logText;
  }

  public void setLogText(boolean logText) {
    this.logText = logText;
  }

  private String sendRequest(String request) throws IOException {
    URLConnection urlConn;
    DataOutputStream printout;
    InputStreamReader input = null;
    URL url = new URL(software.getServiceUrl());
    urlConn = url.openConnection();
    urlConn.setDoInput(true);
    urlConn.setDoOutput(true);
    urlConn.setUseCaches(false);
    urlConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
    urlConn.setRequestProperty("SOAPAction", "\"http://tempuri.org/ExecuteHL7Message\"");
    printout = new DataOutputStream(urlConn.getOutputStream());
    StringWriter stringWriter = new StringWriter();
    PrintWriter out = new PrintWriter(stringWriter);
    request = request.replaceAll("\\&", "&amp;");
    out.println("<?xml version='1.0' encoding='UTF-8'?>");
    out.println("<Envelope xmlns=\"http://www.w3.org/2003/05/soap-envelope\">");
    out.println("   <Header />");
    out.println("   <Body>");
    out.println("      <submitSingleMessage xmlns=\"urn:cdc:iisb:2011\">");
    out.println("         <username>" + software.getServiceUserid() + "</username>");
    out.println("         <password>" + software.getServicePassword() + "</password>");
    out.println("         <facilityID>" + software.getServiceFacilityid() + "</facilityID>");
    out.println("         <hl7Message>" + request + "</hl7Message>");
    out.println("      </submitSingleMessage>");
    out.println("   </Body>");
    out.println("</Envelope>");

    printout.writeBytes(stringWriter.toString());
    printout.flush();
    printout.close();
    input = new InputStreamReader(urlConn.getInputStream());
    StringBuilder response = new StringBuilder();
    BufferedReader in = new BufferedReader(input);
    String line;
    while ((line = in.readLine()) != null) {
      response.append(line);
      response.append('\r');
    }
    input.close();
    String s = response.toString();
    int start = s.indexOf("MSH|");
    if (start > 0) {
      s = s.substring(start);
      int e = s.indexOf("</");
      if (e > 0) {
        s = s.substring(0, e);
      }
    }
    s = s.replace("&amp;", "\\&");
    if (s.endsWith("]]>")) {
      s = s.substring(0, s.length() - 3);
    }
    return s;
  }

}
