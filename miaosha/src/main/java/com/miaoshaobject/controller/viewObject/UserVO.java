package com.miaoshaobject.controller.viewObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiaoyi
 * @create 2022-09-29 21:40
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserVO {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String telphone;
}
