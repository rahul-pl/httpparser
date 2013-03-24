package prj.httpparser.httpparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prj.httpparser.turnstile.HTTPStateMachine;
import prj.httpparser.turnstile.InitializationException;
import prj.httpparser.turnstile.StateChangeListener;
import prj.httpparser.utils.EventSource;
import prj.httpparser.wordparser.Word;
import prj.httpparser.wordparser.WordListener;
import prj.httpparser.wordparser.WordParser;

public class HTTPRequestParser extends EventSource<HTTPRequestListener> implements WordListener
{
    private WordParser _wordParser;
    private HTTPStateMachine _stateMachine;
    private Logger _logger;
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
                    String[] resourceArray = _lastWord.toString().split("\\?");
                    _rawHTTPRequest.setResourceAddress(resourceArray[0]);
                    if (resourceArray.length == 2)
                    {
                        _rawHTTPRequest.setGETParams(resourceArray[1]);
                    }
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
                    String[] headerSplit = header.split(":");
                    if (headerSplit.length == 2)
                    {
                        String field = headerSplit[0];
                        String value = headerSplit[1];
                        _rawHTTPRequest.addHeader(field, value);
                    }
                    resetStringBuilder();
                }
                else if (newState.equals(HTTPRequestState.FINAL))
                {
                    _rawHTTPRequest.setBody(_wordParser.remaining());
                    resetStringBuilder();
                    _wordParser.reset();
                    fireHttpRequestArrived();
                }
            }
            catch (Exception e)
            {
                _logger.warn("Exception while processing params on state change " +
                        _wordParser.current().replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t"),
                        e);
                cleanup();
            }
        }
    };
    private RawHTTPRequest _rawHTTPRequest;
    private StringBuilder _lastWord;

    public HTTPRequestParser(WordParser wordParser)
    {
        _logger = LoggerFactory.getLogger(HTTPRequestParser.class.getSimpleName());
        _wordParser = wordParser;
        _wordParser.addListener(this);
        _rawHTTPRequest = new RawHTTPRequest();
        _lastWord = new StringBuilder();
        _stateMachine = new HTTPStateMachine();
        _stateMachine.addStateChangeListener(_stateChangeListener);
        resetStringBuilder();
    }

    public void parse(String input)
    {
        _wordParser.parse(input);
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
        catch (InitializationException | IllegalStateException e)
        {
            _logger.warn("Exception in state machine state change " +
                    _wordParser.current().replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t"),
                    e);
            cleanup();
        }
    }

    @Override
    public void onParsingError(String requestString)
    {
        _logger.warn("parsing error while parsing {}",
                requestString.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t"));
        cleanup();
    }

    private void cleanup()
    {
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
        for (HTTPRequestListener l : _listeners)
        {
            l.onHttpRequest(_rawHTTPRequest);
        }
    }

    private void fireHttpRequestError()
    {
        for (HTTPRequestListener l : _listeners)
        {
            l.onHttpRequestError();
        }
    }
}
