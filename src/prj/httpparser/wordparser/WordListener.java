package prj.httpparser.wordparser;

public interface WordListener
{
    public void onWordArrived(WordType type, String word);

    public void onError();
}
