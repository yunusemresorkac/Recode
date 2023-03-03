package com.yeslabapps.friendb.model;

public class Order {

    private long orderTime;
    private String orderId;
    private String customerId;
    private int status;
    private String orderKey;
    private String orderTitle;
    private int price;
    private String orderProductId;

    public Order(){

    }


    public Order(long orderTime, String orderId, String customerId, int status, String orderKey, String orderTitle, int price,String orderProductId) {
        this.orderTime = orderTime;
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
        this.orderKey = orderKey;
        this.orderTitle = orderTitle;
        this.price = price;
        this.orderProductId = orderProductId;
    }

    public String getOrderProductId() {
        return orderProductId;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getStatus() {
        return status;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public int getPrice() {
        return price;
    }
}
