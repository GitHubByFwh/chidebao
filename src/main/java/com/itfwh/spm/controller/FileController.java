package com.itfwh.spm.controller;

import com.itfwh.spm.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author:Fwh
 * @date 2022/4/17 9:15
 * @ClassName CommonController
 * @Description 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class FileController {

    @Value("${chiDeBao.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //当前file是一个临时文件，需要转存到指定位置，否则请求完成后文件就会删除
        log.info(file.toString());

        //原始文件名:但是存在原始文件名相同存在覆盖问题
        String originalFilename = file.getOriginalFilename();//abc.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //判断文件地址是否存在不存在则创建
        File dir = new File(basePath);
        //判断目录是否存在
        if (!dir.exists()){
            //创建目录
            dir.mkdirs();
        }

        //使用UUID重新生成文件名，防止文件名称重复，造成覆盖
        String fileName = UUID.randomUUID().toString()+suffix;

        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name , HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //输出流，通过输出流将文件写会浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应的文件是什么文件
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
