package com.itfwh.spm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfwh.spm.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author:Fwh
 * @date 2022/4/19 20:22
 * @ClassName AddressBookMapper
 * @Description
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
