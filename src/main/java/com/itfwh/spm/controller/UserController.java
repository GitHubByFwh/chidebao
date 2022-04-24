package com.itfwh.spm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itfwh.spm.utils.ValidateCodeUtils;
import com.itfwh.spm.common.R;
import com.itfwh.spm.pojo.User;
import com.itfwh.spm.service.UserService;
import com.itfwh.spm.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Author:Fwh
 * @date 2022/4/19 17:13
 * @ClassName UserController
 * @Description
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (Strings.isNotEmpty(phone)){
            //生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用阿里云短信服务API发送短信
            log.info("code = {}",code);

            /*
            * signName 签名：阿里云短信测试
            * templateCode 模板：SMS_154950909
            * phoneNumbers 手机号：phone
            * param 参数：验证码
            * */
            //SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
            //保存验证码，保存到session
            session.setAttribute(phone,code);
            return R.success("验证码发送成功");
        }

        return R.error("验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号phone，以及验证码code
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从session中获取保存的验证码
        String  sessionCode = (String) session.getAttribute(phone);
        //验证码校验
        if (session!=null && sessionCode.equals(code)){
            //比对成功
            //判断是否为新用户，自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                //新用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前登录的员工的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
