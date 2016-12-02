package ru.webmoney.api.payment;

import android.content.Context;

import ru.webmoney.api.APIException;

//http://wiki.webmoney.ru/projects/webmoney/wiki/Интерфейс_X20
public interface X20Interface
{
    ConfirmResponse confirmTransaction(ConfirmRequest request) throws APIException;
    TransactionResponse createTransaction(TransactionRequest request) throws APIException;
    String getErrorDescription(Context context, int retval) throws APIException;
}
