package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

/**
 * @author Stefanie
 * POJO ("Plain Old Java Object") to contain data used in RoadsideAssistance ListView
 */
public class RoadsideAssistanceEntry {
    String text_institution;
    Integer resId_institution;
    Integer resId_call;

    public RoadsideAssistanceEntry(String text_institution, Integer resId_institution, Integer resId_call) {
        this.text_institution = text_institution;
        this.resId_institution = resId_institution;
        this.resId_call = resId_call;
    }

    public String getText_institution() {
        return text_institution;
    }

    public void setText_institution(String text_institution) {
        this.text_institution = text_institution;
    }

    public Integer getResId_institution() {
        return resId_institution;
    }

    public void setResId_institution(Integer resId_institution) {
        this.resId_institution = resId_institution;
    }

    public Integer getResId_call() {
        return resId_call;
    }

    public void setResId_call(Integer resId_call) {
        this.resId_call = resId_call;
    }
}
