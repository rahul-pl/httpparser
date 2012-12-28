package prj.httpparser.httpparser;

public enum HTTPRequestState
{
    START,
    METHOD_NAME_PARSED,
    RESOURCE_LOCATION_PARSED,
    HTTP_VERSION_NAME_PARSED,
    REQUEST_LINE_COMPLETE,
    HEADER_FIELD_NAME_PARSED,
    HEADER_FIELD_VALUE_PARSED,
    HEADER_FIELD_VALUE_PARSING,
    FINAL,
    ERROR
}