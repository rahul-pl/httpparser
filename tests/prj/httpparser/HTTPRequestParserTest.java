package prj.httpparser;

import org.junit.Before;
import org.junit.Test;
import prj.httpparser.characterparse.CharParser;
import prj.httpparser.httpparser.HTTPRequestListener;
import prj.httpparser.httpparser.HTTPRequestParser;
import prj.httpparser.httpparser.RawHTTPRequest;
import prj.httpparser.httpparser.RequestType;
import prj.httpparser.wordparser.WordParser;

import static org.mockito.Mockito.*;

public class HTTPRequestParserTest
{
    private HTTPRequestParser httpParser;
    private HTTPRequestListener mockRequestListener;

    @Before
    public void setUp() throws Exception
    {
        httpParser = new HTTPRequestParser(new WordParser(new CharParser()));
        mockRequestListener = mock(HTTPRequestListener.class);
        httpParser.addListener(mockRequestListener);
    }

    @Test
    public void parse_simpleGetRequest() throws Exception
    {
        httpParser.parse("GET / HTTP/1.1\r\n\r\n");
        verify(mockRequestListener, times(1))
                .onHttpRequest(eq(new RawHTTPRequest() {
                    {
                        setRequestType(RequestType.GET);
                        setResourceAddress("/");
                        setHttpVersion("HTTP/1.1");
                    }
                }));
    }

    @Test
    public void parse_invalidRequest()
    {
        httpParser.parse("GET /\r\n");
        verify(mockRequestListener).onHttpRequestError();
    }
}
