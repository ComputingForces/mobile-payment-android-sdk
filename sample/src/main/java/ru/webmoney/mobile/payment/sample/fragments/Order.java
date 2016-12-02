package ru.webmoney.mobile.payment.sample.fragments;

import java.util.Date;


public class Order
{
    public final long number;
    public final float amount;
    public final Date  date;
    public final String description;

    public Order(long number, float amount, Date date, String description)
    {
        this.number = number;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }
}
