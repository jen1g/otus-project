package com.otushomework.deliveryservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.Set;

@Entity
public class TimeSlot {

    @Id
    private String timeslotId;
    private String timeslot;
    private int maxValueBooked;
    private int booked;

    @OneToMany(mappedBy = "timeslot")
    @JsonIgnore
    private Set<Delivery> deliveries;

    public Set<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(Set<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public String getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(String timeslotId) {
        this.timeslotId = timeslotId;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public int getMaxValueBooked() {
        return maxValueBooked;
    }

    public void setMaxValueBooked(int maxValueBooked) {
        this.maxValueBooked = maxValueBooked;
    }

    public int getBooked() {
        return booked;
    }

    public void setBooked(int booked) {
        this.booked = booked;
    }
}
