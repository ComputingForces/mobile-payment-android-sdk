package ru.webmoney.api.payment;


public class MerchantRequest
{
    public String wmid;
    public String payeePurse;

    //https://merchant.wmtransfer.com/conf/purses.asp
    public String secretKeyX20;
    public SignType signType;
    //public String sign;
    //public String sha256;
    //public String md5;


    public String lang; // for example "ru-RU"
}

