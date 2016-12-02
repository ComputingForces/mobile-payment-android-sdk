package ru.webmoney.mobile.payment.sample.tasks;

import android.content.Context;

import ru.webmoney.api.APIClient;
import ru.webmoney.api.APIException;
import ru.webmoney.api.payment.ConfirmRequest;
import ru.webmoney.api.payment.ConfirmResponse;
import ru.webmoney.api.payment.TransactionRequest;
import ru.webmoney.api.payment.TransactionResponse;


public class TransactionConfirmAsyncTask extends X20AsyncTask
{
    public TransactionConfirmAsyncTask(Context context, APIClient apiClient, ITaskCallback onResult, int requestCode, Object tag)
    {
        super(context, apiClient, onResult, requestCode, tag);
    }

    @Override
    protected Object doInBackground(Object... objects)
    {
        ConfirmRequest request = (ConfirmRequest) objects[0];
        try
        {
            ConfirmResponse response = apiClient.getX20Interface().confirmTransaction(request);
            if (handleResult(response))
            {
                return response;
            }
        }
        catch (APIException e)
        {
            this.error = e;
        }
        return null;
    }
}
