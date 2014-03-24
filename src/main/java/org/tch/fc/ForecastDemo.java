package org.tch.fc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.ForecastItem;
import org.tch.fc.model.Service;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;

public class ForecastDemo {
  private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

  public static void main(String[] args) throws Exception {
    String serviceUrl = "http://tchforecasttester.org/fv/forecast";
    Service service = Service.TCH;

    // java org.tch.fc.ForecastDemo service=tch
    // serviceUrl=http://tchforecasttester.org/fv/forecast evalDate=07/12/2011
    // patientDob=04/01/2006 patientSex=F vaccine08=04/01/2006

    TestCase testCase = new TestCase();
    testCase.setEvalDate(sdf.parse("07/12/2011"));
    testCase.setPatientSex("F");
    testCase.setPatientDob(sdf.parse("04/01/2006"));
    List<TestEvent> testEventList = new ArrayList<TestEvent>();
    testCase.setTestEventList(testEventList);

    for (String arg : args) {
      int pos;
      if ((pos = arg.indexOf("=")) != -1) {
        String key = arg.substring(0, pos).trim();
        String value = arg.substring(pos + 1).trim();
        if (key.equals("service")) {
          service = Service.getService(value);
        } else if (key.equals("serviceUrl")) {
          serviceUrl = value;
        } else if (key.equals("evalDate")) {
          testCase.setEvalDate(sdf.parse(value));
        } else if (key.equals("patientDob")) {
          testCase.setPatientDob(sdf.parse(value));
        } else if (key.equals("patientSex")) {
          testCase.setPatientSex(value);
        } else if (key.startsWith("vaccine")) {
          int vaccineId = Integer.parseInt(key.substring("vaccine".length()));
          testEventList.add(new TestEvent(vaccineId, sdf.parse(value)));
        }
      }
    }

    Software software = new Software();
    software.setServiceUrl(serviceUrl);
    software.setService(service);

    ConnectorInterface connector = ConnectFactory.createConnecter(software, ForecastItem.getForecastItemList());
    List<ForecastActual> forecastActualList = connector.queryForForecast(testCase);

    System.out.println("RAW RESULTS");
    System.out.println();
    if (forecastActualList.size() > 0) {
      System.out.print(forecastActualList.get(0).getLogText().toString());
    }
    System.out.println();
    System.out.println("RESULTS");

    System.out.println("+----------------+------+------------+------------+------------+------------+-----+");
    System.out.print(left("Forecast Item", 15));
    System.out.print(left("Dose", 5));
    System.out.print(left("Valid", 11));
    System.out.print(left("Due", 11));
    System.out.print(left("Overdue", 11));
    System.out.print(left("Finished", 11));
    System.out.print(left("CVX", 4));
    System.out.println("|");
    System.out.println("+----------------+------+------------+------------+------------+------------+-----+");
    for (ForecastActual forecastActual : forecastActualList) {
      System.out.print(left(forecastActual.getForecastItem().getLabel(), 15));
      System.out.print(left(forecastActual.getDoseNumber(), 5));
      System.out.print(left(forecastActual.getValidDate(), 11));
      System.out.print(left(forecastActual.getDueDate(), 11));
      System.out.print(left(forecastActual.getOverdueDate(), 11));
      System.out.print(left(forecastActual.getFinishedDate(), 11));
      System.out.print(left(forecastActual.getVaccineCvx(), 4));
      System.out.println("|");
    }
    System.out.println("+----------------+------+------------+------------+------------+------------+-----+");

  }

  private static String left(Date date, int length) {
    if (date == null) {
      return left("", length);
    }
    return left(sdf.format(date), length);
  }

  private static String left(String text, int length) {
    String result = text + "                                              ";
    result = result.substring(0, length);
    return "| " + result;
  }
}
