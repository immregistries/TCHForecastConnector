package org.tch.fc.model;

import java.io.Serializable;

public enum EventType implements Serializable {

  VACCINE("V", "Vaccination"), HISTORY_OF_DISEASE("H", "History of Disease"), CONTRAINDICATION("C", "Contraindication"), ;
  private static final long serialVersionUID = 1L;

  private String eventTypeCode = "";
  private String label = "";

  EventType(String eventTypeCode, String label) {
    this.eventTypeCode = eventTypeCode;
    this.label = label;
  }

  public String getEventTypeCode() {
    return eventTypeCode;
  }

  public void setEventTypeCode(String eventTypeCode) {
    this.eventTypeCode = eventTypeCode;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public static EventType getEventType(String eventTypeCode) {
    for (EventType eventType : values()) {
      if (eventType.getEventTypeCode().equalsIgnoreCase(eventTypeCode)) {
        return eventType;
      }
    }
    return null;
  }

}
