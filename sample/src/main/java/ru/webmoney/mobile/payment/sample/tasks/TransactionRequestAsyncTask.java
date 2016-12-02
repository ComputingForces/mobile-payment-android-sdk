package ru.webmoney.mobile.payment.sample.tasks;

import android.content.Context;

import ru.webmoney.api.APIClient;
import ru.webmoney.api.APIException;
import ru.webmoney.api.payment.TransactionRequest;
import ru.webmoney.api.payment.TransactionResponse;


public class TransactionRequestAsyncTask extends X20AsyncTask
{
    public TransactionRequestAsyncTask(Context context, APIClient apiClient, ITaskCallback onResult, int requestCode, Object tag)
    {
        super(context, apiClient, onResult, requestCode, tag);
    }

    @Override
    protected Object doInBackground(Object... objects)
    {
        TransactionRequest request = (TransactionRequest) objects[0];
        try
        {
            TransactionResponse response = apiClient.getX20Interface().createTransaction(request);
            if (request.isEmulated && null != response && 540 == response.retval)
            {
                return response;
            }
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




