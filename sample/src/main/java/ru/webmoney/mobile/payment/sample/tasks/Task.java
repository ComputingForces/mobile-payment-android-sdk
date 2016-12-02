package ru.webmoney.mobile.payment.sample.tasks;

import android.os.AsyncTask;

import ru.webmoney.api.APIException;


abstract class Task extends AsyncTask<Object, Void, Object>
{
    protected final ITaskCallback onResult;
    protected final int           requestCode;
    protected final Object        tag;
    protected APIException        error;

    public Task(ITaskCallback onResult, int requestCode, Object tag)
    {
        this.onResult = onResult;
        this.tag       = tag;
        this.requestCode = requestCode;
    }

    @Override
    protected void onPreExecute()
    {
        if (null != onResult)
            onResult.onPreExecute(requestCode, tag);
    }

    @Override
    protected void onPostExecute(Object obj)
    {
        if (null != onResult)
        {

            if (null != obj)
            {
                onResult.onPostExecute(requestCode, true, tag);
                onResult.onSuccess(requestCode, obj, tag);
            }
            else
            {
                onResult.onPostExecute(requestCode, false, tag);
                onResult.onError(requestCode, error, tag);
            }
        }
    }


}
