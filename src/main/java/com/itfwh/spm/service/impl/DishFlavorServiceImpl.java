package com.itfwh.spm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itfwh.spm.mapper.DishFlavorMapper;
import com.itfwh.spm.pojo.DishFlavor;
import com.itfwh.spm.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @Author:Fwh
 * @date 2022/4/17 16:04
 * @ClassName DishFlavorServiceImpl
 * @Description
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>implements DishFlavorService {
}
