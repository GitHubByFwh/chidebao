package com.itfwh.spm.controller;

import com.itfwh.spm.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:Fwh
 * @date 2022/4/20 11:43
 * @ClassName OrderDetailController
 * @Description 订单明细
 */
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
}
