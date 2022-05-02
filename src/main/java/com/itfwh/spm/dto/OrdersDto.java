package com.itfwh.spm.dto;

import com.itfwh.spm.pojo.OrderDetail;
import com.itfwh.spm.pojo.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

}
