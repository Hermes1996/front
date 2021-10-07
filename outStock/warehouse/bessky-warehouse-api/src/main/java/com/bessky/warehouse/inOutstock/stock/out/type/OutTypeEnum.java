package com.bessky.warehouse.inOutstock.stock.out.type;

import com.bessky.common.model.type.CodeNameEnum;

import java.util.Arrays;

/**
 * 出库类型枚举类
 *
 * @author dinggaunghui
 * @date 2021/08/12
 */
public enum OutTypeEnum implements CodeNameEnum
{
    SALES("1", "销售出库"),

    OVERSEA_LOCATION("2", "自营海外仓调拨出库"),

    FBA_LOCATION("13", "FBA仓出库"),

    INVENTORY_VERIFICATION("3", "库存清点出库"),

    RETURN("8", "退货出库"),

    PACKAGE("9", "包材出库"),

    OTHER("5", "其他出库"),

    SIMPLE_LEND("11", "样品借出"),

    FBA_TRANSFERRING("12", "FBA仓调拨出库"),

    OVERSEA_TRANSFERRING("14", "海外仓调拨出库"),

    SCRAP_GOODS("15", "报废出库"),

    ORDER_RETRANSMISSION("16", "重发订单出库"),

    ORDER_ALLOCATION("17", "SKU调拨出库"),

    DISTRIBUTION_OUT("19", "分销出库"),

    WAREHOUSE_SIMPLE_OUT("18", "仓库留样出库");

    private String code;

    private String display;

    OutTypeEnum(String code, String display)
    {
        this.code = code;
        this.display = display;
    }

    public static OutTypeEnum build(int code)
    {
        return Arrays.stream(values()).filter(type -> type.intCode() == code).findAny().orElse(null);
    }

    public static OutTypeEnum build(String display)
    {
        return Arrays.stream(values()).filter(type -> type.display.equals(display)).findAny().orElse(null);
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

    public int intCode()
    {
        return Integer.parseInt(this.code);
    }

}
