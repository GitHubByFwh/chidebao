package com.itfwh.spm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itfwh.spm.common.CustomException;
import com.itfwh.spm.dto.SetmealDto;
import com.itfwh.spm.mapper.SetmealMapper;
import com.itfwh.spm.pojo.Category;
import com.itfwh.spm.pojo.Setmeal;
import com.itfwh.spm.pojo.SetmealDish;
import com.itfwh.spm.service.CategoryService;
import com.itfwh.spm.service.SetmealDishService;
import com.itfwh.spm.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author:Fwh
 * @date 2022/4/16 19:21
 * @ClassName SetmealServiceImpl
 * @Description
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public SetmealDto getBtIdWith_Dish(Long id) {

        /*
        *  DishDto dishDto = new DishDto();

        //查询菜品的基本信息，从dish表查
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品的口味信息，重dish_flavor表查
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        * */
        SetmealDto setmealDto = new SetmealDto();
        //查找套餐的信息 操作套餐表
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        //根据套餐id查询setmeal_dish表获取菜品id
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    /**
     * 修改套餐，并修改相关的菜品
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //修改套餐表的信息
        this.updateById(setmealDto);
        Long setmealId = setmealDto.getId();
        //根据id修改setmeal_Dish表的菜品信息
        //1.根据套餐id，删除对应的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(queryWrapper);
        //2.根据id添加新的菜品信息进去
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void remove_dish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除：售卖中禁止删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);//设置查询的条件
        int count = this.count(queryWrapper);//查询出当前选择中是否存在在售数据
        //如果不能删除，抛出业务异常信息，
        if(count > 0){
            throw new CustomException("套餐正在售卖中,不能删除");
        }
        //删除套餐表的该套餐
        this.removeByIds(ids);

        //删除套餐菜品表的套餐对应的菜品
        LambdaQueryWrapper<SetmealDish> Wrapper = new LambdaQueryWrapper();
        Wrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(Wrapper);
    }
}
