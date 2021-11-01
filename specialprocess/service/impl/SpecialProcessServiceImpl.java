package com.bessky.erp.warehouse.specialprocessapproval.service.impl;

import com.bessky.erp.core.common.bean.Pager;
import com.bessky.erp.foundation.exception.db.DBErrorCode;
import com.bessky.erp.foundation.exception.platform.BusinessException;
import com.bessky.erp.foundation.sqler.SqlerException;
import com.bessky.erp.starter.core.constants.AssertConstants;
import com.bessky.erp.warehouse.abnormalreceiptgood.service.impl.AbnormalReceiptGoodServiceImpl;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcess;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcessQueryCondition;
import com.bessky.erp.warehouse.specialprocessapproval.dao.SpecialProcessDao;
import com.bessky.erp.warehouse.specialprocessapproval.service.SpecialProcessService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * 特殊加工审批服务层实现
 *
 * @author 丁光辉
 * @version Bessky V100R001 2021年10月18日
 * @since Bessky V100R001C00
 */
@Service("specialProcessService")
public class SpecialProcessServiceImpl implements SpecialProcessService
{
    private static final Log logger = LogFactory.getLog(AbnormalReceiptGoodServiceImpl.class);

    @Resource
    private SpecialProcessDao specialProcessDao;

    @Override
    public SpecialProcess getSpecialProcess(Integer id)
    {
        return specialProcessDao.querySpecialProcess(id);
    }

    @Override
    public List<SpecialProcess> querySpecialProcessList()
    {
        return specialProcessDao.querySpecialProcessList();
    }

    @Override
    public List<SpecialProcess> querySpecialProcessList(SpecialProcessQueryCondition query, Pager pager)
    {
        Assert.notNull(query, AssertConstants.NOT_NULL_MSG);

        if (pager != null && pager.isQueryCount())
        {
            Integer count = specialProcessDao.querySpecialProcessCount(query);
            if (count == 0)
            {
                return Lists.newArrayList();
            }
            pager.setTotalCount(count);
        }

        List<SpecialProcess> specialProcessList = specialProcessDao.querySpecialProcessList(query, pager);

        return specialProcessList;
    }

    @Override
    public void createSpecialProcess(SpecialProcess specialProcess)
    {
        try
        {
            specialProcessDao.createSpecialProcess(specialProcess);
        }
        catch (SqlerException e)
        {
            logger.error(e.getMessage(), e);
            throw new BusinessException(DBErrorCode.DB_OPERATION_FAILED, e);
        }
    }

    @Override
    public void updateSpecialProcess(SpecialProcess specialProcess)
    {
        try
        {
            specialProcessDao.updateSpecialProcess(specialProcess);
        }
        catch (SqlerException e)
        {
            logger.error(e.getMessage(), e);
            throw new BusinessException(DBErrorCode.DB_OPERATION_FAILED, e);
        }
    }

    @Override
    public void batchUpdateSpecialProcess(List<SpecialProcess> specialProcessList)
    {
        if (CollectionUtils.isNotEmpty(specialProcessList))
        {
            try
            {
                specialProcessDao.batchUpdateSpecialProcess(specialProcessList);
            }
            catch (SqlerException e)
            {
                logger.error(e.getMessage(), e);
                throw new BusinessException(DBErrorCode.DB_OPERATION_FAILED, e);
            }
        }
    }
}