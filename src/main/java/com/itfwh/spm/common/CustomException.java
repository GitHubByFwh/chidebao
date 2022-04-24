package com.itfwh.spm.common;

/**
 * @Author:Fwh
 * @date 2022/4/16 19:37
 * @ClassName CustomException
 * @Description 自定义的业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
