package prj.httpparser.httpparser;

import prj.httpparser.HttpRequest;

public interface HTTPParserListener
{
    public void onHttpRequest(HttpRequest request);

    public void onHttpRequestError();
}
