package com.itfwh.spm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itfwh.spm.mapper.CategoryMapper;
import com.itfwh.spm.pojo.Category;

/**
 * @Author:Fwh
 * @date 2022/4/16 17:07
 * @ClassName CategoryService
 * @Description
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
