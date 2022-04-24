package com.itfwh.spm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itfwh.spm.common.BaseContext;
import com.itfwh.spm.common.R;
import com.itfwh.spm.pojo.AddressBook;
import com.itfwh.spm.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

/**
 * @Author:Fwh
 * @date 2022/4/19 20:24
 * @ClassName AddressBookController
 * @Description
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        log.info("address:{}",addressBook.toString());
        addressBook.setUserId(BaseContext.getCurrentID());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        log.info("address:{}",addressBook);
        //根据userId设置当前用户的所有的地址都为非默认地址 isDefault = 0
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentID());
        addressBook.setIsDefault(0);
        addressBookService.update(addressBook,queryWrapper);

        //将指定的id的地址设置为默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook!=null){
            return R.success(addressBook);
        }else {
            return R.error("不存在该地址");
        }
    }

    @GetMapping("default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentID()).eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("未找到默认地址");
        }
    }

    /**
     * 展示地址栏
     * @return
     */
    @GetMapping("/list")
    public R<List> list(){
        log.info("查询所有地址");
        //查找当前登录用户的所有地址，并且按照默认地址在上进行排序
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper();
        //设置条件：用户id
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentID());
        //设置排序
        queryWrapper.orderByDesc(AddressBook::getIsDefault);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

}
