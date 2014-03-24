/*
 * Copyright 2013 - Texas Children's Hospital
 * 
 *   Texas Children's Hospital licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */
package org.tch.fc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.ForecastItem;
import org.tch.fc.model.Service;
import org.tch.fc.model.Software;
import org.tch.fc.model.TestCase;
import org.tch.fc.model.TestEvent;

public class TestConnect extends junit.framework.TestCase {

  public void testConnectSWP() throws Exception {

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    TestCase testCase = new TestCase();
    testCase.setEvalDate(sdf.parse("07/12/2011"));
    testCase.setPatientSex("F");
    testCase.setPatientDob(sdf.parse("04/01/2006"));
    List<TestEvent> testEventList = new ArrayList<TestEvent>();
    testEventList.add(new TestEvent(8, sdf.parse("04/01/2006")));
    testCase.setTestEventList(testEventList);
    Software software = new Software();
    software.setServiceUrl("http://69.64.70.10:8080/vfmservice/VFMWebService");
    software.setService(Service.SWP);

    ConnectorInterface connector = ConnectFactory.createConnecter(software, ForecastItem.getForecastItemList());
    List<ForecastActual> forecastActualList = connector.queryForForecast(testCase);
    assertNotNull(forecastActualList);
    boolean foundHepB = false;
    for (ForecastActual forecastActual : forecastActualList) {
      System.out.println("--> " + forecastActual.getForecastItem().getLabel() + " Dose "
          + forecastActual.getDoseNumber() + " Due " + sdf.format(forecastActual.getDueDate()) + " Valid " + sdf.format(forecastActual.getValidDate()) + " Overdue " + sdf.format(forecastActual.getOverdueDate()));
      if (forecastActual.getForecastItem().getForecastItemId() == 5) {
        assertEquals(sdf.parse("05/01/2006"), forecastActual.getDueDate());
        foundHepB = true;
      }
    }
    assertTrue("HepB forecast not found", foundHepB);
  }

  public void testConnectTCH() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    TestCase testCase = new TestCase();
    testCase.setEvalDate(sdf.parse("07/12/2011"));
    testCase.setPatientSex("F");
    testCase.setPatientDob(sdf.parse("04/01/2006"));
    List<TestEvent> testEventList = new ArrayList<TestEvent>();
    testEventList.add(new TestEvent(8, sdf.parse("04/01/2006")));
    testCase.setTestEventList(testEventList);
    Software software = new Software();
    software.setServiceUrl("http://tchforecasttester.org/fv/forecast");
    software.setService(Service.TCH);

    ConnectorInterface connector = ConnectFactory.createConnecter(software, ForecastItem.getForecastItemList());
    List<ForecastActual> forecastActualList = connector.queryForForecast(testCase);
    assertNotNull(forecastActualList);
    boolean foundHepB = false;
    for (ForecastActual forecastActual : forecastActualList) {
      if (forecastActual.getForecastItem().getForecastItemId() == 5) {
        assertEquals(sdf.parse("05/01/2006"), forecastActual.getDueDate());
        assertNotNull(forecastActual.getLogText());
        System.out.print(forecastActual.getLogText().toString());
        foundHepB = true;
      }
    }
    assertTrue("HepB forecast not found", foundHepB);
    
  }
  
  public void testConnectSTC() throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    TestCase testCase = new TestCase();
    testCase.setEvalDate(sdf.parse("07/12/2011"));
    testCase.setPatientSex("F");
    testCase.setPatientDob(sdf.parse("04/01/2006"));
    List<TestEvent> testEventList = new ArrayList<TestEvent>();
    testEventList.add(new TestEvent(8, sdf.parse("04/01/2006")));
    testCase.setTestEventList(testEventList);
    Software software = new Software();
    software.setServiceUrl("http://epicenter.stchome.com/safdemo/soa/forecast/getForecast.wsdl");
    software.setService(Service.STC);

    ConnectorInterface connector = ConnectFactory.createConnecter(software, ForecastItem.getForecastItemList());
    List<ForecastActual> forecastActualList = connector.queryForForecast(testCase);
    assertNotNull(forecastActualList);
    boolean foundHepB = false;
    for (ForecastActual forecastActual : forecastActualList) {
      if (forecastActual.getForecastItem().getForecastItemId() == 5) {
        assertEquals(sdf.parse("04/01/2006"), forecastActual.getDueDate());
        assertNotNull(forecastActual.getLogText());
        System.out.print(forecastActual.getLogText().toString());
        foundHepB = true;
      }
    }
    assertTrue("HepB forecast not found", foundHepB);
    
  }

}
