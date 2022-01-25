package com.example.gargianimalcare;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class customerComplain_model {
    private String complainId, subject, description, location, status, employeeName, Empl_phone_num ;
    private long time,timeOfsolution;

    public customerComplain_model(String id, String sub, String desc, long tim, String loc, String stat, String Employee, String EmplePh, long  timeOfS)
    {
        complainId=id;
        subject=sub;
        description=desc;
        time=tim;
        location=loc;
        status=stat;
        employeeName=Employee;
        Empl_phone_num=EmplePh;
    }

    public String getComplainId() {
        return complainId;
    }

    public void setComplainId(String complainId) {
        this.complainId = complainId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {

        Date date = new Date ();
        date.setTime((long)time*1000);

        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm");
        return dateFormat.format(date);

    }

    public void setTime(long time) {

        this.time = time;
    }

    public String  getTimeOfsolution() {
        Date date = new Date ();
        date.setTime((long)timeOfsolution*1000);

        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm");
        return dateFormat.format(date);


    }

    public void setTimeOfsolution(long timeOfsolution) {
        this.timeOfsolution = timeOfsolution;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmpl_phone_num() {
        return Empl_phone_num;
    }

    public void setEmpl_phone_num(String empl_phone_num) {
        Empl_phone_num = empl_phone_num;
    }
}
