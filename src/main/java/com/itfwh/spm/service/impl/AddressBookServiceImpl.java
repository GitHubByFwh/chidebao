package com.itfwh.spm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itfwh.spm.mapper.AddressBookMapper;
import com.itfwh.spm.pojo.AddressBook;
import com.itfwh.spm.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author:Fwh
 * @date 2022/4/19 20:23
 * @ClassName AddressBookServiceImpl
 * @Description
 */
@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
