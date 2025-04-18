package main;

public class SpotipyException extends RuntimeException {
    
    private int httpStatus;

    public SpotipyException(int httpStatus, String msg) {

        super(httpStatus + ": " + msg);

    }

    public int getHttpStatus() {

        return httpStatus;

    }

}
