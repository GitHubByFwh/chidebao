package com.itfwh.spm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itfwh.spm.dto.DishDto;
import com.itfwh.spm.pojo.Dish;

import java.util.List;

/**
 * @Author:Fwh
 * @date 2022/4/16 19:18
 * @ClassName DishService
 * @Description
 */
public interface DishService extends IService<Dish> {
    //新增菜品，并插入菜品的口味，需要操作两张表，：dish，dishFlavor
    public void saveWithFlavor(DishDto dishDto);

    //根据ID查询菜品信息以及口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新口味信息
    public void updateWithFlavor(DishDto dishDto);
    //删除菜品，同时珊瑚菜品和口味的关联
    public void remove_flavor(List<Long> ids);
}
