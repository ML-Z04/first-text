package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时类(用来处理订单超时问题和订单已派送用户没有点击完成内容)
 */
@Component
@Slf4j
public class OrderTask {
    //注入service
    @Autowired
    private OrderMapper orderMapper;

    //处理订单超时问题
    @Scheduled(cron="0 * * * * ?" )
    public void processOrderOutTime(){
        log.info("处理订单超时的情况");
        /**
         * 订单超时的时间为15分钟  如果在15分钟之内 不会修改订单的状态  超过15分钟就会修改订单状态
         * 修改的订单状态:
         *  1.需要把订单状态修改成为6
         *  2.添加订单取消原因字段
         *  3.添加订单取消时间
         */
        //1.构建符合订单超时时间的条件(超时时间)
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(-15);
        //2.根据订单的状态和超时时间来查询符合要求的订单数据
        List<Orders> ordersList =  orderMapper.getOrdersByStatusAndDateTime(Orders.PENDING_PAYMENT,dateTime);
        //3.根据查询到的订单集合数据来更新订单内容(订单状态、订单取消原因、订单取消时间)
        //属于lamobd 表达式
        if(ordersList!=null&&ordersList.size()>0){
            ordersList.forEach(orders->{
                //订单状态
                orders.setStatus(Orders.CANCELLED);
                //订单取消原因
                orders.setCancelReason("支付超时,取消订单!");
                //订单取消时间
                orders.setCancelTime(LocalDateTime.now());
                //进行订单数据的更新
                orderMapper.update(orders);
            });
        }
    }
    //处理订单状态为已派送用户没有点击完成情况订单
    @Scheduled(cron="0 0 1 * * ?")
    //@Scheduled(cron="0 * * * * ?" )
    public void processOrderDelivery(){
        log.info("处理订单状态为派送中的状态将状态改为已完成");
        //1.构建符合订单数据的时间(获取当前时间-60)
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(-60);
        //2.调用订单数据查询方法根据订单状态和订单时间来查询
        List<Orders> ordersList = orderMapper.getOrdersByStatusAndDateTime(Orders.DELIVERY_IN_PROGRESS, dateTime);
        //3.更新符合条件的数据
        if(ordersList!=null&&ordersList.size()>0){
            ordersList.forEach(orders -> {
                //修改订单状态为已完成
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            });
        }
    }
}
