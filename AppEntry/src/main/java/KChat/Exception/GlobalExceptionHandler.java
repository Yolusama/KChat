package KChat.Exception;

import KChat.Result.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void handleAsyTimeExp(){
        final String content = "sse连接超时，将重新连接...";
        logger.info(content);
        System.out.println(content);
    }

    @ExceptionHandler(Exception.class)
    public ActionResult handleException(Exception ex){
        logger.error(ex.toString());
        ex.printStackTrace();
        return ActionResult.ServerError;
    }
}
