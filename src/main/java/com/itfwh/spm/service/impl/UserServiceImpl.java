package com.itfwh.spm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itfwh.spm.mapper.UserMapper;
import com.itfwh.spm.pojo.User;
import com.itfwh.spm.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Author:Fwh
 * @date 2022/4/19 17:12
 * @ClassName UserServiceImpl
 * @Description
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
