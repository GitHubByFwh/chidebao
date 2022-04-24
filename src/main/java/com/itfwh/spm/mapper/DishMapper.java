package com.itfwh.spm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfwh.spm.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author:Fwh
 * @date 2022/4/16 19:18
 * @ClassName DishMapper
 * @Description
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
