package ch.lgo.drinks.simple.exceptions;

/**
 * Checked Exceptions should be used to declare for expected, but unpreventable
 * errors that are reasonable to recover from.
 * 
 * Expected but unpreventable: The caller did everything within their power to
 * validate the input parameters, but some condition outside their control has
 * caused the operation to fail. For example, you try reading a file but someone
 * deletes it between the time you check if it exists and the time the read
 * operation begins. By declaring a checked exception, you are telling the
 * caller to anticipate this failure.
 * 
 * Reasonable to recover from: There is no point telling callers to anticipate
 * exceptions that they cannot recover from. If a user attempts to read from an
 * non-existing file, the caller can prompt them for a new filename. On the
 * other hand, if the method fails due to a programming bug (invalid method
 * arguments or buggy method implementation) there is nothing the application
 * can do to fix the problem in mid-execution. The best it can do is log the
 * problem and wait for the developer to fix it at a later time.
 */
public class BusinessException extends Exception {

    /** application specific error code */
    int code;

    /** link documenting the exception */
    String link;

    /** detailed error description for developers */
    String developerMessage;

    public BusinessException(int status, int code, String message,
            String developerMessage, String link) {
        super(message);
        this.code = code;
        this.developerMessage = developerMessage;
        this.link = link;
    }

    public BusinessException() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
