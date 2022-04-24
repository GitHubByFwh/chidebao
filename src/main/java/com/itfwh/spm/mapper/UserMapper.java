package com.itfwh.spm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfwh.spm.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author:Fwh
 * @date 2022/4/19 17:11
 * @ClassName UserMapper
 * @Description
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
