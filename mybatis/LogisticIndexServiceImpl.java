package com.bessky.logistic.index.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bessky.logistic.index.entity.LogisticIndex;
import com.bessky.logistic.index.entity.LogisticIndexQuery;
import com.bessky.logistic.index.mapper.LogisticIndexMapper;
import com.bessky.logistic.index.service.LogisticIndexService;
import org.apache.dubbo.config.annotation.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物流首页服务层实现
 *
 * @author xuchengzhi
 * @date 2021/06/24
 */
@Service
public class LogisticIndexServiceImpl extends ServiceImpl<LogisticIndexMapper, LogisticIndex> implements LogisticIndexService
{
    @Override
    public LogisticIndex getStateCount()
    {
        LogisticIndex logisticIndex = new LogisticIndex();
        logisticIndex.setWaitingConfirmClaimCount(baseMapper.getWaitingConfirmClaimCount());
        logisticIndex.setWaitingReturnCount(baseMapper.getWaitingReturnCount());
        logisticIndex.setResendCount(baseMapper.getResendCount());
        logisticIndex.setWaitingDeliverPackageCount(baseMapper.getWaitingDeliverPackageCount());

        // 获取昨天已退货数量
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        logisticIndex.setReturnedCount(baseMapper
                .getReturnedCount(LocalDateTime.of(yesterday.toLocalDate(), LocalTime.MIN), LocalDateTime
                        .of(yesterday.toLocalDate(), LocalTime.MAX)));

        // 获取订单数量
        Map<Integer, Integer> orderCountMap = baseMapper.getOrderCountList().stream()
                .collect(Collectors.toMap(LogisticIndex::getState, LogisticIndex::getCount));
        logisticIndex.setOrderCountMap(orderCountMap);

        // 获取异常数量
        Map<Integer, Integer> abnormalCountMap = baseMapper.getAbnormalCountList().stream()
                .collect(Collectors.toMap(LogisticIndex::getState, LogisticIndex::getCount));
        logisticIndex.setAbnormalCountMap(abnormalCountMap);

        return logisticIndex;
    }

    @Override
    @DS("track")
    public Map<Integer, Integer> getTrackNodeWarningCountMap(LogisticIndexQuery query)
    {
        return baseMapper.getTrackNodeWarningCountList(query).stream()
                .collect(Collectors.toMap(LogisticIndex::getState, LogisticIndex::getCount));
    }
}