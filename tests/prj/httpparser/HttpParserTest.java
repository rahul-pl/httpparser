package prj.httpparser;

import org.junit.Before;
import org.junit.Test;
import prj.httpparser.httpparser.HttpParser;

public class HttpParserTest
{
    private HttpParser httpParser;

    @Before
    public void setUp() throws Exception
    {
        httpParser = new HttpParser();
    }

    @Test
    public void testParse() throws Exception
    {
        String requestString = "GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n";
        HttpRequest request = httpParser.parse(requestString);
    }
}
