package com.example.cs4520teamproject.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Group implements Serializable {
    private Date createAt;
    private String createBy;
    private ArrayList<String> members;
    private String id;
    private String date;
    private double  latitude;
    private double longitude;
    private boolean hasFull;
    private String destination;
    private String note;
    private int totalNumberOfMembers;
    private int curNumberOfMembers;
    private int averageCost;
    private Date groupDate;

    @Override
    public String toString() {
        return "Group{" +
                "createAt=" + createAt +
                ", createBy='" + createBy + '\'' +
                ", members=" + members +
                ", id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", hasFull=" + hasFull +
                ", destination='" + destination + '\'' +
                ", note='" + note + '\'' +
                ", totalNumberOfMembers=" + totalNumberOfMembers +
                ", curNumberOfMembers=" + curNumberOfMembers +
                ", averageCost=" + averageCost +
                ", groupDate=" + groupDate +
                '}';
    }

    public Group() {
    }

    public Date getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(Date groupDate) {
        this.groupDate = groupDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
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

    public boolean isHasFull() {
        return hasFull;
    }

    public void setHasFull(boolean hasFull) {
        this.hasFull = hasFull;
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

    public int getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(int averageCost) {
        this.averageCost = averageCost;
    }
}
