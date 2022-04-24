package com.itfwh.spm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itfwh.spm.common.BaseContext;
import com.itfwh.spm.common.R;
import com.itfwh.spm.pojo.Category;
import com.itfwh.spm.pojo.ShoppingCart;
import com.itfwh.spm.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author:Fwh
 * @date 2022/4/19 22:47
 * @ClassName ShoppingCartController
 * @Description
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}",shoppingCart);
        //设置用户id：指定是哪个用户的购物车数据
        shoppingCart.setUserId(BaseContext.getCurrentID());
        //一份菜品，添加的时候要查一下在不在数据库中，如果存在且口味一致，数量加一，否则添加一条数据
        //查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        //判断接收的是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        if (dishId!= null){
            //添加到购物车的是菜品
            //设置根据userId和dishId         前端未实现以及口味dishFlavor查询
            queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId())
                    .eq(ShoppingCart::getDishId,shoppingCart.getDishId());
                    //.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId())
                    .eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
                    //.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }
        //sql:select * from shopping_cart where userId = ? and (dishId = ? or setmealId = ?) and dishFlavor = ?;
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne != null){
            //存在
            //个数加一
            Integer number = cartServiceOne.getNumber();//原来的数量
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //不存在
            //新增
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");
        Long userId = BaseContext.getCurrentID();//获取当前用户的id
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //sql:delete from shoppingCart where user_id = ?;
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentID());//根据userId清空当前用户购物车

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("shoppingCart:{}",shoppingCart.toString());
        //获取用户id，给shoppingCart设置
        Long userId = BaseContext.getCurrentID();
        shoppingCart.setUserId(userId);
        //查看当前用户中的添加的个数：等于1，删除该行，否则number-1；
        //查看是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        //判断接收的是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper();
        if (dishId!= null){
            //添加到购物车的是菜品
            //设置根据userId和dishId         前端未实现以及口味dishFlavor查询
            queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId())
                    .eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            //.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId())
                    .eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
            //.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }
        //sql:select * from shopping_cart where userId = ? and (dishId = ? or setmealId = ?) and dishFlavor = ?;
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        //判断数量是否为1
        Integer cartServiceOneNumber = cartServiceOne.getNumber();//商品数量
        if (cartServiceOneNumber>1){
            //更新表
            cartServiceOne.setNumber(cartServiceOneNumber-1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //删除
            shoppingCartService.removeById(cartServiceOne.getId());
            cartServiceOne.setNumber(0);
        }
        return R.success(cartServiceOne);
    }
}
