package prj.httpparser.httpparser;

import prj.httpparser.utils.EventSource;
import prj.httpparser.wordparser.Word;
import prj.httpparser.wordparser.WordListener;
import prj.httpparser.wordparser.WordParser;
import prj.turnstile.InitializationException;
import prj.turnstile.StateChangeListener;
import prj.turnstile.StateMachine;

public class HTTPParser extends EventSource<HTTPParserListener> implements WordListener
{
    private WordParser _wordParser;
    private StateMachine<HTTPRequestState, Word.WordType> _stateMachine;
    private StateChangeListener<HTTPRequestState, Word.WordType> _stateChangeListener = new StateChangeListener<HTTPRequestState, Word.WordType>()
    {
        @Override
        public void onChange(HTTPRequestState oldState, Word.WordType cause, HTTPRequestState newState)
        {
            try
            {
                if (newState.equals(HTTPRequestState.METHOD_NAME_PARSED))
                {
                    _rawHTTPRequest.setRequestType(RequestType.valueOf(_lastWord.toString().toUpperCase()));
                    resetStringBuilder();
                }
                else if (newState.equals(HTTPRequestState.RESOURCE_LOCATION_PARSED))
                {
                    _rawHTTPRequest.setResourceAddress(_lastWord.toString());
                    resetStringBuilder();
                }
                else if (newState.equals(HTTPRequestState.HTTP_VERSION_NAME_PARSED))
                {
                    _rawHTTPRequest.setHttpVersion(_lastWord.toString());
                    resetStringBuilder();
                }
                else if (newState.equals(HTTPRequestState.HEADER_FIELD_VALUE_PARSED))
                {
                    String header = _lastWord.toString();
                    String field = header.split(":")[0];
                    String value = header.split(":")[1];
                    _rawHTTPRequest.addHeader(field, value);
                    resetStringBuilder();
                }
                else if (newState.equals(HTTPRequestState.FINAL))
                {
                    fireHttpRequestArrived();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                onError();
            }
        }
    };
    private RawHTTPRequest _rawHTTPRequest;
    private StringBuilder _lastWord;

    public HTTPParser(WordParser wordParser)
    {
        _wordParser = wordParser;
        _wordParser.addListener(this);
        _rawHTTPRequest = new RawHTTPRequest();
        _lastWord = new StringBuilder();
        _stateMachine = new StateMachine<>(HTTPRequestState.ERROR);
        initializeStateMachine();
        resetStringBuilder();
    }

    public void parse(String input)
    {
        if (_stateMachine.getCurrentState().equals(HTTPRequestState.START))
        {
            _stateMachine.start(HTTPRequestState.START);
            _wordParser.parse(input);
        }
        else
        {
            System.out.println("another parse request is already in progress");
        }
    }

    @Override
    public void onWordArrived(Word word)
    {
        try
        {
            if (word.getType().equals(Word.WordType.WORD))
            {
                _lastWord.append(word.getValue());
            }
            _stateMachine.process(word.getType());
        }
        catch (InitializationException e)
        {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onError()
    {
        System.out.println("Error occurred");
        resetStringBuilder();
        _wordParser.reset();
        fireHttpRequestError();
    }

    private void resetStringBuilder()
    {
        _lastWord = new StringBuilder();
    }

    private void fireHttpRequestArrived()
    {
        _stateMachine.start(HTTPRequestState.START);
        for (HTTPParserListener l : _listeners)
        {
            l.onHttpRequest(_rawHTTPRequest);
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

        _stateMachine.addTransition(HTTPRequestState.START, Word.WordType.CRLF);
        _stateMachine.addTransition(HTTPRequestState.START, Word.WordType.WORD, HTTPRequestState.METHOD_NAME_PARSED);

        _stateMachine.addTransition(HTTPRequestState.METHOD_NAME_PARSED, Word.WordType.WORD, HTTPRequestState.RESOURCE_LOCATION_PARSED);

        _stateMachine.addTransition(HTTPRequestState.RESOURCE_LOCATION_PARSED, Word.WordType.WORD, HTTPRequestState.HTTP_VERSION_NAME_PARSED);

        _stateMachine.addTransition(HTTPRequestState.HTTP_VERSION_NAME_PARSED, Word.WordType.CRLF, HTTPRequestState.REQUEST_LINE_COMPLETE);

        _stateMachine.addTransition(HTTPRequestState.REQUEST_LINE_COMPLETE, Word.WordType.WORD, HTTPRequestState.HEADER_FIELD_NAME_PARSED);

        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_NAME_PARSED, Word.WordType.WORD, HTTPRequestState.HEADER_FIELD_VALUE_PARSING);

        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSING, Word.WordType.WORD);
        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSING, Word.WordType.CRLF, HTTPRequestState.HEADER_FIELD_VALUE_PARSED);

        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSED, Word.WordType.WORD, HTTPRequestState.HEADER_FIELD_NAME_PARSED);
        _stateMachine.addTransition(HTTPRequestState.HEADER_FIELD_VALUE_PARSED, Word.WordType.CRLF, HTTPRequestState.FINAL);

        _stateMachine.addStateChangeListener(_stateChangeListener);
    }
}
