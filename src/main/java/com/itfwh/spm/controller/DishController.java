package com.itfwh.spm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itfwh.spm.common.R;
import com.itfwh.spm.dto.DishDto;
import com.itfwh.spm.pojo.Category;
import com.itfwh.spm.pojo.Dish;
import com.itfwh.spm.pojo.DishFlavor;
import com.itfwh.spm.service.CategoryService;
import com.itfwh.spm.service.DishFlavorService;
import com.itfwh.spm.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author:Fwh
 * @date 2022/4/17 16:05
 * @ClassName DishController
 * @Description 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        //清理对应的菜品分类的缓存数据
        String key = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);

        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息的分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        //查询出来的分页对象pageInfo对象缺少categoryName，而子类包含categoryName，且需要查询两张表才能得到准确的信息
        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        //匹配查询的name，以及根据更新时间降序排列
        queryWrapper.like(name != null,Dish::getName,name)
                .orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝：将之前的分页对象接收之后进行拷贝，避免里面的数据records，因为records里面的数据是不完整的，需要进行优化
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        //设置一个新的records（list）接收对之前的records中没有categoryName的修改之后的list集合
        List<DishDto> list = records.stream().map((item)->{
            //初始化dishDto对象
            DishDto dishDto = new DishDto();

            //将已知的菜品信息复制给dishDto对象
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//菜品分类ID
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        //将新的records赋值给复制好的分页对象
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        //清理对应的菜品分类的缓存数据
        String key = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList =null;

        //构造key：dish_分类id_状态（1）
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从redis'中获取缓存数据，
        dishDtoList= (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList!=null){
            //如果存在，直接返回，不需要查询数据库
            return R.success(dishDtoList);
        }

        //不存在，需要查询数据库，并且，将查询到的菜品数据缓存到redis中

        //构建查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询状态为在售（1），且是当前菜品的分类的菜
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId()).eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);

            //select * from dish_flavor where dish_id = ?;
            List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());

        //缓存到redis中，下次使用的时候不需要访问数据库
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
/*
    public R<List<Dish>> list(Dish dish){
        //构建查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询状态为在售（1），且是当前菜品的分类的菜
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId()).eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }
*/

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("删除的分组：ids:{}",ids);

        dishService.remove_flavor(ids);

        //删除操作不用更新缓存，因为给用户的展示的数据是在售状态的商品，而在售状态的商品，在修改销售状态为停售之前是没有权限删除的
        //无权限删除，即在售物品无法删除，无法造成脏读数据
        return R.success("删除分组成功");
    }

    /**
     * 起售停售
     * @param ids
     * @param type
     * @return
     */
    @PostMapping("/status/{type}")
    public R<String> updateStatus(String[] ids, @PathVariable String type){
        log.info("修改的状态为：{}",type);
        List<String> list = Arrays.asList(ids);
        Dish dish = new Dish();
        //修改菜品表
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        //修改条件
        updateWrapper.in("id",list);
        //修改值
        dish.setStatus(new Integer(type));

        dishService.update(dish,updateWrapper);


        Set<Long> set = new HashSet<>();
        for (String id : ids) {
            Dish d = dishService.getById(id);
            set.add(d.getCategoryId());
        }

        //清理对应的菜品分类的缓存数据
        for (Long categoryId : set) {
            String key = "dish_"+categoryId+"_1";
            redisTemplate.delete(key);
        }

        return R.success("更新成功");
    }
}
