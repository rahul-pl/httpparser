package prj.httpparser.turnstile;

import prj.httpparser.httpparser.HTTPRequestState;
import prj.httpparser.wordparser.Word;

public class HTTPStateMachine extends StateMachine<HTTPRequestState, Word.WordType>
{
    public HTTPStateMachine()
    {
        super(HTTPRequestState.ERROR);
        initializeStateMachine();
    }

    private void initializeStateMachine()
    {
        start(HTTPRequestState.START);

        addTransition(HTTPRequestState.START, Word.WordType.CRLF);
        addTransition(HTTPRequestState.START, Word.WordType.WORD, HTTPRequestState.METHOD_NAME_PARSED);

        addTransition(HTTPRequestState.METHOD_NAME_PARSED, Word.WordType.WORD, HTTPRequestState.RESOURCE_LOCATION_PARSED);

        addTransition(HTTPRequestState.RESOURCE_LOCATION_PARSED, Word.WordType.WORD, HTTPRequestState.HTTP_VERSION_NAME_PARSED);

        addTransition(HTTPRequestState.HTTP_VERSION_NAME_PARSED, Word.WordType.CRLF, HTTPRequestState.REQUEST_LINE_COMPLETE);

        addTransition(HTTPRequestState.REQUEST_LINE_COMPLETE, Word.WordType.WORD, HTTPRequestState.HEADER_FIELD_NAME_PARSED);
        addTransition(HTTPRequestState.REQUEST_LINE_COMPLETE, Word.WordType.CRLF, HTTPRequestState.FINAL);

        addTransition(HTTPRequestState.HEADER_FIELD_NAME_PARSED, Word.WordType.WORD, HTTPRequestState.HEADER_FIELD_VALUE_PARSING);
        addTransition(HTTPRequestState.HEADER_FIELD_NAME_PARSED, Word.WordType.CRLF, HTTPRequestState.HEADER_FIELD_VALUE_PARSED);

        addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSING, Word.WordType.WORD);
        addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSING, Word.WordType.CRLF, HTTPRequestState.HEADER_FIELD_VALUE_PARSED);

        addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSED, Word.WordType.WORD, HTTPRequestState.HEADER_FIELD_NAME_PARSED);
        addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSED, Word.WordType.CRLF, HTTPRequestState.FINAL);
    }
}
