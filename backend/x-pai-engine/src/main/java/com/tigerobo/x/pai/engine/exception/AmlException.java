package com.tigerobo.x.pai.engine.exception;

public class AmlException extends RuntimeException {

    private static final long serialVersionUID = -2883219783517631145L;
    public AmlException(){
        super();
    }

    public AmlException(String s){
        super(s);
    }
    public AmlException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmlException(Throwable cause) {
        super(cause);
    }


    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new AmlException(String.valueOf(errorMessage));
        }
    }

}
