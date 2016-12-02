package ru.webmoney.api;

import ru.webmoney.api.payment.X20Interface;
import ru.webmoney.api.ru.webmoney.api.net.ConnectionSettings;

public interface APIClient
{
    ConnectionSettings getConnectionSettings();
    X20Interface getX20Interface();
}

