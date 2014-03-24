package org.tch.fc;

import java.io.PrintWriter;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tch.fc.model.EventType;
import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.ForecastItem;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;

import com.stchome.saf.common.GenderType;
import com.stchome.saf.common.VaccMessageType;
import com.stchome.saf.common.VaccineCodeType;
import com.stchome.saf.messages.get._1_2.ForecastDetailsType;
import com.stchome.saf.messages.get._1_2.GetForecastRequestSoap11Stub;
import com.stchome.saf.messages.get._1_2.PersonDetailsType;
import com.stchome.saf.messages.get._1_2.ResponseDetailType;
import com.stchome.saf.messages.get._1_2.VaccinationType;

import static org.tch.fc.model.ForecastItem.*;

public class STCConnector extends GetForecastRequestSoap11Stub implements ConnectorInterface {

  private Map<String, List<ForecastItem>> familyMapping = new HashMap<String, List<ForecastItem>>();
  private Map<String, String> notSupported = new HashMap<String, String>();

  private Software software = null;
  private List<ForecastItem> forecastItemList = null;

  private static String STC_HEP_3_DOSE = "4";
  private static String STC_HEP_2_DOSE = "12";

  public STCConnector(Software software, List<ForecastItem> forecastItemList) throws Exception {
    super(new URL(software.getServiceUrl()), null);
    this.software = software;
    this.forecastItemList = forecastItemList;
    map("1", ID_DTAP_TDAP_TD); // DTaP
    map("1", ID_DTAP); // DTaP
    map("1", ID_TDAP_TD); // DTaP
    map("2", ID_HIB); // Hib
    map("3", ID_POLIO); // IPV
    map("4", ID_HEPB); // HepB
    map("4", ID_HEPB_3_ONLY); // HepB
    map("5", ID_MMR); // MMR
    map("6", ID_VAR); // Var
    map("7", ID_MENING); // MCV4
    map("9", ID_HEPA); // HepA
    map("10", ID_INFLUENZA); // Influenza
    map("11", ID_PCV); // PCV13
    map("11", ID_PNEUMO); // PCV13
    map("12", ID_HEPB); // HepB 3 Dose
    map("12", ID_HEPB_2_ONLY); // Hep-B 2 Dose
    map("13", ID_ANTHRAX); // Anthrax
    map("14", ID_SMALLPOX_SHOT_OR_READING); // Smallpox shot/reading
    map("15", ID_MEASLES_ONLY); // Measles
    map("16", ID_MUMPS_ONLY); // Mumps
    map("17", ID_RUBELLA_ONLY); // Rubella
    map("18", ID_PPSV); // Pneumo PPSV
    map("20", ID_ROTA); // Rota
    map("21", ID_HPV); // HPV
    map("22", ID_ZOSTER); // Herpes Zoster
    map("23", ID_NOVEL_H1N1); // Novel Influenza
    map("24", ID_TDAP_TD); // Tdap
    map("24", ID_DTAP_TDAP_TD); // Tdap
  }

  private static final Map<String, String> stcFamilyCodeNameMap = new HashMap<String, String>();
  static {
    stcFamilyCodeNameMap.put("1", "DTaP/DT/Td/Tdap");
    stcFamilyCodeNameMap.put("2", "HIB");
    stcFamilyCodeNameMap.put("3", "POLIO");
    stcFamilyCodeNameMap.put("4", "HEP-B 3 DOSE");
    stcFamilyCodeNameMap.put("5", "MMR");
    stcFamilyCodeNameMap.put("6", "VARICELLA");
    stcFamilyCodeNameMap.put("7", "MENINGOCOCCAL");
    stcFamilyCodeNameMap.put("9", "HEP-A");
    stcFamilyCodeNameMap.put("10", "FLU");
    stcFamilyCodeNameMap.put("11", "PNEUMO (PCV)");
    stcFamilyCodeNameMap.put("12", "HEP-B 2 DOSE");
    stcFamilyCodeNameMap.put("13", "ANTHRAX");
    stcFamilyCodeNameMap.put("14", "SMALLPOX SHOT/READING");
    stcFamilyCodeNameMap.put("15", "MEASLES");
    stcFamilyCodeNameMap.put("16", "MUMPS");
    stcFamilyCodeNameMap.put("17", "RUBELLA");
    stcFamilyCodeNameMap.put("18", "PNEUMO (PPSV)");
    stcFamilyCodeNameMap.put("20", "ROTAVIRUS");
    stcFamilyCodeNameMap.put("21", "HPV");
    stcFamilyCodeNameMap.put("22", "HERPES ZOSTER");
    stcFamilyCodeNameMap.put("23", "NOVEL INFLUENZA H1N1-09");
    stcFamilyCodeNameMap.put("24", "Tdap");
  }
  
  private static String getStcLabel(String id)
  {
    String label = stcFamilyCodeNameMap.get(id);
    if (label == null)
    {
      return "Unknown STC Family Code " + id; 
    }
    return label;
  }

  private void map(String familyName, int forecastItemId) {
    for (ForecastItem forecastItem : forecastItemList) {
      if (forecastItem.getForecastItemId() == forecastItemId) {
        List<ForecastItem> forecastItemListFromMap = familyMapping.get(familyName);
        if (forecastItemListFromMap == null) {
          forecastItemListFromMap = new ArrayList<ForecastItem>();
          familyMapping.put(familyName, forecastItemListFromMap);
        }
        forecastItemListFromMap.add(forecastItem);
        return;
      }
    }
  }

  public List<ForecastActual> queryForForecast(TestCase testCase) throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    StringWriter sw = new StringWriter();
    PrintWriter logOut = new PrintWriter(sw);

    logOut.println("STC Forecaster");
    logOut.println();
    logOut.println("Current time " + new Date());
    logOut.println("Connecting to " + software.getServiceUrl());
    PersonDetailsType personDetailsType = new PersonDetailsType();
    personDetailsType.setDateOfBirth(sdf.format(testCase.getPatientDob()));
    personDetailsType.setGender("M".equals(testCase.getPatientSex()) ? GenderType.M : GenderType.F);
    personDetailsType.setPersonId(testCase.getTestCaseNumber());
    // sdf.format(testCase.getEvalDate())
    List<VaccinationType> vaccinationTypeList = new ArrayList<VaccinationType>();
    for (TestEvent testEvent : testCase.getTestEventList()) {
      if (testEvent.getEvent().getEventType() == EventType.VACCINE) {
        VaccinationType vaccinationType = new VaccinationType();
        String cvxCode = testEvent.getEvent().getVaccineCvx();
        if (cvxCode.startsWith("0")) {
          cvxCode = cvxCode.substring(1);
        }
        vaccinationType.setVaccCode(cvxCode);
        vaccinationType.setVaccDate(sdf.format(testEvent.getEventDate()));
        vaccinationType.setDoseSize(new BigDecimal(1.0));
        vaccinationType.setCodeType(VaccineCodeType.CVX);
        vaccinationTypeList.add(vaccinationType);
      }
    }
    personDetailsType.setVaccination(vaccinationTypeList.toArray(new VaccinationType[] {}));

    List<ForecastActual> list = new ArrayList<ForecastActual>();
    ResponseDetailType[] responseDetailTypes = getForecast(new PersonDetailsType[] { personDetailsType });
    if (responseDetailTypes.length == 0) {
      logOut.println("No results returned!");
    } else {
      ResponseDetailType responseDetailType = responseDetailTypes[0];
      personDetailsType = responseDetailType.getPersonDetails();
      logOut.println("Results returned");
      logOut.println("Information: " + responseDetailType.getInformation());

      if (personDetailsType.getVaccination() == null) {
        logOut.println("No vaccinations");
      } else {
        for (VaccinationType vaccinationType : personDetailsType.getVaccination()) {
          logOut.println("Vacc: " + vaccinationType.getVaccCode() + " given " + vaccinationType.getVaccDate());
          if (vaccinationType.getMessage() != null) {
            for (VaccMessageType message : vaccinationType.getMessage()) {
              logOut.println("  + " + message.getMessage() + " (" + message.getMessageType() + ")");
            }
          }
        }
      }

      for (ForecastDetailsType forecastDetailsType : responseDetailType.getForecastDetails()) {
        logOut.println("----------------------------------------------------------------");
        logOut.println("Reading " + getStcLabel(forecastDetailsType.getFamilyCode()));
        logOut.println(" + Dose:     " + forecastDetailsType.getDoseNumber());
        logOut.println(" + Due:      " + forecastDetailsType.getRecommendedDate());
        logOut.println(" + Valid:    " + forecastDetailsType.getMinAllowableDate());
        logOut.println(" + Overdue:  " + forecastDetailsType.getPastDueDate());
        logOut.println(" + Finished: " + forecastDetailsType.getMaxAllowableDate());
        List<ForecastItem> forecastItemListFromMap = familyMapping.get(forecastDetailsType.getFamilyCode());
        if (forecastItemListFromMap != null) {
          for (ForecastItem forecastItem : forecastItemListFromMap) {
            if (forecastItem == null) {
              String label = notSupported.get(forecastDetailsType.getFamilyCode());
              if (label != null) {
                logOut.println("Unsupported family code " + forecastDetailsType.getFamilyCode() + " (" + label + ")");
              } else {
                logOut.println("Unrecognized family code " + forecastDetailsType.getFamilyCode() + " ");
              }
            }
            else {
              if (forecastItem.getForecastItemId() == ID_DTAP || forecastItem.getForecastItemId() == ID_TDAP_TD) {
                // screen out the DTaP and Td recommendations for the age of the
                // patient
                Calendar sevenYearsOld = Calendar.getInstance();
                sevenYearsOld.setTime(testCase.getPatientDob());
                sevenYearsOld.add(Calendar.YEAR, 7);
                boolean isSevenYearsOldOrOlder = sevenYearsOld.after(testCase.getEvalDate());
                if (isSevenYearsOldOrOlder) {
                  if (forecastItem.getForecastItemId() == ID_DTAP) {
                    // Don't add DTaP recommendation because test case is now 7
                    // years or older at time of recommendation
                    logOut.println("Patient is 7 years old or older so not saving DTaP/Td/Tdap recommendation as DTaP");
                    continue;
                  }
                } else {
                  if (forecastItem.getForecastItemId() == ID_TDAP_TD) {
                    // Don't add Tdap recommendation because test case is not
                    // yet at 7 years of age
                    logOut.println("Patient is younger than 7 years so not saving DTaP/Td/Tdap recommendation as Tdap/Td");
                    continue;
                  }
                }
              } else if (forecastItem.getForecastItemId() == ID_HEPB) {
                // need to decide on which hep b recommendation is going to
                // survive
                // by default we assume that a 3 dose HepB series is used, the
                // two dose is
                // not as likely.
                // A 2 dose series can be given if the patient starts the series
                boolean use3Dose = true;
                final String HEPB_CVX = "08";
                final String HEPB_ADULT_CVX = "43";
                Collections.sort(testCase.getTestEventList(), new Comparator<TestEvent>() {
                  public int compare(TestEvent testEvent1, TestEvent testEvent2) {
                    return testEvent1.getEventDate().compareTo(testEvent2.getEventDate());
                  }
                });
                for (TestEvent testEvent : testCase.getTestEventList()) {
                  if (testEvent.getEvent().getEventType() == EventType.VACCINE) {
                    String cvxCode = testEvent.getEvent().getVaccineCvx();
                    if (cvxCode.equals(HEPB_CVX)) {
                      break;
                    } else if (cvxCode.equals(HEPB_ADULT_CVX)) {
                      use3Dose = false;
                      break;
                    }
                  }
                }
                if (use3Dose) {
                  if (forecastDetailsType.getFamilyCode().equals(STC_HEP_2_DOSE)) {
                    logOut.println("The first dose was for 3-dose series, not saving 2 dose series under Hep B forecast item.");
                    continue;
                  }
                } else {
                  if (forecastDetailsType.getFamilyCode().equals(STC_HEP_3_DOSE)) {
                    logOut.println("The first dose was for 2-dose series, not saving 3 dose series under Hep B forecast item.");
                    continue;
                  }
                }
              }
              logOut.println("Saving as results for forecast item " + forecastItem.getLabel());
              ForecastActual forecastActual = new ForecastActual();
              forecastActual.setForecastItem(forecastItem);
              forecastActual.setDoseNumber("" + forecastDetailsType.getDoseNumber());
              forecastActual.setDueDate(parseDate(forecastDetailsType.getRecommendedDate()));
              forecastActual.setValidDate(parseDate(forecastDetailsType.getMinAllowableDate()));
              forecastActual.setOverdueDate(parseDate(forecastDetailsType.getPastDueDate()));
              forecastActual.setFinishedDate(parseDate(forecastDetailsType.getMaxAllowableDate()));
              list.add(forecastActual);
            }
          }
        }
      }
    }

    logOut.close();
    for (ForecastActual forecastActual : list) {
      forecastActual.setLogText(sw.toString());
    }
    return list;
  }

  private Date parseDate(String s) {
    Date date = null;
    if (s == null) {
      return null;
    }

    if (s.length() > 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      try {
        date = sdf.parse(s);
      } catch (ParseException pe) {
        // ignore
      }
    }
    return date;
  }

}
