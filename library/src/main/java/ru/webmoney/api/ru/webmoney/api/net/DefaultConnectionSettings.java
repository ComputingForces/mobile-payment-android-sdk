package ru.webmoney.api.ru.webmoney.api.net;

class DefaultConnectionSettings implements ConnectionSettings
{
    public final static int HTTP_CONNECT_TIMEOUT = 60000;
    public final static int HTTP_READ_TIMEOUT = 60000;

    private  int connectTimeout = HTTP_CONNECT_TIMEOUT;
    private  int requestTimeout = HTTP_READ_TIMEOUT;

    public int getConnectTimeout()
    {
        return connectTimeout;
    }

    public int getReadTimeout()
    {
        return requestTimeout;
    }
    @Override
    public void setConnectTimeout(int timeout)
    {
        this.connectTimeout = timeout;
    }

    @Override
    public void setReadTimeout(int timeout)
    {
        this.requestTimeout = timeout;
    }
}

