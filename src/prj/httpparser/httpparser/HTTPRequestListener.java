package prj.httpparser.httpparser;

public interface HTTPRequestListener
{
    public void onHttpRequest(RawHTTPRequest httpRequest);

    public void onHttpRequestError();
}
