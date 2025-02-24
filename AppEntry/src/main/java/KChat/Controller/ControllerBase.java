package KChat.Controller;

import KChat.Result.ActionResult;
import KChat.Service.RedisCache;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class ControllerBase {
    protected RedisCache redis;

    public ActionResult ok()
    {
        HttpStatus status = HttpStatus.OK;
        return new ActionResult(status.toString(),status.value());
    }

    public ActionResult ok(String message)
    {
        return makeResult(message,HttpStatus.OK);
    }

    public<T> ActionResult<T> successWithData(T data)
    {
        HttpStatus status = HttpStatus.OK;
        return makeResult(status.toString(),status,data);
    }

    public<T> ActionResult<T> successWithData(String message,T data)
    {
        return new ActionResult<>(message,HttpStatus.OK.value(),data);
    }

    public ActionResult fail()
    {
        return ActionResult.DefinedErrorResult;
    }
    public ActionResult fail(String message){return new ActionResult(message,ActionResult.DefinedErrorResult.getCode());}

    public ActionResult fail(HttpStatus status)
    {
        return new ActionResult(status.toString(),status.value());
    }

    public ActionResult fail(String message,HttpStatus status)
    {
        return new ActionResult(message,status.value());
    }

    public HttpEntity<byte[]> fileResult(String fileName, byte[] data)
    {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length))
                .body(data);
    }

    public ActionResult makeResult(String message,HttpStatus status){
        return new ActionResult(message,status.value());
    }

    public<T> ActionResult<T> makeResult(String message,HttpStatus status,T data){
        return new ActionResult<>(message,status.value(),data);
    }
}
