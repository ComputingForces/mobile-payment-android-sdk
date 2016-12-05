package ru.webmoney.api.payment;

import android.content.Context;

import ru.webmoney.api.APIException;

//http://wiki.webmoney.ru/projects/webmoney/wiki/%D0%98%D0%BD%D1%82%D0%B5%D1%80%D1%84%D0%B5%D0%B9%D1%81_X20
public interface X20Interface
{
    ConfirmResponse confirmTransaction(ConfirmRequest request) throws APIException;
    TransactionResponse createTransaction(TransactionRequest request) throws APIException;
    String getErrorDescription(Context context, int retval) throws APIException;
}
