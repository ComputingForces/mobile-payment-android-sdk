package ru.webmoney.api;

import ru.webmoney.api.payment.X20Interface;
import ru.webmoney.api.payment.X20InterfaceFactory;
import ru.webmoney.api.ru.webmoney.api.net.ConnectionSettings;
import ru.webmoney.api.ru.webmoney.api.net.ConnectionSettingsFactory;

public class DefaultAPIClient implements APIClient
{
    private ConnectionSettings connectionSettings = ConnectionSettingsFactory.createInstance();
    private X20Interface X20Implementation;

    @Override
    public ConnectionSettings getConnectionSettings()
    {
        return connectionSettings;
    }

    @Override
    public X20Interface getX20Interface()
    {
        if (null == X20Implementation)
        {
            X20Implementation = X20InterfaceFactory.createInstance(getConnectionSettings());
        }
        return X20Implementation;
    }
}


