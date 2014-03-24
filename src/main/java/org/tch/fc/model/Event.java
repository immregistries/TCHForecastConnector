package org.tch.fc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event implements Serializable {
  
  public Event()
  {
    // default;
  }
  
  private Event(int eventId, String label, EventType eventType, String vaccineCvx, String vaccineMvx)
  {
    this.eventId = eventId;
    this.label = label;
    this.eventType = eventType;
    this.vaccineCvx = vaccineCvx;
    this.vaccineMvx = vaccineMvx;
  }

  private static final long serialVersionUID = 1L;

  private int eventId = 0;
  private String label = "";
  private EventType eventType = null;
  private String vaccineCvx = "";
  private String vaccineMvx = "";

  public int getEventId() {
    return eventId;
  }

  public void setEventId(int eventId) {
    this.eventId = eventId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  public void setEventTypeCode(String eventTypeCode) {
    this.eventType = EventType.getEventType(eventTypeCode);
  }

  public String getEventTypeCode() {
    return eventType == null ? null : eventType.getEventTypeCode();
  }

  public String getVaccineCvx() {
    return vaccineCvx;
  }

  public void setVaccineCvx(String vaccineCvx) {
    this.vaccineCvx = vaccineCvx;
  }

  public String getVaccineMvx() {
    return vaccineMvx;
  }

  public void setVaccineMvx(String vaccineMvx) {
    this.vaccineMvx = vaccineMvx;
  }

  @Override
  public String toString() {
    return (eventType == null ? "" : eventType.getLabel()) + ": " + label;
  }
  
  private static List<Event> eventList = null;
  private static Map<Integer, Event> eventMap = null;
  
  public static List<Event> getEventList()
  {
    initEventListAndMap();
    return eventList;
  }
  
  public static Event getEvent(int eventId)
  {
    initEventListAndMap();
    return eventMap.get(eventId);
  }

  public static void initEventListAndMap() {
    if (eventList == null)
    {
      eventList = new ArrayList<Event>();
      eventList.add(new Event(1, "DTP", EventType.VACCINE, "01", ""));
      eventList.add(new Event(2, "OPV", EventType.VACCINE, "02", ""));
      eventList.add(new Event(3, "MMR", EventType.VACCINE, "03", ""));
      eventList.add(new Event(4, "M/R", EventType.VACCINE, "04", ""));
      eventList.add(new Event(5, "measles", EventType.VACCINE, "05", ""));
      eventList.add(new Event(6, "rubella", EventType.VACCINE, "06", ""));
      eventList.add(new Event(7, "mumps", EventType.VACCINE, "07", ""));
      eventList.add(new Event(8, "Hep B, adolescent or pediatric", EventType.VACCINE, "08", ""));
      eventList.add(new Event(9, "Td (adult), adsorbed", EventType.VACCINE, "09", ""));
      eventList.add(new Event(10, "IPV", EventType.VACCINE, "10", ""));
      eventList.add(new Event(11, "pertussis", EventType.VACCINE, "11", ""));
      eventList.add(new Event(12, "diphtheria antitoxin", EventType.VACCINE, "12", ""));
      eventList.add(new Event(13, "TIG", EventType.VACCINE, "13", ""));
      eventList.add(new Event(14, "IG, unspecified formulation", EventType.VACCINE, "14", ""));
      eventList.add(new Event(15, "influenza, split (incl. purified surface antigen)", EventType.VACCINE, "15", ""));
      eventList.add(new Event(16, "influenza, whole", EventType.VACCINE, "16", ""));
      eventList.add(new Event(17, "Hib, unspecified formulation", EventType.VACCINE, "17", ""));
      eventList.add(new Event(18, "rabies, intramuscular injection", EventType.VACCINE, "18", ""));
      eventList.add(new Event(19, "BCG", EventType.VACCINE, "19", ""));
      eventList.add(new Event(20, "DTaP", EventType.VACCINE, "20", ""));
      eventList.add(new Event(21, "varicella", EventType.VACCINE, "21", ""));
      eventList.add(new Event(22, "DTP-Hib", EventType.VACCINE, "22", ""));
      eventList.add(new Event(23, "plague", EventType.VACCINE, "23", ""));
      eventList.add(new Event(24, "anthrax", EventType.VACCINE, "24", ""));
      eventList.add(new Event(25, "typhoid, oral", EventType.VACCINE, "25", ""));
      eventList.add(new Event(26, "cholera", EventType.VACCINE, "26", ""));
      eventList.add(new Event(27, "botulinum antitoxin", EventType.VACCINE, "27", ""));
      eventList.add(new Event(28, "DT (pediatric)", EventType.VACCINE, "28", ""));
      eventList.add(new Event(29, "CMVIG", EventType.VACCINE, "29", ""));
      eventList.add(new Event(30, "HBIG", EventType.VACCINE, "30", ""));
      eventList.add(new Event(31, "Hep A, pediatric, unspecified formulation", EventType.VACCINE, "31", ""));
      eventList.add(new Event(32, "meningococcal MPSV4", EventType.VACCINE, "32", ""));
      eventList.add(new Event(33, "pneumococcal polysaccharide PPV23", EventType.VACCINE, "33", ""));
      eventList.add(new Event(34, "RIG", EventType.VACCINE, "34", ""));
      eventList.add(new Event(35, "tetanus toxoid, adsorbed", EventType.VACCINE, "35", ""));
      eventList.add(new Event(36, "VZIG", EventType.VACCINE, "36", ""));
      eventList.add(new Event(37, "yellow fever", EventType.VACCINE, "37", ""));
      eventList.add(new Event(38, "rubella/mumps", EventType.VACCINE, "38", ""));
      eventList.add(new Event(39, "Japanese encephalitis SC", EventType.VACCINE, "39", ""));
      eventList.add(new Event(40, "rabies, intradermal injection", EventType.VACCINE, "40", ""));
      eventList.add(new Event(41, "typhoid, parenteral", EventType.VACCINE, "41", ""));
      eventList.add(new Event(42, "Hep B, adolescent/high risk infant", EventType.VACCINE, "42", ""));
      eventList.add(new Event(43, "Hep B, adult", EventType.VACCINE, "43", ""));
      eventList.add(new Event(44, "Hep B, dialysis", EventType.VACCINE, "44", ""));
      eventList.add(new Event(45, "Hep B, unspecified formulation", EventType.VACCINE, "45", ""));
      eventList.add(new Event(46, "Hib (PRP-D)", EventType.VACCINE, "46", ""));
      eventList.add(new Event(47, "Hib (HbOC)", EventType.VACCINE, "47", ""));
      eventList.add(new Event(48, "Hib (PRP-T)", EventType.VACCINE, "48", ""));
      eventList.add(new Event(49, "Hib (PRP-OMP)", EventType.VACCINE, "49", ""));
      eventList.add(new Event(50, "DTaP-Hib", EventType.VACCINE, "50", ""));
      eventList.add(new Event(51, "Hib-Hep B", EventType.VACCINE, "51", ""));
      eventList.add(new Event(52, "Hep A, adult", EventType.VACCINE, "52", ""));
      eventList.add(new Event(53, "typhoid, parenteral, AKD (U.S. military)", EventType.VACCINE, "53", ""));
      eventList.add(new Event(54, "adenovirus, type 4", EventType.VACCINE, "54", ""));
      eventList.add(new Event(55, "adenovirus, type 7", EventType.VACCINE, "55", ""));
      eventList.add(new Event(56, "dengue fever", EventType.VACCINE, "56", ""));
      eventList.add(new Event(57, "hantavirus", EventType.VACCINE, "57", ""));
      eventList.add(new Event(58, "Hep C", EventType.VACCINE, "58", ""));
      eventList.add(new Event(59, "Hep E", EventType.VACCINE, "59", ""));
      eventList.add(new Event(60, "herpes simplex 2", EventType.VACCINE, "60", ""));
      eventList.add(new Event(61, "HIV", EventType.VACCINE, "61", ""));
      eventList.add(new Event(62, "HPV, quadrivalent", EventType.VACCINE, "62", ""));
      eventList.add(new Event(63, "Junin virus", EventType.VACCINE, "63", ""));
      eventList.add(new Event(64, "leishmaniasis", EventType.VACCINE, "64", ""));
      eventList.add(new Event(65, "leprosy", EventType.VACCINE, "65", ""));
      eventList.add(new Event(66, "Lyme disease", EventType.VACCINE, "66", ""));
      eventList.add(new Event(67, "malaria", EventType.VACCINE, "67", ""));
      eventList.add(new Event(68, "melanoma", EventType.VACCINE, "68", ""));
      eventList.add(new Event(69, "parainfluenza-3", EventType.VACCINE, "69", ""));
      eventList.add(new Event(70, "Q fever", EventType.VACCINE, "70", ""));
      eventList.add(new Event(71, "RSV-IGIV", EventType.VACCINE, "71", ""));
      eventList.add(new Event(72, "rheumatic fever", EventType.VACCINE, "72", ""));
      eventList.add(new Event(73, "Rift Valley fever", EventType.VACCINE, "73", ""));
      eventList.add(new Event(74, "rotavirus, tetravalent", EventType.VACCINE, "74", ""));
      eventList.add(new Event(75, "vaccinia (smallpox)", EventType.VACCINE, "75", ""));
      eventList.add(new Event(76, "Staphylococcus bacterio lysate", EventType.VACCINE, "76", ""));
      eventList.add(new Event(77, "tick-borne encephalitis", EventType.VACCINE, "77", ""));
      eventList.add(new Event(78, "tularemia vaccine", EventType.VACCINE, "78", ""));
      eventList.add(new Event(79, "vaccinia immune globulin", EventType.VACCINE, "79", ""));
      eventList.add(new Event(80, "VEE, live", EventType.VACCINE, "80", ""));
      eventList.add(new Event(81, "VEE, inactivated", EventType.VACCINE, "81", ""));
      eventList.add(new Event(82, "adenovirus, unspecified formulation", EventType.VACCINE, "82", ""));
      eventList.add(new Event(83, "Hep A, ped/adol, 2 dose", EventType.VACCINE, "83", ""));
      eventList.add(new Event(84, "Hep A, ped/adol, 3 dose", EventType.VACCINE, "84", ""));
      eventList.add(new Event(85, "Hep A, unspecified formulation", EventType.VACCINE, "85", ""));
      eventList.add(new Event(86, "IG", EventType.VACCINE, "86", ""));
      eventList.add(new Event(87, "IGIV", EventType.VACCINE, "87", ""));
      eventList.add(new Event(88, "influenza, unspecified formulation", EventType.VACCINE, "88", ""));
      eventList.add(new Event(89, "polio, unspecified formulation", EventType.VACCINE, "89", ""));
      eventList.add(new Event(90, "rabies, unspecified formulation", EventType.VACCINE, "90", ""));
      eventList.add(new Event(91, "typhoid, unspecified formulation", EventType.VACCINE, "91", ""));
      eventList.add(new Event(92, "VEE, unspecified formulation", EventType.VACCINE, "92", ""));
      eventList.add(new Event(93, "RSV-MAb", EventType.VACCINE, "93", ""));
      eventList.add(new Event(94, "MMRV", EventType.VACCINE, "94", ""));
      eventList.add(new Event(95, "TST-OT tine test", EventType.VACCINE, "95", ""));
      eventList.add(new Event(96, "TST-PPD intradermal", EventType.VACCINE, "96", ""));
      eventList.add(new Event(97, "TST-PPD tine test", EventType.VACCINE, "97", ""));
      eventList.add(new Event(98, "TST, unspecified formulation", EventType.VACCINE, "98", ""));
      eventList.add(new Event(99, "RESERVED - do not use", EventType.VACCINE, "99", ""));
      eventList.add(new Event(100, "pneumococcal conjugate PCV 7", EventType.VACCINE, "100", ""));
      eventList.add(new Event(101, "typhoid, ViCPs", EventType.VACCINE, "101", ""));
      eventList.add(new Event(102, "DTP-Hib-Hep B", EventType.VACCINE, "102", ""));
      eventList.add(new Event(103, "meningococcal C conjugate", EventType.VACCINE, "103", ""));
      eventList.add(new Event(104, "Hep A-Hep B", EventType.VACCINE, "104", ""));
      eventList.add(new Event(105, "vaccinia (smallpox) diluted", EventType.VACCINE, "105", ""));
      eventList.add(new Event(106, "DTaP, 5 pertussis antigens", EventType.VACCINE, "106", ""));
      eventList.add(new Event(107, "DTaP, unspecified formulation", EventType.VACCINE, "107", ""));
      eventList.add(new Event(108, "meningococcal, unspecified formulation", EventType.VACCINE, "108", ""));
      eventList.add(new Event(109, "pneumococcal, unspecified formulation", EventType.VACCINE, "109", ""));
      eventList.add(new Event(110, "DTaP-Hep B-IPV", EventType.VACCINE, "110", ""));
      eventList.add(new Event(111, "influenza, live, intranasal", EventType.VACCINE, "111", ""));
      eventList.add(new Event(112, "tetanus toxoid, unspecified formulation", EventType.VACCINE, "112", ""));
      eventList.add(new Event(113, "Td (adult) preservative free", EventType.VACCINE, "113", ""));
      eventList.add(new Event(114, "meningococcal MCV4P", EventType.VACCINE, "114", ""));
      eventList.add(new Event(115, "Tdap", EventType.VACCINE, "115", ""));
      eventList.add(new Event(116, "rotavirus, pentavalent", EventType.VACCINE, "116", ""));
      eventList.add(new Event(117, "VZIG (IND)", EventType.VACCINE, "117", ""));
      eventList.add(new Event(118, "HPV, bivalent", EventType.VACCINE, "118", ""));
      eventList.add(new Event(119, "rotavirus, monovalent", EventType.VACCINE, "119", ""));
      eventList.add(new Event(120, "DTaP-Hib-IPV", EventType.VACCINE, "120", ""));
      eventList.add(new Event(121, "zoster", EventType.VACCINE, "121", ""));
      eventList.add(new Event(122, "rotavirus, unspecified formulation", EventType.VACCINE, "122", ""));
      eventList.add(new Event(123, "influenza, H5N1-1203", EventType.VACCINE, "123", ""));
      eventList.add(new Event(125, "Novel Influenza-H1N1-09, nasal", EventType.VACCINE, "125", ""));
      eventList.add(new Event(126, "Novel influenza-H1N1-09, preservative-free", EventType.VACCINE, "126", ""));
      eventList.add(new Event(127, "Novel influenza-H1N1-09", EventType.VACCINE, "127", ""));
      eventList.add(new Event(128, "Novel Influenza-H1N1-09, all formulations", EventType.VACCINE, "128", ""));
      eventList.add(new Event(129, "Japanese Encephalitis, unspecified formulation", EventType.VACCINE, "129", ""));
      eventList.add(new Event(130, "DTaP-IPV", EventType.VACCINE, "130", ""));
      eventList.add(new Event(131, "typhus, historical", EventType.VACCINE, "131", ""));
      eventList.add(new Event(132, "DTaP-IPV-HIB-HEP B, historical", EventType.VACCINE, "132", ""));
      eventList.add(new Event(133, "Pneumococcal conjugate PCV 13", EventType.VACCINE, "133", ""));
      eventList.add(new Event(134, "Japanese Encephalitis IM", EventType.VACCINE, "134", ""));
      eventList.add(new Event(135, "Influenza, high dose seasonal", EventType.VACCINE, "135", ""));
      eventList.add(new Event(136, "Meningococcal MCV4O", EventType.VACCINE, "136", ""));
      eventList.add(new Event(137, "HPV, unspecified formulation", EventType.VACCINE, "137", ""));
      eventList.add(new Event(138, "Td (adult)", EventType.VACCINE, "138", ""));
      eventList.add(new Event(139, "Td(adult) unspecified formulation", EventType.VACCINE, "139", ""));
      eventList.add(new Event(140, "Influenza, seasonal, injectable, preservative free", EventType.VACCINE, "140", ""));
      eventList.add(new Event(141, "Influenza, seasonal, injectable", EventType.VACCINE, "141", ""));
      eventList.add(new Event(142, "tetanus toxoid, not adsorbed", EventType.VACCINE, "142", ""));
      eventList.add(new Event(143, "Adenovirus types 4 and 7", EventType.VACCINE, "143", ""));
      eventList.add(new Event(144, "influenza, seasonal, intradermal, preservative free", EventType.VACCINE, "144", ""));
      eventList.add(new Event(145, "RSV-MAb (new)", EventType.VACCINE, "145", ""));
      eventList.add(new Event(146, "DTaP,IPV,Hib,HepB", EventType.VACCINE, "146", ""));
      eventList.add(new Event(147, "meningococcal MCV4, unspecified formulation", EventType.VACCINE, "147", ""));
      eventList.add(new Event(998, "no vaccine administered", EventType.VACCINE, "998", ""));
      eventList.add(new Event(999, "unknown", EventType.VACCINE, "999", ""));

      eventMap = new HashMap<Integer, Event>();
      for (Event event : eventList)
      {
        eventMap.put(event.eventId, event);
      }
    }
  }
}
