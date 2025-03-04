package KChat.Exception;

import KChat.Result.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    public ActionResult handleException(Exception ex){
        logger.error(ex.toString());
        ex.printStackTrace();
        return ActionResult.ServerError;
    }
}
