package com.miaoshaobject.error;

/**
 * @author xiaoyi
 * @create 2022-09-30 16:48
 */
public enum EmBusinessError implements CommonError{
    //定义通用的错误类型10001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    //未知错误
    UNKNOWN_ERROR(10002,"未知错误"),

    //2000开头为用户信息相关的错误
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_LOGIN_FAIL(20002,"用户手机号或者是密码不正确"),
    USER_NOT_LOGIN(20003,"用户还未登录"),

    //30000开头为交易信息定义错误
    STOCK_NOT_ENOUGH(30001,"库存不足"),

    ;

    private EmBusinessError(int errCode,String errMsg){
        this.errCode=errCode;
        this.errMsg=errMsg;
    }
    private int errCode;
    private String errMsg;

    @Override
    public int getErrorCode() {
        return this.errCode;
    }

    @Override
    public String getErrorMessage() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
