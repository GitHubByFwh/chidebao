package com.itfwh.spm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itfwh.spm.common.R;
import com.itfwh.spm.dto.OrdersDto;
import com.itfwh.spm.pojo.Orders;
import com.itfwh.spm.pojo.User;
import com.itfwh.spm.service.OrdersService;
import com.itfwh.spm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:Fwh
 * @date 2022/4/20 11:38
 * @ClassName OrdersController
 * @Description 订单
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private UserService userService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 分页展示订单数据
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page , int pageSize , String number, String beginTime , String endTime ){
        log.info("page ={},pageSize={},number ={},beginTime={},endTime={}",page,pageSize,number,beginTime,endTime);
        //查询订单表
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //筛选订单编号以及开始结束的时间
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> pageDto= new Page<>(page,pageSize);
        queryWrapper.eq(StringUtils.isNotEmpty(number),Orders::getNumber,number)
                .between(StringUtils.isNotEmpty(beginTime)&&!StringUtils.isNotEmpty(endTime),
                        Orders::getOrderTime,
                        beginTime,endTime);
        ordersService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo,pageDto,"records");

        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> ordersDtoList = records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            Long userId = item.getUserId();
            User user = userService.getById(userId);
            ordersDto.setUserName(user.getName());
            return ordersDto;
        }).collect(Collectors.toList());

        pageDto.setRecords(ordersDtoList);
        return R.success(pageDto);
    }

    /**
     *
     * @return
     */
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders) {
        log.info("orders={}",orders);
        ordersService.updateById(orders);
        return R.success("订单进度修改成功");
    }
}
