package prj.httpparser.httpparser;

import prj.httpparser.HttpRequest;
import prj.httpparser.utils.EventSource;
import prj.httpparser.wordparser.WordListener;
import prj.httpparser.wordparser.WordParser;
import prj.httpparser.wordparser.WordType;
import prj.turnstile.InitializationException;
import prj.turnstile.StateChangeListener;
import prj.turnstile.StateMachine;

public class HTTPParser extends EventSource<HTTPParserListener> implements WordListener
{
    private WordParser _wordParser;
    private StateMachine<HTTPRequestState, WordType> _stateMachine;
    private StateChangeListener<HTTPRequestState, WordType> _stateChangeListener = new StateChangeListener<HTTPRequestState, WordType>()
    {
        @Override
        public void onChange(HTTPRequestState oldState, WordType cause, HTTPRequestState newState)
        {
            System.out.println("state changes from \'" + oldState + "\' to \'" + newState + "\' due to \'" + cause + "\'");
        }
    };

    public HTTPParser(WordParser wordParser)
    {
        _wordParser = wordParser;
        _wordParser.addListener(this);
        _stateMachine = new StateMachine<>(HTTPRequestState.ERROR);
        initializeStateMachine();
    }

    public void parse(String input)
    {
        _wordParser.parse(input);
    }

    @Override
    public void onWordArrived(WordType type, String word)
    {
        try
        {
            _stateMachine.process(type);
        }
        catch (InitializationException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onError()
    {
        fireHttpRequestError();
    }

    private void fireHttpRequestArrived(HttpRequest request)
    {
        for (HTTPParserListener l : _listeners)
        {
            l.onHttpRequest(request);
        }
    }

    private void fireHttpRequestError()
    {
        for (HTTPParserListener l : _listeners)
        {
            l.onHttpRequestError();
        }
    }

    private void initializeStateMachine()
    {
        _stateMachine.start(HTTPRequestState.START);

        _stateMachine.addTransition(HTTPRequestState.START, WordType.CRLF);
        _stateMachine.addTransition(HTTPRequestState.START, WordType.WORD, HTTPRequestState.METHOD_NAME_PARSED);

        _stateMachine.addTransition(HTTPRequestState.METHOD_NAME_PARSED, WordType.WORD, HTTPRequestState.RESOURCE_LOCATION_PARSED);

        _stateMachine.addTransition(HTTPRequestState.RESOURCE_LOCATION_PARSED, WordType.WORD, HTTPRequestState.HTTP_VERSION_NAME_PARSED);

        _stateMachine.addTransition(HTTPRequestState.HTTP_VERSION_NAME_PARSED, WordType.CRLF, HTTPRequestState.REQUEST_LINE_COMPLETE);

        _stateMachine.addTransition(HTTPRequestState.REQUEST_LINE_COMPLETE, WordType.WORD, HTTPRequestState.HEADER_FIELD_NAME_PARSED);

        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_NAME_PARSED, WordType.WORD, HTTPRequestState.HEADER_FIELD_VALUE_PARSING);

        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSING, WordType.WORD);
        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSING, WordType.CRLF, HTTPRequestState.HEADER_FIELD_VALUE_PARSED);

        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSED, WordType.WORD, HTTPRequestState.HEADER_FIELD_NAME_PARSED);
        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSED, WordType.CRLF, HTTPRequestState.FINAL);

        _stateMachine.addStateChangeListener(_stateChangeListener);
    }
}
