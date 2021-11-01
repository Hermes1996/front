<template>

    <div>
        <el-description title="出库单">
            <el-description-item label="编号" :span-map="{xl:2}">
                <template slot="content">
                    {{dataSource.id}}
                </template>
            </el-description-item>
            <el-description-item label="出库时间" :span-map="{xl:3}">
                <template slot="content">
                    {{dataSource.createTime}}
                </template>
            </el-description-item>
            <el-description-item label="出库类型" :span-map="{xl:3}">
                <template slot="content">
                    {{dataExtra.outStockType}}
                </template>
            </el-description-item>
            <el-description-item label="出库人" :span-map="{xl:2}">
                <template slot="content">
                    {{dataExtra.outStockName}}
                </template>
            </el-description-item>
            <el-description-item label="仓库" :span-map="{xl:2}">
                <template slot="content">
                    {{dataExtra.warehouseName}}
                </template>
            </el-description-item>
            <el-description-item label="状态" :span-map="{xl:2}">
                <template slot="content">
                    {{dataExtra.outStockState}}
                </template>
            </el-description-item>
            <el-description-item label="备注" :span-map="{xl:9}">
                <template slot="content">
                    {{dataSource.remark}}
                </template>
            </el-description-item>
            <el-description-item value="sku" :span-map="{xl:2}"/>
            <el-description-item value="出库数量" :span-map="{xl:2}"/>
            <el-description-item value="出库价格" :span-map="{xl:2}"/>
            <el-description-item value="合计" :span-map="{xl:1}"/>
            <el-description-item value="系统订单号" :span-map="{xl:2}"/>
            <el-description-item value="ItemID" :span-map="{xl:1}"/>
            <el-description-item value="TransactionID" :span-map="{xl:2}"/>
            <el-description-item value="Seller" :span-map="{xl:2}"/>
            <el-description-item value="销售平台" :span-map="{xl:1}"/>
            <el-description-item value="备注" :span-map="{xl:8}"/>
            <div v-for="item in dataSource.outStockItems " :key="item.id">
                <el-description-item :span-map="{xl:2}">
                    <template slot="content">
                        {{item.sku}}
                    </template>
                </el-description-item>
                <el-description-item :span-map="{xl:2}">
                    <template slot="content">
                        {{item.quantity}}
                    </template>
                </el-description-item>
                <el-description-item :span-map="{xl:2}">
                    <template slot="content">
                        {{item.price}}
                    </template>
                </el-description-item>
                <el-description-item :span-map="{xl:1}">
                    <template slot="content">
                        {{item.price*item.quantity}}
                    </template>
                </el-description-item>
                <el-description-item :span-map="{xl:2}">
                    <template slot="content">
                        <div v-if="item.systemOrderId">{{item.systemOrderId}}</div>
                        <div v-else>无</div>
                    </template>
                </el-description-item>
                <el-description-item :span-map="{xl:1}">
                    <template slot="content">
                        <div v-if="item.platformOrderId">{{item.platformOrderId}}</div>
                        <div v-else>无</div>
                    </template>
                </el-description-item>
                <el-description-item :span-map="{xl:2}">
                    <template slot="content">
                        <div v-if="item.transationId">{{item.transationId}}</div>
                        <div v-else>无</div>
                    </template>
                </el-description-item>
                <el-description-item :span-map="{xl:2}">
                    <template slot="content">
                        <div v-if="item.account">{{item.account}}</div>
                        <div v-else>无</div>
                    </template>
                </el-description-item>
                <el-description-item :span-map="{xl:1}">
                    <template slot="content">
                        <div v-if="item.platform">{{platformMap[item.platform]}}</div>
                        <div v-else>无</div>
                    </template>
                </el-description-item>
                <el-description-item :span-map="{xl:8}">
                    <template slot="content">
                        {{item.remark}}
                    </template>
                </el-description-item>
            </div>
            <el-description-item value="-" :span-map="{xl:12}"/>
            <el-description-item label="出库SKU总个数" :span-map="{xl:3}">
                <template slot="content">
                    {{totalSku}}
                </template>
            </el-description-item>
            <el-description-item label="出库货物总个数" :span-map="{xl:3}">
                <template slot="content">
                    {{totalOutStock}}
                </template>
            </el-description-item>
            <el-description-item label="出库货物总额" :span-map="{xl:5}">
                <template slot="content">
                    {{totalPrice}}
                </template>
            </el-description-item>
        </el-description>
    </div>

</template>
<script>
    import ElDescription from './components/e_desc'
    import ElDescriptionItem from './components/e_desc_item'
    import {getOutStockDetail, getPlatformList} from '@/api/outstock'

    export default {
        name: 'OutStockEdit',
        components: {ElDescription, ElDescriptionItem}, // 引用组件变成标签el-description
        data() {
            return {
                dataSource: {outStockItems: []},
                totalSku: 0,
                totalPrice: 0,
                totalOutStock: 0,
                platformMap: {},
                dataExtra: {
                    outStockName: '', outStockType: '', warehouseName: '', outStockState: ''
                }
            }
        },
        created() {
            //获得平台列表
            getPlatformList().then(({data}) => {
                this.platformMap = data.reduce((pre, cur) => {
                    return {...pre, [cur.key]: cur.value}
                }, {})
            });
            // 获取出库表明细
            getOutStockDetail(this.$route.params.id).then(({data, extra}) => {
                Object.assign(this.dataExtra, extra);
                this.dataSource = data;
                //items计算
                this.dataSource.outStockItems.forEach(item => {
                    this.totalSku += item.quantity;
                    this.totalPrice += item.price * item.quantity;
                });
                this.totalOutStock = this.dataSource.outStockItems.length;
            });
        }
    }
</script>
