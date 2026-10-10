package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    //插入订单数据
    void insert(Orders orders);
    //根据订单号和用户id查询订单数据
    @Select("select * from orders where number = #{outTradeNo} and user_id = #{userId}")
    Orders getOrderByNumberAndUserId(String outTradeNo, Long userId);
    //更新订单数据
    void update(Orders orders);
    //历史订单查询
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    //查询单个订单详情数据
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    //查询超时订单数据
    @Select("select * from orders where status = #{status} and order_time < #{dateTime}")
    List<Orders> getOrdersByStatusAndDateTime(Integer status, LocalDateTime dateTime);
    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 统计指定时间区间内的销量排名前10
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin,LocalDateTime end);
    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);
}
