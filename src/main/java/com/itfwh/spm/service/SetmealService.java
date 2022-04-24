package com.itfwh.spm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itfwh.spm.dto.SetmealDto;
import com.itfwh.spm.pojo.Setmeal;

import java.util.List;

/**
 * @Author:Fwh
 * @date 2022/4/16 19:20
 * @ClassName SetmealService
 * @Description
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    //根据id获取套餐的信息
    public SetmealDto getBtIdWith_Dish(Long id);

    //修改套餐表和套餐菜品表
    public void updateWithDish(SetmealDto setmealDto);
    //删除套餐，同时需要删除套餐和菜品的关联数据
    public void remove_dish(List<Long> ids);
}
