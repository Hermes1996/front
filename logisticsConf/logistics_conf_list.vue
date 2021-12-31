<template>
  <div>
    <!-- 搜索表单 -->
    <search-form :model="query" :table-ref="false" @search="getLogisticsConfPageList">
      <search-form-item label="物流公司" prop="logisticCompany">
        <el-select v-model="query.logisticCompany" clearable filterable class="w100p" placeholder="请选择物流公司">
          <el-option v-for="item in dataSource.logisticCompanys" :key="item.id" :value="item.name" :label="item.name" />
        </el-select>
      </search-form-item>
      <search-form-item label="创建人" prop="createBy">
        <el-select v-model="query.createBy" clearable filterable class="w100p" placeholder="请选择创建人">
          <el-option v-for="item in dataSource.logisticUsers" :key="item.userId" :value="item.userId" :label="item.username + item.name" />
        </el-select>
      </search-form-item>
      <template v-slot:toolbar>
        <router-link v-permission="['5501010200']" to="/conf/core/add" class="ml10">
          <el-button type="primary" icon="el-icon-plus">添加</el-button>
        </router-link>
      </template>
    </search-form>

    <!-- 数据表格 -->
    <data-table ref="dataTable" v-loading="dataLoading" :data="page.records" row-key="logisticsId" stripe border highlight-current-row>
      <el-table-column prop="logisticsCompany" label="物流公司" width="300" />
      <el-table-column prop="userId" label="用户标识" />
      <el-table-column prop="userCode" label="用户编码" />
      <el-table-column prop="token" label="令牌" />
      <el-table-column prop="loginName" label="登陆名" />
      <el-table-column prop="loginPwd" label="登陆密码" />
      <el-table-column prop="clientId" label="客户端标识" />
      <el-table-column prop="clientSecret" label="客户端密钥" />
      <el-table-column prop="createByName" label="创建人" />
      <action-column text="编辑" link="/conf/core/edit/{logisticsId}" :disabled="$unauth('5501010300')">
        <template v-slot="{ row }">
          <el-dropdown-item @click.native="businessId = row.logisticsId">查看日志</el-dropdown-item>
        </template>
      </action-column>
    </data-table>

    <!-- 分页控件 -->
    <page :total="page.total" :size.sync="query.size" :current.sync="query.current" @change="getLogisticsConfPageList" />

    <!-- 查看日志 -->
    <view-log module="LOGISTICS_CONF" :business-id.sync="businessId" />
  </div>
</template>

<script>
import { getLogisticsConfPageList } from '@/api/logistics_conf'
import { getLogisticCompanyList } from '@/api/logistic_company'
import { getLogisticUserList } from '@/api/tail_contact_info'

export default {
  name: 'LogisticsConfList',
  data() {
    return {
      dataLoading: true,
      page: { records: [], total: 0 },
      query: {
        size: 10,
        current: 1,
        logisticCompany: '',
        createBy: undefined
      },
      dataSource: {
        logisticCompanys: [],
        logisticUsers: []
      },
      businessId: undefined,
    }
  },
  created() {
    // 获取物流公司列表
    getLogisticCompanyList().then(({ data }) => {
      this.dataSource.logisticCompanys = data
    })
    // 获取用户列表
    getLogisticUserList().then(({ data }) => {
      this.dataSource.logisticUsers = data
    })
  },
  activated() {
    // 获取物流配置列表
    this.getLogisticsConfPageList()
  },
  methods: {
    // 获取物流配置列表
    getLogisticsConfPageList() {
      this.dataLoading = true
      getLogisticsConfPageList(this.query).then(({ data }) => {
        this.page = data
        this.dataLoading = false
      })
    },
  }
}
</script>
