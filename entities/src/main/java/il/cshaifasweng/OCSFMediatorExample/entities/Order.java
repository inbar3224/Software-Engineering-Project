package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")

public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String address;
    private String card;
    private String credit;
    private String cvv;
    private String dateTime;
    private double finalPrice;
    private String formOfSupplying;
    private String monthAndYear;
    private int orderID;
    private String receiverName;
    private String receiverPhone;
    private double refund;
    // status: 1 = active, 2 = supplied already, 3 = canceled
    private int status;
    private String storeName;
    private int userID;

    private String flowers;

    public Order(int userID, String flowers, String card, String formOfSupplying,
                 String storeName, String address, String receiverName, String receiverPhone,
                 String dateTime, double finalPrice, String credit, String cvv,
                 String monthAndYear, int status, int orderID, double refund) {
        super();
        this.address = address;
        this.card = card;
        this.credit = credit;
        this.cvv = cvv;
        this.dateTime = dateTime;
        this.finalPrice = finalPrice;
        this.formOfSupplying = formOfSupplying;
        this.monthAndYear = monthAndYear;
        this.orderID = orderID;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.refund = refund;
        this.status = status;
        this.storeName = storeName;
        this.userID = userID;
        this.flowers = flowers;
    }

    public Order() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getFormOfSupplying() {
        return formOfSupplying;
    }

    public void setFormOfSupplying(String formOfSupplying) {
        this.formOfSupplying = formOfSupplying;
    }

    public String getMonthAndYear() {
        return monthAndYear;
    }

    public void setMonthAndYear(String monthAndYear) {
        this.monthAndYear = monthAndYear;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFlowers() {
        return flowers;
    }

    public void setFlowers(String flowers) {
        this.flowers = flowers;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getRefund() {
        return refund;
    }

    public void setRefund(double refund) {
        this.refund = refund;
    }
}
