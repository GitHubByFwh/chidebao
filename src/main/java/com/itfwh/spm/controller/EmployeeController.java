package com.itfwh.spm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itfwh.spm.common.R;
import com.itfwh.spm.pojo.Employee;
import com.itfwh.spm.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @Author:Fwh
 * @date 2022/4/14 20:58
 * @ClassName EmployeeController
 * @Description
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /*
        * 1.将页面提交的密码password进行md5加密处理
        * 2.根据页面提交的用户名username查询数据库
        * 3.如果没有查询到则返回登录失败结果，否则进入4
        * 4.密码比对，如果不一致则返回登录失败结果，否则进入5
        * 5.查看员工的状态，如果为禁用状态，则返回员工已禁用结果 否则进入6
        * 6.登录成功，将员工id存入session并返回登录成功结果
        * */
        //1.
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);//设计表的时候employee表的字段是唯一（unique）的

        //3.
        if (one == null){
            //无用户
            return R.error("登录失败");
        }
        //4.
        if (!one.getPassword().equals(password)){
            //校验不成功
            return R.error("登录失败");
        }

        if (one.getStatus() == 0){
            //账号禁用
            return R.error("登录失败");
        }
        //6.
        request.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前登录的员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}" ,employee.toString());
        //初始化员工密码123456，需要MD5加密
        String pw = "123456";
        employee.setPassword(DigestUtils.md5DigestAsHex(pw.getBytes()));

        /*employee.setCreateTime(LocalDateTime.now());//获取当前系统时间作为注册时间
        employee.setUpdateTime(LocalDateTime.now());//获取当前系统时间作为更新时间

        //获取当前登录用户的id
        Long empid = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empid);
        employee.setUpdateUser(empid);*/

        boolean save = employeeService.save(employee);
        return save?R.success("新增成功"):R.error("新增失败");
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page( int page,int pageSize,String name){
        /*
        * 1.页面发送的ajax请求，将分页查询参数（page，pageSize，name）提交到服务器
        * 2.服务器controller接收页面提交的数据并调用service查询数据
        * 3.service调用mapper操作数据库，查询分页数据
        * 4.controller将查询到的分页数据响应给页面
        * 5.页面接收到分页数据并通过ElementUI组件展示到页面上
        * */
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //2.
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper();
        //添加过滤条件
        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查新
        employeeService.page(pageInfo,wrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id为：{}" ,id);

        /*//设置更新的时间以及修改人
        Long user = (Long) request.getSession().getAttribute("employee");//取出当前用户的id
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(user);*/
        employeeService.updateById(employee);
        return R.success("信息更新成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应的员工信息");
    }
}
