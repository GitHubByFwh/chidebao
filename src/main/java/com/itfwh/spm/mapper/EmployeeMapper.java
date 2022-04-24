package com.itfwh.spm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfwh.spm.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author:Fwh
 * @date 2022/4/14 20:53
 * @ClassName EmployeeMapper
 * @Description
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
