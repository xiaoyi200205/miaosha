package com.miaoshaobject.error;

/**
 * @author xiaoyi
 * @create 2022-09-30 16:44
 */
public interface CommonError {
    int getErrorCode();
    String getErrorMessage();
    CommonError setErrMsg(String errMsg);
}
