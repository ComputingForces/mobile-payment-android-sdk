package ru.webmoney.mobile.payment.sample.tasks;


import ru.webmoney.api.APIException;

public interface ITaskCallback
{
    void onPreExecute(int requestCode, Object tag);
    void onPostExecute(int requestCode, boolean bSuccess, Object tag);
    void onSuccess(int requestCode, Object data, Object tag);
    void onError(int requestCode, APIException error, Object tag);
}



