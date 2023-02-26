package com.miaoshaobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoyi
 * @create 2022-09-30 16:26
 */
@Data
public class CommonReturnType {
    //返回请的处理的结果“success”或“fail”
    private String status;

    //若status=success，返回前端需要的json数据
    //若status=fail，则data内使用通用的错误码格式
    private Object data;



    //定义一个通用的创建方法
    public static CommonReturnType  create(Object result){
        return CommonReturnType.create(result,"success");
    }
    public static CommonReturnType  create(Object result,String status){
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }
}
