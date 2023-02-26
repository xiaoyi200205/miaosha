package com.miaoshaobject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaobject.controller.viewObject.UserVO;
import com.miaoshaobject.error.BusinessException;
import com.miaoshaobject.error.EmBusinessError;
import com.miaoshaobject.response.CommonReturnType;
import com.miaoshaobject.service.UserService;
import com.miaoshaobject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoyi
 * @create 2022-09-29 21:05
 */
@Controller
@RequestMapping(value = "/user")
@CrossOrigin(allowCredentials = "true",originPatterns = "*")
public class UserController extends BaseController{
    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;


    //用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验,手机号或者是密码一个为空就抛出异常

        //System.out.println("telphone:"+telphone+"========"+"   password:"+password+"   esc:"+EncodeByMd5(password));

        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone)&& org.apache.commons.lang3.StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //用户登录服务,用来校验用户登录是否合法
        UserModel userModel = userService.validateLogin(telphone, this.EncodeByMd5(password));
        //System.out.println("登录界面:"+userModel);

        //将登录凭证加入到用户登录成功的session内

        //修改成，若用户登录成功后将对应的登录信息和登录凭证一起存入redis中

        //生成登录评证token，UUID
        String uuidToken = UUID.randomUUID().toString().replace("-","");

        //建立token和用户登录太之间的联系
        //如果uuid存在，我们就认为这个用户已经登录
        redisTemplate.opsForValue().set(uuidToken,userModel);
        //设置一个小时后uuid过期
        redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);

//        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
//        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
        //下发了token
        return CommonReturnType.create(uuidToken);
    }


    //用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name ="telphone")String telphone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age")Integer age,
                                     @RequestParam(name = "password")String password
                                    ) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpCode符合
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        if(!StringUtils.equals(inSessionOtpCode,otpCode)){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);

        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byPhone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        System.out.println(userModel);
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    //给密码加密
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }

    //用户获取otp短信的接口
    @RequestMapping(value = "/getOtp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone){
        //按照一定的规则生成OPT验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt+=10000;
        String otpCode = String.valueOf(randomInt);

        //将OPT验证码同对应用户的手机号进行关联,使用httpSession的方式绑定他的手机号和OTPCODE
        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        //将OTP验证码通过短信通道发送给用户，（省略）
        System.out.println("telphone = "+telphone +" & otpCode = "+otpCode);
        return CommonReturnType.create(null);
    }



    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        //调用service层的方法
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }


        //将核心领域模型用户对象转化为可供ui使用的viewObject
        UserVO userVO = convertFromModel(userModel);

        //返回通用对象
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }else{
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userModel,userVO);
            return userVO;
        }
    }
}
