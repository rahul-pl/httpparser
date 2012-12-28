package prj.httpparser.characterparser;

import org.junit.Before;
import org.junit.Test;
import prj.httpparser.characterparse.CharListener;
import prj.httpparser.characterparse.CharParser;
import prj.httpparser.characterparse.CharType;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CharParserTest
{
    private CharParser charParser;
    private CharListener mockCharListener;

    @Before
    public void setUp()
    {
        charParser = new CharParser();
        mockCharListener = mock(CharListener.class);
        charParser.addListener(mockCharListener);
    }

    @Test
    public void parse_shouldFireOneForEachCharacter()
    {
        String inputString = "\r\nabcdef\n";
        charParser.parse(inputString);
        verify(mockCharListener, times(inputString.length())).charFound((CharType) any(), anyChar(), anyInt());
    }

    @Test
    public void parse_shouldIdentifyCarriageReturn()
    {
        String inputString = "\r";
        charParser.parse(inputString);
        verify(mockCharListener).charFound(CharType.CARRIAGE_RETURN, '\r', 0);
    }
}
