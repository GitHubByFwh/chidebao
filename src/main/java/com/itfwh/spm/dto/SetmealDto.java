package com.itfwh.spm.dto;

import com.itfwh.spm.pojo.Setmeal;
import com.itfwh.spm.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
