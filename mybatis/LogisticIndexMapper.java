package com.bessky.logistic.index.mapper;

import com.bessky.common.jdbc.mapper.MyBaseMapper;
import com.bessky.logistic.index.entity.LogisticIndex;
import com.bessky.logistic.index.entity.LogisticIndexQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物流首页持久层接口
 *
 * @author xuchengzhi
 * @date 2021/06/24
 */
public interface LogisticIndexMapper extends MyBaseMapper<LogisticIndex>
{
    /**
     * 获取等待确认索赔数量
     *
     * @return
     */
    @Select("SELECT COUNT(1) FROM t_logistics_reparation WHERE reparat_status = 1")
    Integer getWaitingConfirmClaimCount();

    /**
     * 获取等待退回数量
     *
     * @return
     */
    @Select("SELECT COUNT(1) FROM t_logistics_abnormal_order WHERE handle_way=1")
    Integer getWaitingReturnCount();

    /**
     * 获取重发数量
     *
     * @return
     */
    @Select("SELECT COUNT(1) FROM t_everyday_export WHERE format_id=-200")
    Integer getResendCount();

    /**
     * 获取等待收包数量
     *
     * @return
     */
    @Select("SELECT COUNT(1) FROM t_deliver_logistics_package WHERE out_status=1 AND order_count > 0")
    Integer getWaitingDeliverPackageCount();

    /**
     * 获取已退货数量
     *
     * @param fromDateTime
     * @param toDateTime
     * @return
     */
    @Select("SELECT COUNT(1) FROM t_order WHERE status=32 AND order_id IN (SELECT business_id FROM t_system_update WHERE update_type=9 AND creation_date >= #{fromDateTime} AND creation_date <= #{toDateTime})")
    Integer getReturnedCount(@Param("fromDateTime") LocalDateTime fromDateTime, @Param("toDateTime") LocalDateTime toDateTime);

    /**
     * 获取订单数量列表
     *
     * @return
     */
    @Select("SELECT status AS state, COUNT(1) AS count FROM t_order WHERE status IN (10068, 58, 59) GROUP BY status")
    List<LogisticIndex> getOrderCountList();

    /**
     * 获取异常数量列表
     *
     * @return
     */
    @Select("SELECT status AS state, COUNT(1) AS count FROM t_logistics_abnormal_order WHERE status<>5 GROUP BY status")
    List<LogisticIndex> getAbnormalCountList();

    /**
     * 获取轨迹节点预警数量列表
     *
     * @param query
     * @return
     */
    List<LogisticIndex> getTrackNodeWarningCountList(LogisticIndexQuery query);
}