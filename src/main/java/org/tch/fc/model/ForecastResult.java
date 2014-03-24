package org.tch.fc.model;

import java.util.Date;


public abstract class ForecastResult {

  public static final String DOSE_NUMBER_COMPLETE = "COMP";
  public static final String DOSE_NUMBER_ERROR = "ERROR";

  protected TestCase testCase = null;
  protected ForecastItem forecastItem = null;
  protected String doseNumber = null;
  protected Date validDate = null;
  protected Date dueDate = null;
  protected Date overdueDate = null;
  protected Date finishedDate = null;
  protected String vaccineCvx = "";

  public String getVaccineCvx() {
    return vaccineCvx;
  }

  public void setVaccineCvx(String vaccineCvx) {
    this.vaccineCvx = vaccineCvx;
  }

  public Date getFinishedDate() {
    return finishedDate;
  }

  public void setFinishedDate(Date finishedDate) {
    this.finishedDate = finishedDate;
  }

  public TestCase getTestCase() {
    return testCase;
  }

  public void setTestCase(TestCase testCase) {
    this.testCase = testCase;
  }

  public ForecastItem getForecastItem() {
    return forecastItem;
  }

  public void setForecastItem(ForecastItem forecastItem) {
    this.forecastItem = forecastItem;
  }
  
  public void setComplete()
  {
    this.doseNumber = "COMP";
  }

  public String getDoseNumber() {
    return doseNumber;
  }

  public void setDoseNumber(String doseNumber) {
    this.doseNumber = doseNumber;
  }

  public Date getValidDate() {
    return validDate;
  }

  public void setValidDate(Date validDate) {
    this.validDate = validDate;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public Date getOverdueDate() {
    return overdueDate;
  }

  public void setOverdueDate(Date overdueDate) {
    this.overdueDate = overdueDate;
  }

  public boolean isComplete() {
    return doseNumber != null && doseNumber.equals(DOSE_NUMBER_COMPLETE);
  }

}
