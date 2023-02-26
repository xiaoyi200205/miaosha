package com.miaoshaobject.validator;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author xiaoyi
 * @create 2022-10-02 18:07
 */
@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;


    //实现校验方法并返回校验结果
    public ValidationResult validate(Object bean){
        ValidationResult validationResult = new ValidationResult();
        Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(bean);
        if(constraintViolationSet.size()>0){
            //有错误
            validationResult.setHasError(true);
            for(ConstraintViolation<Object> set : constraintViolationSet){
                //错误的信息
                String errMsg = set.getMessage();
                //错误的字段
                String propertyName = set.getPropertyPath().toString();
                validationResult.getErrorMsgMap().put(propertyName,errMsg);
            }
        }
        return validationResult;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        //将 hibernate validator 通过工厂的初始化方式使其实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
