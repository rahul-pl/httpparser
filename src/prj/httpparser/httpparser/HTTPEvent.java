package prj.httpparser.httpparser;

public enum HTTPEvent
{
    CRLF,
    VERB,
    RESOURCE_LOCATION,
    HTTP_VERSION,
    WORD_WITH_SEMICOLON,
    WORD,
    UNEXPECTED_TOKEN
}
