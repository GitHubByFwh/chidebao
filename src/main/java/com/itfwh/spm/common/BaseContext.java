package com.itfwh.spm.common;

/**
 * @Author:Fwh
 * @date 2022/4/16 16:06
 * @ClassName BaseContext
 * @Description 基于ThreadLocal封装的工具类，用于保存获取当前登录用户的id
 */
public class BaseContext {
    /**
     * threadLocal设置当前线程为作用域的id值
     */
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentID(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public  static Long getCurrentID(){
        return threadLocal.get();
    }
}
