package com.itfwh.spm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itfwh.spm.mapper.ShoppingCartMapper;
import com.itfwh.spm.pojo.ShoppingCart;
import com.itfwh.spm.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @Author:Fwh
 * @date 2022/4/19 22:46
 * @ClassName ShoppingCartServiceImpl
 * @Description
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
