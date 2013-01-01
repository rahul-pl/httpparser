package prj.httpparser.httpparser;

import java.util.HashMap;
import java.util.Map;

public class RawHTTPRequest
{
    private RequestType requestType;
    private String resourceAddress;
    private String httpVersion;
    private Map<String, String> headers;

    public RawHTTPRequest()
    {
        headers = new HashMap<>();
    }

    public RequestType getRequestType()
    {
        return requestType;
    }

    public void setRequestType(RequestType requestType)
    {
        this.requestType = requestType;
    }

    public String getResourceAddress()
    {
        return resourceAddress;
    }

    public void setResourceAddress(String resourceAddress)
    {
        this.resourceAddress = resourceAddress;
    }

    public String getHttpVersion()
    {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion)
    {
        this.httpVersion = httpVersion;
    }

    public void addHeader(String field, String value)
    {
        headers.put(field, value);
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }
}
