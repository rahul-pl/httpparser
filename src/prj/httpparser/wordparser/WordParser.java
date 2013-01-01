package prj.httpparser.wordparser;

import prj.httpparser.characterparse.CharListener;
import prj.httpparser.characterparse.CharParser;
import prj.httpparser.characterparse.CharType;
import prj.httpparser.utils.EventSource;

public class WordParser extends EventSource<WordListener> implements CharListener
{
    private CharParser _charParser;
    private StringBuilder _stringBuilder;

    public WordParser(CharParser charParser)
    {
        _charParser = charParser;
        _charParser.addListener(this);
        _stringBuilder = new StringBuilder();
    }

    public void parse(String inputString)
    {
        _charParser.parse(inputString);
    }

    public void reset()
    {
        resetStringBuilder();
        _charParser.reset();
    }

    private void resetStringBuilder()
    {
        _stringBuilder = new StringBuilder();
    }

    @Override
    public void charFound(CharType type, char character, int position)
    {
//        System.out.println(type + " " + character);
        switch (type)
        {
            case PRINTABLE:
                _stringBuilder.append(character);
                break;
            case CARRIAGE_RETURN:
                if (_stringBuilder.length() > 0)
                {
                    fireWordParsed(new Word(Word.WordType.WORD, _stringBuilder.toString()));
                    resetStringBuilder();
                }
                _stringBuilder.append(character);
                break;
            case LINE_FEED:
                if (_stringBuilder.length() == 1 && _stringBuilder.charAt(0) == '\r')
                {
                    fireWordParsed(new Word(Word.WordType.CRLF, null));
                    resetStringBuilder();
                }
                else
                {
                    fireErrorEvent();
                    resetStringBuilder();
                }
                break;
            case SPACE:
            case HORIZONTAL_TAB:
                if (_stringBuilder.length() != 0)
                {
                    fireWordParsed(new Word(Word.WordType.WORD, _stringBuilder.toString()));
                    resetStringBuilder();
                }
                break;
        }
    }

    private void fireWordParsed(Word word)
    {
        for (WordListener l : _listeners)
        {
            l.onWordArrived(word);
        }
    }

    private void fireErrorEvent()
    {
        for (WordListener l : _listeners)
        {
            l.onError();
        }
    }
}
