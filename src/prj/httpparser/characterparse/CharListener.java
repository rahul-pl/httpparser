package prj.httpparser.characterparse;

public interface CharListener
{
    public void charFound(CharType type, char character, int position);
}
