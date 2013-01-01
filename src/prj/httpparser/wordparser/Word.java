package prj.httpparser.wordparser;

public class Word
{
    public enum WordType
    {
        WORD, CRLF
    }
    private WordType _type;
    private String _value;

    public Word(WordType type, String value)
    {
        _type = type;
        _value = value;
    }

    public WordType getType()
    {
        return _type;
    }

    public String getValue()
    {
        return _value;
    }
}
