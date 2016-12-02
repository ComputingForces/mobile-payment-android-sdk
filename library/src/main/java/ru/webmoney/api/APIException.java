package ru.webmoney.api;


public class APIException extends Exception
{
    private Integer status;
    private Integer serverResult;
    private String serverMessage;

    public APIException()
    {
    }

    public APIException(String message)
    {
        this(null, message);
    }

    public APIException(Integer status, String message)
    {
        super(message);
        this.status = status;
    }

    public APIException(Throwable throwable)
    {
        super(throwable);
    }

    public APIException(String message, Throwable t)
    {
        this(null, message, t);
    }

    public APIException(Integer status, String message, Throwable t)
    {
        super(message, t);
        this.status = status;
    }

    /**
     * HTTP status returned from API call or null if call hasn't been executed
     * yet.
     */
    public Integer getStatus() {
        return status;
    }

    public Integer getServerResult()
    {
        return serverResult;
    }

    public void setServerResult(Integer serverResult)
    {
        this.serverResult = serverResult;
    }

    public String getServerMessage()
    {
        return serverMessage;
    }

    public APIException setServerMessage(String serverMessage)
    {
        this.serverMessage = serverMessage;
        return this;
    }

    @Override
    public String getMessage()
    {
        if (null != serverMessage && 0 != serverMessage.length())
            return serverMessage;
        return super.getMessage();
    }
}