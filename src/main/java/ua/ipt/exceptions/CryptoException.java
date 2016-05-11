package ua.ipt.exceptions;

/**
 * Created by Roman Horilyi on 21.04.2016.
 */
public class CryptoException extends Exception {

    public CryptoException() {
    }

    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
