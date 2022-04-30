package propofol.ptfservice.api.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import propofol.ptfservice.api.common.exception.dto.ErrorDto;
import propofol.ptfservice.domain.exception.NotFoundPortfolioException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ExceptionAdviceController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto SQLException(ConstraintViolationException e){
        log.info("Message = {}", e.getMessage());
        return null;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto badRequestType1Error(HttpMessageNotReadableException e) {
        ErrorDto errorDto = createError("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto notFoundPortfolioException(NotFoundPortfolioException e) {
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto NotMatchMemberException (NotMatchMemberException e) {
        ErrorDto errorDto = createError(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorDto;
    }

    private ErrorDto createError(String errorMessage, HttpStatus status) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(errorMessage);
        errorDto.setStatus(status.value());
        return errorDto;
    }
}
