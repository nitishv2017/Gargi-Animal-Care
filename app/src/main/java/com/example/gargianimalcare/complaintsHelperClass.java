package com.example.gargianimalcare;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class complaintsHelperClass implements Parcelable {
    String complainID, userID, customerName, phoneNumber,
            address, subject, description, status, employeeID,
            employeeName, descriptionofSolution,employeePhone;
    double latitude, longitude;
    long timeOfComplain, timeofCompletion;

    public complaintsHelperClass()
    {

    }
    public complaintsHelperClass(String address, String complainID, String customerName, String description, String descriptionofSolution, String employeeID, String employeeName,String employeePh, Double latitude, Double longitude, String phoneNumber, String status, String subject, Long timeOfComplain, Long timeofCompletion, String userID) {
        super();
        this.address = address;
        this.complainID = complainID;
        this.customerName = customerName;
        this.description = description;
        this.descriptionofSolution = descriptionofSolution;
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.employeePhone=employeePh;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.subject = subject;
        this.timeOfComplain = timeOfComplain;
        this.timeofCompletion = timeofCompletion;
        this.userID = userID;
    }

    protected complaintsHelperClass(Parcel in) {
        complainID = in.readString();
        userID = in.readString();
        customerName = in.readString();
        phoneNumber = in.readString();
        address = in.readString();
        subject = in.readString();
        description = in.readString();
        status = in.readString();
        employeeID = in.readString();
        employeeName = in.readString();
        descriptionofSolution = in.readString();
        employeePhone = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        timeOfComplain = in.readLong();
        timeofCompletion = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(complainID);
        dest.writeString(userID);
        dest.writeString(customerName);
        dest.writeString(phoneNumber);
        dest.writeString(address);
        dest.writeString(subject);
        dest.writeString(description);
        dest.writeString(status);
        dest.writeString(employeeID);
        dest.writeString(employeeName);
        dest.writeString(descriptionofSolution);
        dest.writeString(employeePhone);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(timeOfComplain);
        dest.writeLong(timeofCompletion);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<complaintsHelperClass> CREATOR = new Creator<complaintsHelperClass>() {
        @Override
        public complaintsHelperClass createFromParcel(Parcel in) {
            return new complaintsHelperClass(in);
        }

        @Override
        public complaintsHelperClass[] newArray(int size) {
            return new complaintsHelperClass[size];
        }
    };

    public static Creator<complaintsHelperClass> getCREATOR() {
        return CREATOR;
    }

    public String getEmployeePhone() {
        return employeePhone;
    }

    public void setEmployeePhone(String employeePhone) {
        this.employeePhone = employeePhone;
    }

    public String getComplainID() {
        return complainID;
    }

    public void setComplainID(String complainID) {
        this.complainID = complainID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDescriptionofSolution() {
        return descriptionofSolution;
    }

    public void setDescriptionofSolution(String descriptionofSolution) {
        this.descriptionofSolution = descriptionofSolution;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimeOfComplain() {
        return timeOfComplain;
    }

    public void setTimeOfComplain(long timeOfComplain) {
        this.timeOfComplain = timeOfComplain;
    }

    public long getTimeofCompletion() {
        return timeofCompletion;
    }

    public void setTimeofCompletion(long timeofCompletion) {
        this.timeofCompletion = timeofCompletion;
    }
}
