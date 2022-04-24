package com.itfwh.spm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itfwh.spm.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author:Fwh
 * @date 2022/4/16 17:06
 * @ClassName CategoryMapper
 * @Description
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
