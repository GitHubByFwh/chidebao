package com.itfwh.spm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfwh.spm.pojo.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author:Fwh
 * @date 2022/4/19 22:45
 * @ClassName ShoppingCartMapper
 * @Description
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
