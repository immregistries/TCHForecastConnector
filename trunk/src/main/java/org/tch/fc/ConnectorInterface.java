package org.tch.fc;

import java.util.List;

import org.tch.fc.model.ForecastActual;
import org.tch.fc.model.SoftwareResult;
import org.tch.fc.model.TestCase;

public interface ConnectorInterface {
  /**
   * Given a test case, query the software to receive back a list of Forecast Actual results. 
   * @param forecastActualList
   * @return
   */
  public List<ForecastActual> queryForForecast(TestCase testCase, SoftwareResult softwareResult) throws Exception;
  
  public void setLogText(boolean logText);
  
  public boolean isLogText();
}
