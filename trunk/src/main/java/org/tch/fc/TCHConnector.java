package org.tch.fc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tch.fc.model.EvaluationActual;
import org.tch.fc.model.EventType;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.Software;
import org.tch.fc.model.SoftwareResult;
import org.tch.fc.model.SoftwareSetting;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestCaseSetting;
import org.tch.fc.model.TestEvent;
import org.tch.fc.model.VaccineGroup;

public class TCHConnector implements ConnectorInterface
{

  private static final String VACCINATION_LINE_PREFIX = "Vaccination #";

  private Map<String, List<VaccineGroup>> familyMapping = new HashMap<String, List<VaccineGroup>>();

  private Map<String, String> evaluationToCvxMapping = new HashMap<String, String>();

  private Software software = null;
  private boolean logText = false;

  public boolean isLogText() {
    return logText;
  }

  public void setLogText(boolean logText) {
    this.logText = logText;
  }

  public TCHConnector(Software software, List<VaccineGroup> forecastItemList) {
    this.software = software;
    addForcastItem(forecastItemList, "Hib", VaccineGroup.ID_HIB);
    addForcastItem(forecastItemList, "HepB", VaccineGroup.ID_HEPB);
    addForcastItem(forecastItemList, "DTaP", VaccineGroup.ID_DTAP);
    addForcastItem(forecastItemList, "DTaP", VaccineGroup.ID_DTAP_TDAP_TD);
    addForcastItem(forecastItemList, "Td", VaccineGroup.ID_TD_ONLY);
    addForcastItem(forecastItemList, "Td", VaccineGroup.ID_TDAP_TD);
    addForcastItem(forecastItemList, "Td", VaccineGroup.ID_DTAP_TDAP_TD);
    addForcastItem(forecastItemList, "Tdap", VaccineGroup.ID_TDAP_ONLY);
    addForcastItem(forecastItemList, "Tdap", VaccineGroup.ID_TDAP_TD);
    addForcastItem(forecastItemList, "Tdap", VaccineGroup.ID_DTAP_TDAP_TD);
    addForcastItem(forecastItemList, "IPV", VaccineGroup.ID_POLIO);
    addForcastItem(forecastItemList, "HepA", VaccineGroup.ID_HEPA);
    addForcastItem(forecastItemList, "MMR", VaccineGroup.ID_MMR);
    addForcastItem(forecastItemList, "Measles", VaccineGroup.ID_MEASLES_ONLY);
    addForcastItem(forecastItemList, "Mumps", VaccineGroup.ID_MUMPS_ONLY);
    addForcastItem(forecastItemList, "Rubella", VaccineGroup.ID_RUBELLA_ONLY);
    addForcastItem(forecastItemList, "Var", VaccineGroup.ID_VAR);
    addForcastItem(forecastItemList, "Influenza", VaccineGroup.ID_INFLUENZA);
    addForcastItem(forecastItemList, "MCV4", VaccineGroup.ID_MENING);
    addForcastItem(forecastItemList, "HPV", VaccineGroup.ID_HPV);
    addForcastItem(forecastItemList, "Rota", VaccineGroup.ID_ROTA);
    addForcastItem(forecastItemList, "PCV13", VaccineGroup.ID_PNEUMO);
    addForcastItem(forecastItemList, "PCV13", VaccineGroup.ID_PCV);
    addForcastItem(forecastItemList, "Zoster", VaccineGroup.ID_ZOSTER);
    addForcastItem(forecastItemList, "PPSV", VaccineGroup.ID_PPSV);

    evaluationToCvxMapping.put("Varicella", "21");
    evaluationToCvxMapping.put("Rubella", "06");
    evaluationToCvxMapping.put("Measles", "05");
    evaluationToCvxMapping.put("Influenza", "88");
    evaluationToCvxMapping.put("Hib", "17");
    evaluationToCvxMapping.put("HPV", "137");
    evaluationToCvxMapping.put("HepB", "45");
    evaluationToCvxMapping.put("HepA", "85");
    evaluationToCvxMapping.put("Diphtheria", "107");
    evaluationToCvxMapping.put("Mening", "147");
    evaluationToCvxMapping.put("Mumps", "07");
    evaluationToCvxMapping.put("Pertussis", "11");
    evaluationToCvxMapping.put("Pneumo", "152");
    evaluationToCvxMapping.put("Polio", "89");
    evaluationToCvxMapping.put("Rotavirus", "122");
    evaluationToCvxMapping.put("Zoster", "121");
    evaluationToCvxMapping.put("PPSV", "33");

  }

  private void addForcastItem(List<VaccineGroup> forecastItemList, String familyName, int forecastItemId) {
    for (VaccineGroup forecastItem : forecastItemList) {
      if (forecastItem.getVaccineGroupId() == forecastItemId) {
        List<VaccineGroup> forecastItemListFromMap = familyMapping.get(familyName);
        if (forecastItemListFromMap == null) {
          forecastItemListFromMap = new ArrayList<VaccineGroup>();
          familyMapping.put(familyName, forecastItemListFromMap);
        }
        forecastItemListFromMap.add(forecastItem);
        return;
      }
    }
  }

  public List<ForecastActual> queryForForecast(TestCase testCase) throws Exception {

    SoftwareResult softwareResult = new SoftwareResult();
    softwareResult.setSoftware(software);
    StringWriter sw = new StringWriter();
    PrintWriter logOut = null;
    if (logText) {
      logOut = new PrintWriter(sw);
    }
    String queryString = createQueryString(testCase, software, "text");
    if (logOut != null) {
      logOut.println("TCH Forecaster");
      logOut.println();
      logOut.println("Current time " + new Date());
      logOut.println("Connecting to " + software.getServiceUrl());
      logOut.println("Query " + software.getServiceUrl() + queryString);
      logOut.println();
    }
    URLConnection urlConn;
    URL url = new URL(software.getServiceUrl() + queryString);
    urlConn = url.openConnection();
    urlConn.setDoInput(true);
    urlConn.setDoOutput(true);
    urlConn.setUseCaches(true);
    urlConn.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
    urlConn.connect();

    InputStreamReader input = null;
    input = new InputStreamReader(urlConn.getInputStream());
    List<ForecastActual> list = new ArrayList<ForecastActual>();
    BufferedReader in = new BufferedReader(input);
    String line;
    if (logOut != null) {
      logOut.println("Results:");
    }
    while ((line = in.readLine()) != null) {
      if (logOut != null) {
        logOut.println(line);
      }
      line = line.trim();
      if (line.startsWith("Forecasting ")) {
        // Example lines
        // 0 1 2 3 4 5 6 7 8 9 10 11
        // Forecasting MMR dose 1 due 05/01/2066 valid 04/29/2006 overdue
        // 04/29/2006 finished 10/05/2009
        // Forecasting Hib complete
        String[] parts = line.split("\\s");
        if (parts.length > 2) {
          List<VaccineGroup> forecastItemListFromMap = familyMapping.get(parts[1]);
          if (forecastItemListFromMap != null) {
            for (VaccineGroup forecastItem : forecastItemListFromMap) {
              if (forecastItem != null) {
                ForecastActual forecastActual = new ForecastActual();
                forecastActual.setSoftwareResult(softwareResult);
                forecastActual.setVaccineGroup(forecastItem);
                if ("complete".equalsIgnoreCase(parts[2])) {
                  forecastActual.setDoseNumber("COMP");
                } else {
                  if (parts.length > 3 && "dose".equals(parts[2])) {
                    forecastActual.setDoseNumber(parts[3]);
                  }
                  if (parts.length > 5 && "due".equals(parts[4])) {
                    forecastActual.setDueDate(parseDate(parts[5]));
                  }
                  if (parts.length > 7 && "valid".equals(parts[6])) {
                    forecastActual.setValidDate(parseDate(parts[7]));
                  }
                  if (parts.length > 9 && "overdue".equals(parts[8])) {
                    forecastActual.setOverdueDate(parseDate(parts[9]));
                  }
                  if (parts.length > 11 && "finished".equals(parts[10])) {
                    forecastActual.setFinishedDate(parseDate(parts[11]));
                  }
                }
                list.add(forecastActual);
              }
            }
          }
        }
      } else if (line.startsWith(VACCINATION_LINE_PREFIX)) {
        line = line.substring(VACCINATION_LINE_PREFIX.length());
        int pos = line.indexOf(':');
        if (pos > 0) {
          try {
            int i = Integer.parseInt(line.substring(0, pos).trim());
            int count = 0;
            for (TestEvent testEvent : testCase.getTestEventList()) {
              if (testEvent.getEvent().getEventType() == EventType.VACCINE) {
                count++;
                if (count == i) {
                  line = line.substring(pos + 1).trim();
                  int isValidPos = line.indexOf(" is a valid ");
                  int isInvalidPos = line.indexOf(" is an invalid ");
                  if (isValidPos > 0 || isInvalidPos > 0) {
                    boolean isValid = isInvalidPos == -1 && isValidPos > -1;
                    pos = isInvalidPos + 15;
                    if (isInvalidPos == -1) {
                      pos = isValidPos + 12;
                    }
                    int startPos = line.indexOf(" dose ", pos);
                    String doseName = "";
                    String vaccineCvx = "";
                    String doseNumber = "";
                    if (startPos > 0) {
                      doseName = line.substring(pos, startPos).trim();
                      vaccineCvx = evaluationToCvxMapping.get(doseName);
                      if (vaccineCvx == null) {
                        vaccineCvx = "";
                      }
                      startPos += 6;
                      int endPos = line.indexOf(".", startPos);
                      if (endPos > 0) {
                        doseNumber = line.substring(startPos, endPos);
                      }
                    }
                    EvaluationActual evaluationActual = new EvaluationActual();
                    evaluationActual.setSoftwareResult(softwareResult);
                    evaluationActual.getSoftwareResult().setSoftware(software);
                    evaluationActual.setTestEvent(testEvent);
                    evaluationActual.setDoseNumber(doseNumber);
                    evaluationActual.setDoseValid(isValid ? "Y" : "N");
                    //evaluationActual.setReasonCode(reasonCode);
                    evaluationActual.setReasonText(line);
                    evaluationActual.setSeriesUsedCode(vaccineCvx);
                    evaluationActual.setSeriesUsedText(doseName);
                    evaluationActual.setVaccineCvx(vaccineCvx);

                    if (testEvent.getEvaluationActualList() == null) {
                      testEvent.setEvaluationActualList(new ArrayList<EvaluationActual>());
                    }
                    testEvent.getEvaluationActualList().add(evaluationActual);
                  }
                  break;
                }
              }
            }
          } catch (NumberFormatException nfe) {
            // ignore
          }
        }
      }
    }
    input.close();
    if (logOut != null) {
      logOut.close();
      softwareResult.setLogText(sw.toString());
    }

    return list;
  }

  public static String createQueryString(TestCase testCase, Software software, String format) {
    StringBuilder sb = new StringBuilder();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    sb.append("?evalDate=" + sdf.format(testCase.getEvalDate()));
    sb.append("&evalSchedule=");
    sb.append("&resultFormat=" + format);
    sb.append("&patientDob=" + sdf.format(testCase.getPatientDob()) + "");
    sb.append("&patientSex=" + testCase.getPatientSex() + "");
    int count = 0;
    for (TestEvent testEvent : testCase.getTestEventList()) {
      if (testEvent.getEvent() != null && testEvent.getEvent().getEventType() == EventType.VACCINE) {
        count++;
        sb.append("&vaccineDate" + count + "=" + sdf.format(testEvent.getEventDate()));
        sb.append("&vaccineCvx" + count + "=" + testEvent.getEvent().getVaccineCvx());
        sb.append("&vaccineMvx" + count + "=" + testEvent.getEvent().getVaccineMvx());
      }
    }

    Map<String, String> settingsMap = new HashMap<String, String>();
    if (software.getSoftwareSettingList() != null) {
      for (SoftwareSetting softwareSetting : software.getSoftwareSettingList()) {
        settingsMap.put(softwareSetting.getServiceOption().getOptionName(), softwareSetting.getOptionValue());
      }
    }
    if (testCase.getTestCaseSettingList() != null) {
      for (TestCaseSetting testCaseSetting : testCase.getTestCaseSettingList()) {
        settingsMap.put(testCaseSetting.getServiceOption().getOptionName(), testCaseSetting.getOptionValue());
      }
    }

    try {
      for (String key : settingsMap.keySet()) {
        sb.append("&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(settingsMap.get(key), "UTF-8"));
      }
    } catch (UnsupportedEncodingException uee) {
      uee.printStackTrace();
    }

    return sb.toString();
  }

  private Date parseDate(String s) {
    Date date = null;
    if (s == null) {
      return null;
    }

    if (s.length() > 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      try {
        date = sdf.parse(s);
      } catch (ParseException pe) {
        // ignore
      }
    }
    return date;
  }

}
