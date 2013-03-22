package prj.httpparser.wordparser;

public interface WordListener
{
    public void onWordArrived(Word word);

    public void onParsingError(String requestString);
}
