package com.bessky.logistic.conf.core.controller;

import com.bessky.common.core.util.BeanUtils;
import com.bessky.common.model.core.PageDTO;
import com.bessky.common.web.json.Response;
import com.bessky.logistic.conf.core.entity.LogisticsConf;
import com.bessky.logistic.conf.core.entity.LogisticsConfCustom;
import com.bessky.logistic.conf.core.service.LogisticsConfService;
import com.bessky.tool.queue.kafka.service.KafkaService;
import com.bessky.user.core.entity.User;
import com.bessky.user.core.service.UserService;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.common.utils.Assert;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物流配置表控制层
 *
 * @author liunancun
 * @date 2020/04/11
 */
@RestController
@RequestMapping("logistics/confs")
public class LogisticsConfController
{
    @Reference
    private LogisticsConfService logisticsConfService;

    @Reference
    private UserService userService;
    
    @Reference
    private KafkaService kafkaService;

    @PostMapping("list/page")
    @PreAuthorize("hasAuthority('5501010000')")
    public Response<PageDTO<LogisticsConfCustom>> getLogisticsConfPageList(@RequestBody LogisticsConfQuery query)
    {
        // 获取物流配置列表
        PageDTO<LogisticsConfCustom> page = logisticsConfService.getLogisticsConfPageList(query).clone(BeanUtils::clone, LogisticsConfCustom.class);

        // 设置用户名字
        this.setUserName(page.getRecords());

        return Response.ok(page);
    }

    @GetMapping("{id}")
    public Response<LogisticsConf> getLogisticsConf(@PathVariable("id") Integer id)
    {
        // 获取物流配置对象
        LogisticsConf logisticsConf = logisticsConfService.getLogisticsConf(id);

        return Response.ok(logisticsConf);
    }

    @PostMapping("create")
    @PreAuthorize("hasAuthority('5501010200')")
    public Response<?> createLogisticsConf(@RequestBody LogisticsConfCustom logisticsConf)
    {
        // 创建物流配置对象
        LogisticsConf newLogisticsConf = logisticsConfService.createLogisticsConf(logisticsConf);

        // 记录日志
        kafkaService.log(newLogisticsConf.getLogisticsId(), "LOGISTICS_CONF", "创建物流配置");

        return Response.ok();
    }

    @PostMapping("update")
    @PreAuthorize("hasAuthority('5501010300')")
    public Response<?> updateLogisticsConf(@RequestBody LogisticsConfCustom logisticsConf)
    {
        // 主键不能为空
        Integer id = logisticsConf.getLogisticsId();
        Assert.notNull(id, "主键不能为空");
        
        // 修改物流配置对象
        logisticsConfService.updateLogisticsConf(logisticsConf);

        // 记录日志
        kafkaService.log(id, "LOGISTICS_CONF", "修改物流配置");

        return Response.ok();
    }

    /**
     * 设置用户名字
     *
     * @param logisticsConfs
     */
    private void setUserName(List<LogisticsConfCustom> logisticsConfs)
    {
        if (CollectionUtils.isEmpty(logisticsConfs))
        {
            return;
        }

        // 收集用户标识
        List<Integer> userIds = logisticsConfs.stream().map(LogisticsConf::getCreatedBy).distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(userIds))
        {
            return;
        }

        // 获取用户映射
        Map<Integer, String> userMap = userService.getUserList(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, User::getName));

        if (MapUtils.isEmpty(userMap))
        {
            return;
        }

        // 设置用户名字
        logisticsConfs.forEach(item -> item.setCreateByName(userMap.get(item.getCreatedBy())));
    }
}