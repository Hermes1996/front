<template>
    <div>
        <!-- 搜索表单 -->
        <search-form :model="query" @search="getOutStockPageList">
            <search-form-item label="出库人" prop="createdBy" :lg="3">
                <el-select v-model="query.createdBy" clearable filterable class="w100p" placeholder="请选择出库人">
                    <el-option v-for="item in dataSource.users" :key="item.userId" :value="item.userId"
                               :label="item.name"/>
                </el-select>
            </search-form-item>
            <search-form-item label="仓库" prop="warehouseId" :lg="3">
                <el-select v-model="query.warehouseId" clearable filterable class="w100p" placeholder="请选择所属仓库">
                    <el-option v-for="warehouse in dataSource.warehouses" :key="warehouse.warehouseId"
                               :value="warehouse.warehouseId" :label="warehouse.warehouseName"/>
                </el-select>
            </search-form-item>
            <search-form-item label="出库类型" prop="type" :lg="3">
                <el-select v-model="query.type" clearable filterable class="w100p" placeholder="请选择库位类型">
                    <el-option v-for="item in dataSource.types" :key="item.key" :value="item.key" :label="item.value"/>
                </el-select>
            </search-form-item>
            <search-form-item label="出库状态" prop="state" :lg="3">
                <el-select v-model="query.state" clearable filterable class="w100p" placeholder="请选择库位状态">
                    <el-option v-for="item in dataSource.states" :key="item.key" :value="item.key" :label="item.value"/>
                </el-select>
            </search-form-item>
            <search-form-item label="SKU" prop="skuId">
                <el-input v-model.trim="query.skuId" placeholder="多个SKU请用英文逗号隔开"/>
            </search-form-item>
            <search-form-item label="出库时间" prop="creationDateRange" :lg="6" :md="12" :sm="16" :xs="24">
                <el-date-picker
                        v-model="query.creationDateRange"
                        type="datetimerange"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        :default-time="['00:00:00', '23:59:59']"
                        class="w100pi"
                />
            </search-form-item>
            <search-form-item label="流水号" prop="likeOutId" :lg="6">
                <el-input v-model.trim="query.likeOutId" placeholder="多个流水号请用英文逗号隔开"/>
            </search-form-item>
            <template v-slot:toolbar>
                <!--                直接请求js地址-->
                <router-link v-permission="['5405060300']" to="/inOutStock/outStock/add">
                    <el-button type="primary" class="el-icon--right">新建出库单</el-button>
                </router-link>
                <el-dropdown trigger="click" class="ml10">
                    <el-button>
                        批量操作
                        <i class="el-icon-arrow-down el-icon--right"/>
                    </el-button>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item v-permission="['5405010400']" @click.native="toBatchImportOutStock">批量导入出库单
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
                <el-button v-permission="['5405010400']" class="el-icon--right" @click="exportOutStock">导出出库单
                </el-button>
            </template>
        </search-form>

        <!-- 数据表格 -->
        <el-table ref="dataTable" v-loading="dataLoading" class="data-table expand" :data="page.records" stripe border
                  highlight-current-row>
            <el-table-column type="selection" width="51"/>
            <el-table-column prop="id" label="流水号" width="160"/>
            <!--            el-table绑定data,prop可起到绑定作用-->
            <el-table-column prop="createTime" label="出库时间" width="170"/>
            <el-table-column prop="createdBy" label="出库人" width="160">
                <!--                data相当于一个二维列；通过slot-scope可以获取到 row, column, $index 和 store（table 内部的状态管理）的数据-->
                <!--                对象的写法：<template slot-scope="scope">-->
                <!--                    <span >{{ scope.row.date }}</span>-->
                <template slot-scope="{ row }">{{ dataSource.userMap[row.createdBy] }}</template>
            </el-table-column>
            <el-table-column prop="type" label="出库类型" width="180">
                <template slot-scope="{ row }">{{ dataSource.typesMap[row.type] }}</template>
            </el-table-column>
            <el-table-column prop="warehouseId" label="源仓库" width="160">
                <template slot-scope="{ row }">{{ dataSource.warehouseMap[row.warehouseId] }}</template>
            </el-table-column>
            <el-table-column prop="targetWarehouse" label="目的仓库" width="160">
                <template slot-scope="{ row }">{{ dataSource.warehouseMap[row.warehouseId] }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="出库单备注" width="400"/>
            <el-table-column prop="state" label="出库状态" width="160">
                <template slot-scope="{ row }">{{ dataSource.stateMap[row.state] }}</template>
            </el-table-column>
            <el-table-column label="操作" width="280">
                <template slot-scope="{ row }">
                    <span v-if="row.state == 1">
                        <router-link v-permission="['5405060300']" :to="'/inOutStock/outStock/edit/'+row.id">
                            <el-button type="primary" size="mini">编辑</el-button>
                        </router-link>
                        <el-button v-permission="['5405050400']" size="mini" type="danger" @click="deleteOutStock(row)">
                            删除
                        </el-button>
                    </span>
                    <el-dropdown trigger="click" size="medium" class="ml10">
                        <el-button size="mini">
                            操作
                            <i class="el-icon-arrow-down el-icon--right"/>
                        </el-button>
                        <el-dropdown-menu slot="dropdown">
                            <router-link v-permission="['5405060300']" :to="'/inOutStock/outStock/view/' + row.id">
                                <el-dropdown-item>查看明细</el-dropdown-item>
                            </router-link>
                            <el-dropdown-item @click.native="businessId = row.id">日志</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </template>
            </el-table-column>
        </el-table>

        <!-- 批量导入出库表单 -->
        <el-dialog :visible.sync="dialogVisible" title="批量导入出库表" width="30%">
            <!--            visible.sync可视化bool控制-->
            <el-form ref="outStockForm" :model="outStock" label-width="80px">
                <el-form-item label="出库类型" prop="type" verify>
                    <el-select v-model="outStock.type" clearable filterable class="w100p" placeholder="请选择库位类型">
                        <el-option v-for="item in dataSource.types" :key="item.key" :value="item.key"
                                   :label="item.value"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="源仓库" prop="warehouseId" verify>
                    <el-select v-model="outStock.warehouseId" clearable filterable class="w100p" placeholder="请选择所属仓库">
                        <el-option v-for="warehouse in dataSource.warehouses" :key="warehouse.warehouseId"
                                   :value="warehouse.warehouseId" :label="warehouse.warehouseName"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="目的仓库">
                    <el-select v-model="outStock.targetWarehouse" clearable filterable class="w100p"
                               placeholder="请选择所属仓库">
                        <el-option v-for="warehouse in dataSource.warehouses" :key="warehouse.warehouseId"
                                   :value="warehouse.warehouseId" :label="warehouse.warehouseName"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model.trim="outStock.remark" placeholder="请输入出库单备注" maxlength="100"/>
                </el-form-item>
                <el-form-item label="文件">
                  <el-link href="/warehouse/file/ImportOutStockTemplate.xls"
                           target="_blank">批量导入出库单模板下载
                  </el-link>
                    <el-upload
                            action="*"
                            ref="upload"
                            accept=".xls,.xlsx"
                            :limit="1"
                            :file-list="files"
                            :auto-upload="false"
                            :before-upload="beforeUpload"
                            :before-remove="beforeRemove"
                            :on-exceed="handleExceed ">
                        <!--                        文件上传一般传入服务器，这里需要关闭自动上传；让界面文件列表文件滞留-->
                        <el-tooltip class="item" effect="dark" content="只能上传excel文件" placement="top-start">
                            <el-button size="small" type="primary">选取文件</el-button>
                        </el-tooltip>
                    </el-upload>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible= false">取消</el-button>
                <el-button type="primary" @click="batchImportOutStock()">确定</el-button>
            </div>
        </el-dialog>

        <!-- 分页控件 -->
        <page :total="page.total" :size.sync="query.size" :current.sync="query.current" @change="getOutStockPageList"/>

        <!-- 查看日志 -->
        <view-log module="OUT_STOCK" :business-id.sync="businessId"/>
    </div>
</template>

<script>
    import {
        getOutStockPageList,
        getUsernameList,
        getOutTypeList,
        getOutStatusList,
        deleteOutStock,
        batchImportOutStock,
        exportOutStock,
    } from '@/api/outstock'
    import {getAuthWarehouseList} from '@/api/warehouse'


    export default {
        name: 'OutStockList',
        data() {
            return {
                dataLoading: true,
                page: {records: [], total: 0},
                query: {
                    size: 10,
                    current: 1,
                    creationDateRange: [],
                },
                dataSource: {
                    warehouses: [],
                    warehouseMap: {},
                    users: [],
                    userMap: {},
                    states: [],
                    stateMap: {},
                    types: [],
                    typesMap: {}
                },
                outStock: {
                    warehouseId: undefined,
                    type: undefined,
                    targetWarehouse: undefined,
                    remark: '',
                },
                files: [],
                businessId: undefined,
                dialogVisible: false
            }
        },
        created() {
            // 获取出库人列表
            getUsernameList().then(({data}) => {
                this.dataSource.users = data;
                this.dataSource.userMap = data.reduce((pre, cur) => {
                    return {...pre, [cur.userId]: cur.name}
                }, {})
                // reduce必须传初始值pre;初始值为{}，...pre展开
            });
            // 获取仓库列表
            getAuthWarehouseList().then(({data}) => {
                this.dataSource.warehouses = data;
                this.dataSource.warehouseMap = data.reduce((pre, cur) => {
                    return {...pre, [cur.warehouseId]: cur.warehouseName}
                }, {})
            });
            // 获取出库类型
            getOutTypeList().then(({data}) => {
                this.dataSource.types = data;
                this.dataSource.typesMap = data.reduce((pre, cur) => {
                    return {...pre, [cur.key]: cur.value}
                }, {})
            });
            // 获取出库状态
            getOutStatusList().then(({data}) => {
                this.dataSource.states = data;
                this.dataSource.stateMap = data.reduce((pre, cur) => {
                    return {...pre, [cur.key]: cur.value}
                }, {})
            });
            this.getOutStockPageList();
        },
        methods: {
            // 获取出库单列表
            getOutStockPageList() {
                this.dataLoading = true;
                const query = this.query;
                const [fromCreateTime, toCreationTime] = query.creationDateRange;
                getOutStockPageList({...query, fromCreateTime, toCreationTime}).then(({data}) => {
                    this.dataLoading = false;
                    this.page = data
                })
            },
            // 删除库位
            deleteOutStock(row) {
                this.$confirm('确认删除选中库位？', {
                    type: 'warning'
                }).then(() => {
                    deleteOutStock(row.id).then(() => {
                        this.$notify.success({title: '成功', message: '删除成功'});
                        this.getOutStockPageList()
                    })
                })
            },
            toBatchImportOutStock() {
                this.dialogVisible = true;
                this.$nextTick(() => {
                    this.$refs.outStockForm.resetFields()
                })
            },
            // 批量导入出库单
            batchImportOutStock() {
                this.$refs.upload.submit();
                this.$refs.outStockForm.validate(valid => {
                    // 文件个数
                    if (this.files.length === 0) {
                        this.$message.error('请至少上传一个文件');
                        return
                    }//valid前为verify验证，valid后为业务验证
                    if (valid) {
                        //ref相当于id
                        if (this.files.length === 0) {
                            this.$notify.warning({title: '警告', message: '请至少上传一个文件'});
                            return
                        }
                        const formData = new FormData();
                        formData.append('file', this.files[0]);
                        formData.append('outStock', JSON.stringify(this.outStock));
                        //对象安全的转string
                        batchImportOutStock(formData).then(() => {
                            this.dialogVisible = false;
                            this.$notify.success({title: '成功', message: '创建成功'});
                            this.getOutStockPageList()
                        })
                    }
                })
            },
            // 导出出货单
            exportOutStock() {
                if (this.$refs.dataTable.selection.length === 0) {
                    this.$notify.warning({title: '警告', message: '请选择操作数据'});
                    return
                }
                const ids = this.$refs.dataTable.selection.map(v => v['id']).join(',');
                exportOutStock(ids).then(() => {
                    this.$notify.success({title: '成功', message: '导出出库单成功'});
                    this.getLocationAreaPageList()
                })
            },
            // 选择文件
            handleExceed() {
                this.$message.warning(`当前限制选择1个文件`);
            },
            beforeRemove(file) {
                // 固定参数绑定的方法，可获取到file
                return this.$confirm(`确定移除 ${file.name}?`);
            },
            beforeUpload(file) {
                this.files.push(file);
            }
        }
    }
</script>

