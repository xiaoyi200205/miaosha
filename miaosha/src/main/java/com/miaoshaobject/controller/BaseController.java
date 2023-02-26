package com.miaoshaobject.controller;

import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.error.EmBusinessError;
import com.miaoshaobject.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyi
 * @create 2022-09-30 17:35
 */
public class BaseController {

    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";


    //定义exceptionhandler解决未被controller层吸收的exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){
        Map<String,Object> responseData = new HashMap<>();
        if(ex instanceof BusinessException){
            BusinessException businessException = (BusinessException) ex;
            responseData.put("errCode",businessException.getErrorCode());
            responseData.put("errMsg",businessException.getErrorMessage());
        }else {
            responseData.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
            responseData.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrorMessage());
        }
        return CommonReturnType.create(responseData,"fail");
    }
}
