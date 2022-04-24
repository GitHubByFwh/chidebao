package com.itfwh.spm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itfwh.spm.pojo.Orders;

/**
 * @Author:Fwh
 * @date 2022/4/20 11:37
 * @ClassName OrdersService
 * @Description
 */
public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
