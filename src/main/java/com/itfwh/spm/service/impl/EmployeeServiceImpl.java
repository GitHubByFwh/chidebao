package com.itfwh.spm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itfwh.spm.mapper.EmployeeMapper;
import com.itfwh.spm.pojo.Employee;
import com.itfwh.spm.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @Author:Fwh
 * @date 2022/4/14 20:55
 * @ClassName EmployeeServiceImpl
 * @Description
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
