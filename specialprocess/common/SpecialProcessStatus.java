package com.bessky.pss.portal.business.specialprocess.common;

import com.bessky.platform.component.CodeNameEnum;
import com.bessky.platform.dict.EnumDataDict;

/**
 * 特殊加工审批状态
 *
 * @author 丁光辉
 * @version Bessky V100R001 2021年10月18日
 * @since Bessky V100R001C00
 */
@EnumDataDict
public enum SpecialProcessStatus implements CodeNameEnum
{
    TO_BE_CONFIRMED("1", "待确认"),

    PURCHASING_MANAGER_CONFIRM("2", "采购经理确认"),

    WAREHOUSE_MANAGER_CONFIRMED("3", "仓库经理确认"),

    COMPLETED("4", "已完成"),

    DISCARD("5", "废弃"),

    ;

    private String code;

    private String display;

    SpecialProcessStatus(String code, String display)
    {
        this.code = code;
        this.display = display;
    }

    /**
     * 根据code构造枚举
     *
     * @param code
     * @return
     */
    public static SpecialProcessStatus build(String code)
    {
        SpecialProcessStatus[] values = values();

        for (SpecialProcessStatus type : values)
        {
            if (type.code.equals(code))
            {
                return type;
            }
        }

        return null;
    }

    public int intCode()
    {
        return Integer.valueOf(this.code).intValue();
    }

    @Override
    public String code()
    {
        return this.code;
    }

    @Override
    public String display()
    {
        return this.display;
    }
}