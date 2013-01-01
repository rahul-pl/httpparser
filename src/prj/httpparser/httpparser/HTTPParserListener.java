package prj.httpparser.httpparser;

public interface HTTPParserListener
{
    public void onHttpRequest(RawHTTPRequest httpRequest);

    public void onHttpRequestError();
}
