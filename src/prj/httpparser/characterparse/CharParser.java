package prj.httpparser.characterparse;

import prj.httpparser.utils.EventSource;

public class CharParser extends EventSource<CharListener>
{
    boolean reset;

    public CharParser()
    {
    }

    public void parse(String input)
    {
        reset = false;
        for (int i = 0; i < input.length(); i++)
        {
            if (reset)
            {
                break;
            }
            else
            {
                char character = input.charAt(i);
                CharType charType = getCharType(character);
                fireCharacterFoundListener(charType, character, i);
            }
        }
    }

    public void reset()
    {
        reset = true;
    }

    private CharType getCharType(char c)
    {
        switch (c)
        {
            case '\r' :
                return CharType.CARRIAGE_RETURN;
            case ' ' :
                return CharType.SPACE;
            case '\t' :
                return CharType.HORIZONTAL_TAB;
            case '\n' :
                return CharType.LINE_FEED;
            default :
                return CharType.PRINTABLE;
        }
    }

    private void fireCharacterFoundListener(CharType type, char character, int position)
    {
        for (CharListener l : _listeners)
        {
            l.charFound(type, character, position);
        }
    }
}
