package com.itfwh.spm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itfwh.spm.mapper.OrderDetailMapper;
import com.itfwh.spm.pojo.OrderDetail;
import com.itfwh.spm.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author:Fwh
 * @date 2022/4/20 11:42
 * @ClassName OrderDetailServiceImpl
 * @Description
 */
@Service
@Slf4j
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
