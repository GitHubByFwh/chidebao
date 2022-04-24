package com.itfwh.spm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itfwh.spm.common.CustomException;
import com.itfwh.spm.dto.DishDto;
import com.itfwh.spm.mapper.DishMapper;
import com.itfwh.spm.pojo.Dish;
import com.itfwh.spm.pojo.DishFlavor;
import com.itfwh.spm.pojo.Setmeal;
import com.itfwh.spm.service.DishFlavorService;
import com.itfwh.spm.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:Fwh
 * @date 2022/4/16 19:19
 * @ClassName DishServiceImpl
 * @Description
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品同时保存对应的菜品信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        //获取菜品的ID
        Long dishID = dishDto.getId();

        //口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        //使用流的方式进行遍历每一个口味信息并添加上对应的菜品id
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishID);
            return item;
        }).collect(Collectors.toList());
        //保存口味信息到dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据ID查询菜品信息以及口味信息
     * @param id
     * @return
     */
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();

        //查询菜品的基本信息，从dish表查
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品的口味信息，重dish_flavor表查
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表，dish
        this.updateById(dishDto);
        //更新口味表 dish_flavor
        //先清理当前菜品对应的口味：delete
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //然后添加当前提交的口味数据：insert
        List<DishFlavor> flavors = dishDto.getFlavors();
        //使用流的方式进行遍历每一个口味信息并添加上对应的菜品id
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品，同时珊瑚菜品和口味的关联
     * @param ids
     */
    @Override
    @Transactional
    public void remove_flavor(List<Long> ids) {

        //查询套餐状态，确定是否可以删除：售卖中禁止删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Dish::getId,ids).eq(Dish::getStatus,1);//设置查询的条件
        int count = this.count(queryWrapper);//查询出当前选择中是否存在在售数据
        //如果不能删除，抛出业务异常信息，
        if(count > 0){
            throw new CustomException("菜品正在售卖中,不能删除");
        }
        //删除菜品表的该菜品
        this.removeByIds(ids);

        //删除口味表的该菜品口味
        LambdaQueryWrapper<DishFlavor> Wrapper = new LambdaQueryWrapper();
        Wrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(Wrapper);
    }
}
