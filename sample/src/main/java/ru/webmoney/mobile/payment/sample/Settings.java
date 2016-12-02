package ru.webmoney.mobile.payment.sample;


public interface Settings
{
    // WMID аккаунта, принимающего оплату
    String WMID ="000000000000";

    // Номер кошелька, на который будет производиться приём оплаты
    String PURSE ="R000000000000";

    // Секретный ключ, получаемый при настройке кошелька для приёма платежей через X20
    // (настройка производится по адресу https://merchant.wmtransfer.com/conf/purses.asp)
    // Чтобы не компроментировать Secret Key лучше задать в настройке кошелька и использовать
    // отдельный ключ Secret Key X20
    String SecretKeyX20 ="secret_key X 20";

    // Предпочитаемый язык диагностических сообщений, получаемых от X20
    String language = "ru-RU";

    // Режим эмуляции запросов на сервере (0 - отключем; 1 - включен)
    //Ошибка 540 возвращаемая, если данный флаг задан, означает успех!
    boolean emulated = false;
    //boolean emulated = true;

    // Данный параметр обязателен к указанию только для агрегаторов (сервисы посредники осуществляющие прием платежей в пользу третьих лиц).
    // В данном поле агрегаторы обязаны передавать регистрационный номер магазина в каталоге
    // Меагасток http://www.megastock.ru/ в пользу которого осуществляется прием данного платежа
    int shopId   = 0; // not defined

}
