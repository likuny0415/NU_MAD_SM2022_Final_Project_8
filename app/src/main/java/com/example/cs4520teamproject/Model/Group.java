package com.example.cs4520teamproject.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Group implements Serializable {
    private Date createAt;
    private String createAtDate;
    private String createAtTime;
    private String createBy;
    private ArrayList<String> members;
    private String date;
    private double  latitude;
    private double longitude;
    private boolean isFull;
    private String destination;
    private String note;
    private int totalNumberOfMembers;
    private int curNumberOfMembers;

    @Override
    public String toString() {
        return "Group{" +
                "createAt=" + createAt +
                ", createAtDate='" + createAtDate + '\'' +
                ", createAtTime='" + createAtTime + '\'' +
                ", createBy='" + createBy + '\'' +
                ", members=" + members +
                ", date='" + date + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isFull=" + isFull +
                ", destination='" + destination + '\'' +
                ", note='" + note + '\'' +
                ", totalNumberOfMembers=" + totalNumberOfMembers +
                ", curNumberOfMembers=" + curNumberOfMembers +
                '}';
    }

    public Group() {
    }


    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getCreateAtDate() {
        return createAtDate;
    }

    public void setCreateAtDate(String createAtDate) {
        this.createAtDate = createAtDate;
    }

    public String getCreateAtTime() {
        return createAtTime;
    }

    public void setCreateAtTime(String createAtTime) {
        this.createAtTime = createAtTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTotalNumberOfMembers() {
        return totalNumberOfMembers;
    }

    public void setTotalNumberOfMembers(int totalNumberOfMembers) {
        this.totalNumberOfMembers = totalNumberOfMembers;
    }

    public int getCurNumberOfMembers() {
        return curNumberOfMembers;
    }

    public void setCurNumberOfMembers(int curNumberOfMembers) {
        this.curNumberOfMembers = curNumberOfMembers;
    }




}
