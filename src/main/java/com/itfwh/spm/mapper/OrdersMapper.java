package com.itfwh.spm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfwh.spm.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author:Fwh
 * @date 2022/4/20 11:36
 * @ClassName OrdersMapper
 * @Description
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
