<template>
    <el-form ref="outStockForm" class="submit-form w-lg-50p" :model="outStock" label-width="80px">
        <el-form-item label="出库类型" prop="type" verify>
            <el-select v-model="outStock.type" clearable filterable class="w100p" placeholder="请选择库位类型">
                <el-option v-for="item in dataSource.types" :key="item.key" :value="item.key" :label="item.value"/>
            </el-select>
        </el-form-item>
        <el-form-item label="源仓库" prop="warehouseId" verify>
            <el-select v-model="outStock.warehouseId" clearable filterable class="w100p" placeholder="请选择所属仓库">
                <el-option v-for="warehouse in dataSource.warehouses" :key="warehouse.warehouseId"
                           :value="warehouse.warehouseId" :label="warehouse.warehouseName"/>
            </el-select>
        </el-form-item>
        <el-form-item label="目的仓库" prop="targetWarehouse">
            <el-select v-model="outStock.targetWarehouse" clearable filterable class="w100p" placeholder="请选择目的仓库">
                <el-option v-for="warehouse in dataSource.warehouses" :key="warehouse.warehouseId"
                           :value="warehouse.warehouseId" :label="warehouse.warehouseName"/>
            </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
            <el-input v-model.trim="outStock.remark" placeholder="请输入出库单备注" maxlength="100"/>
        </el-form-item>
        <div v-for="(outStockItem,index) in outStock.outStockItems" :key="index">
            <el-form-item label="SKU" :prop="`outStockItems.${index}.sku`"  verify>
                <!--                ES6之模版字符串，prop必须加"",相当于"'outStockItems.'+index+'.sku'" -->
                <el-input v-model.trim="outStockItem.sku" placeholder="请输入SKU" maxlength="15"/>
            </el-form-item>
            <el-form-item label="数量" :prop="`outStockItems[${$index}].quantity`" verify int :gt="0">
<!--                element-ui-verify使用-->
                <el-tooltip effect="light" placement="top">
                    <div slot="content">
                        <el-input v-model="dataSource.maxSkuQualityStr" :disabled="true" style="font-size: 10px"/>
                    </div>
                    <el-input v-model.trim="outStockItem.quantity" placeholder="请输入SKU数量" maxlength="10"/>
                </el-tooltip>
            </el-form-item>
            <el-form-item label="SKU备注"  :prop="`outStockItems.${index}.remark`" verify>
                <el-input v-model.trim="outStockItem.remark" placeholder="请输入SKU备注" maxlength="100"/>
            </el-form-item>
        </div>
        <div class="ml100 mb20">
            <el-table :data="outStock.outStockItems" border>
                <el-table-column label="SKU列表">
                    <template slot-scope="{ row, $index }">
<!--                        $index固定写法-->
                        <el-row :gutter="4">
                            <el-col :span="12">
                                <el-form-item label="SKU" :prop="'outStockItems[' + $index + '].sku'" verify>
<!--        "`outStockItems[${$index}].sku`"-->
                                    <el-input v-model.trim="row.sku" placeholder="请输入SKU" maxlength="15" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="数量" :prop="'outStockItems[' + $index + '].quantity'" verify
                                              :max-decimal-length="3">
                                    <el-tooltip effect="light" placement="top">
                                        <div slot="content">
                                            <el-input v-model="dataSource.maxSkuQualityStr" :disabled="true" style="font-size: 10px" />
                                        </div>
                                        <el-input v-model.trim="row.quantity" placeholder="请输入SKU数量" maxlength="10" />
                                    </el-tooltip>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="SKU备注" :prop="'outStockItems[' + $index + '].remark'" verify>
                                    <el-input v-model.trim="row.remark" placeholder="请输入SKU备注" maxlength="100" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" class-name="tdvat">
                    <template slot-scope="{ $index }">
                        <el-button v-if="$index == 0" @click="addOutStockItem">添加</el-button>
                        <el-button v-else type="danger" @click="deleteOutStockItem($index)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <el-form-item>
            <el-button type="primary" @click="handleSubmit">提交</el-button>
            <router-link to="/inOutStock/outStock/list" class="ml10">
                <el-button>返回</el-button>
            </router-link>
        </el-form-item>
    </el-form>
</template>

<script>
    import {
        getOutStock,
        getOutTypeList,
        getOutStatusList,
        toCreateOrUpdateOutStock,
    } from '@/api/outstock'
    import {getAuthWarehouseList} from '@/api/warehouse'

    export default {
        data() {
            return {
                outStock: {
                    outStockItems: [{
                        remark: '',
                        sku: '',
                        quantity:undefined
                    }]
                },
                dataSource: {
                    warehouses: [],
                    states: [],
                    types: [],
                    maxSkuQuality: undefined,
                    maxSkuQualityStr: "",
                },
            }
        },
        created() {
            // 获取出库单
            if (this.$route.params.id) {
                getOutStock(this.$route.params.id).then(({data}) => {
                    this.outStock = data;
                });
            }
            // 获取仓库列表
            getAuthWarehouseList().then(({data}) => {
                this.dataSource.warehouses = data;
            });
            // 获取出库类型
            getOutTypeList().then(({data}) => {
                this.dataSource.types = data;
            });
            // 获取出库状态
            getOutStatusList().then(({data}) => {
                this.dataSource.states = data;
            });
            // 额外数据获取
            toCreateOrUpdateOutStock().then(({extra}) => {
                this.dataSource.maxSkuQuality = extra.maxSkuQuality;
                this.dataSource.maxSkuQualityStr = "最大数量：" + this.dataSource.maxSkuQuality;
            })
        },
        methods: {
            // 添加sku
            addOutStockItem() {
                this.outStock.outStockItems.push({
                    remark: '',
                    sku: '',
                    quantity: undefined
                })
            },
            // 删除sku
            deleteOutStockItem(index) {
                this.outStock.outStockItems.splice(index, 1)
            },
            // 提交表单
            handleSubmit() {
                this.$refs.outStockForm.validate().then((valid) => {
                    // 出库类型限制
                    let outType = this.outStock.type;
                    if (outType == 5 || outType == 3 || outType == 9) {
                        const temporaryWarehouseId = this.outStock.temporaryWarehouseId;
                        if (typeof (temporaryWarehouseId) === 'undefined') {
                            this.$message.error('库存清点、其它出库、包材出库类型出库,临时仓库不能为空');
                            return false;
                        }
                    }

                    let skuRemarkList = [];
                    let outStockItems = this.outStock.outStockItems;
                    outStockItems.forEach(outStockItem => {
                        skuRemarkList.push(outStockItem.remark)
                    });
                    // 其他类型
                    if (outType == 5) {
                        const regex = /[\u4E00-\u9FA5]/i;
                        let count = 0;
                        const remark = this.outStock.remark;
                        if (remark.length == 0) {
                            this.$message.error('其他类型出库,出库单备注必须输入两位以上中文');
                            return false;
                        }
                        for (let i = 0; i < remark.length; i++) {
                            if (regex.test(remark.charAt(i))) {
                                count = count + 1;
                            }
                        }
                        if (count < 2) {
                            this.$message.error('其他类型出库,出库单备注必须输入两位以上中文');
                            return false;
                        }
                        for (let skuRemark of skuRemarkList) {
                            let skuCount = 0;
                            for (let i = 0; i < skuRemark.length; i++) {
                                if (regex.test(skuRemark.charAt(i))) {
                                    skuCount = skuCount + 1;
                                }
                            }
                            if (skuCount < 2) {
                                this.$message.error('其他类型出库,sku备注必须输入两位以上中文');
                                return false;
                            }
                        }
                    }
                    if (valid) {
                        this.$emit('submit', this.outStock)
                    }
                })
            },
        }
    }
</script>
