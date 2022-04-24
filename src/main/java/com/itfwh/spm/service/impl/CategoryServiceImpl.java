package com.itfwh.spm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itfwh.spm.common.CustomException;
import com.itfwh.spm.mapper.CategoryMapper;
import com.itfwh.spm.pojo.Category;
import com.itfwh.spm.pojo.Dish;
import com.itfwh.spm.pojo.Setmeal;
import com.itfwh.spm.service.CategoryService;
import com.itfwh.spm.service.DishService;
import com.itfwh.spm.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author:Fwh
 * @date 2022/4/16 17:08
 * @ClassName CategoryServiceImpl
 * @Description
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除之前进行判断
     * 查看当前分类的id是否关联菜品和套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联菜品，如果关联，抛出业务异常
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(queryWrapper);
        if (count>0){
            //包含关联，抛出业务异常
            throw new CustomException("当前分类关联了菜品，不能删除");
        }
        //查询当前分类是否关联套餐，如果关联，抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealQueryWrapper);
        if (count1>0) {
            //包含关联，抛出业务异常
            throw new CustomException("当前分类关联了套餐，不能删除");
        }
        //正常删除
        super.removeById(id);
    }
}
