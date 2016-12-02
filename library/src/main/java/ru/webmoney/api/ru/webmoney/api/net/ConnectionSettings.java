package ru.webmoney.api.ru.webmoney.api.net;

public interface ConnectionSettings
{
    /**
     * Sets establish connection timeout for HTTP requests.
     * @param timeout timeout is ms. Value <i>0</i> means infinite timeout.
     */
    public void setConnectTimeout(int timeout);

    /**
     * Sets timeout for read data from established connection for HTTP requests.
     * @param timeout timeout is ms. Value <i>0</i> means infinite timeout.
     */
    public void setReadTimeout(int timeout);

    public int getConnectTimeout();

    public int getReadTimeout();
}

