package com.itfwh.chidebao.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author:Fwh
 * @date 2022/4/17 15:18
 * @ClassName UploadTest
 * @Description
 */
public class UploadTest {
    /**
     * 测试获取文件后缀
     */
    @Test
    public void testUpload(){
        String fileName = "hodiahdo.jpng";
        String substring = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(substring);
    }
}
