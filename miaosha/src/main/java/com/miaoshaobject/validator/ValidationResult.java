package com.miaoshaobject.validator;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyi
 * @create 2022-10-02 18:01
 */
@Data
public class ValidationResult {
    //校验结果是否有错
    private boolean hasError = false;

    //存放错误信息的map
    private Map<String,String> errorMsgMap = new HashMap<>();


    //实现通过格式化字符串信息获取错误结果msg的方法
    public String getErrMsg(){
        String errors = StringUtils.join(this.errorMsgMap.values().toArray(), ",");
        return errors;
    }


}
