package KChat.Result;

public class ActionResult<T> {
    private String message;
    private int code;
    private T data;

    public ActionResult(String message,int code)
    {
        this.message = message;
        this.code = code;
    }

    public ActionResult(String message,int code,T data)
    {
        this(message,code);
        this.data = data;
    }

    public String getMessage(){return message;}
    public int getCode(){return code;}
    public T getData(){return data;}
    public boolean getSucceeded(){
        return code>=200&&code<300;
    }

    public static final ActionResult DefinedErrorResult = new ActionResult("Error",600);
    public static final ActionResult ServerError = new ActionResult("Internal Server Error",500);

}
