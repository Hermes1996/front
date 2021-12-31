<template>
  <el-form ref="submitForm" class="submit-form w-lg-50p" :model="logisticsConf" label-width="140px">
    <el-form-item label="物流公司" prop="logisticsCompany" verify>
      <el-select v-model="logisticsConf.logisticsCompany" :disabled="isUpdate" clearable filterable class="w100p" placeholder="请选择物流公司">
        <el-option v-for="item in dataSource.logisticCompanys" :key="item.id" :value="item.name" :label="item.name" />
      </el-select>
    </el-form-item>
    <el-form-item label="用户标识" prop="userId">
      <el-input v-model.trim="logisticsConf.userId" />
    </el-form-item>
    <el-form-item label="用户编码" prop="userCode">
      <el-input v-model.trim="logisticsConf.userCode" />
    </el-form-item>
    <el-form-item label="令牌" prop="token">
      <el-input v-model.trim="logisticsConf.token" />
    </el-form-item>
    <el-form-item label="登陆名" prop="loginName">
      <el-input v-model.trim="logisticsConf.loginName" />
    </el-form-item>
    <el-form-item label="登陆密码" prop="loginPwd">
      <el-input v-model.trim="logisticsConf.loginPwd" />
    </el-form-item>
    <el-form-item label="客户端标识" prop="clientId">
      <el-input v-model.trim="logisticsConf.clientId" />
    </el-form-item>
    <el-form-item label="客户端密钥" prop="clientSecret">
      <el-input v-model.trim="logisticsConf.clientSecret" />
    </el-form-item>
    <el-form-item label="编码" prop="code">
      <el-input v-model.trim="logisticsConf.code" />
    </el-form-item>
    <el-form-item label="重定向地址" prop="redirectUri">
      <el-input v-model.trim="logisticsConf.redirectUri" />
    </el-form-item>
    <el-form-item label="刷新令牌" prop="refreshToken">
      <el-input v-model.trim="logisticsConf.refreshToken" />
    </el-form-item>
    <el-form-item label="订单编号前缀" prop="orderNoPrefix">
      <el-input v-model.trim="logisticsConf.orderNoPrefix" />
    </el-form-item>
    <el-form-item label="打印类型" prop="printType">
      <el-input v-model.trim="logisticsConf.printType" />
    </el-form-item>
    <el-form-item label="寄件人姓名" prop="fromContacter">
      <el-input v-model.trim="logisticsConf.fromContacter" />
    </el-form-item>
    <el-form-item label="寄件人邮编" prop="fromPostCode">
      <el-input v-model.trim="logisticsConf.fromPostCode" />
    </el-form-item>
    <el-form-item label="寄件人电话" prop="fromTel">
      <el-input v-model.trim="logisticsConf.fromTel" />
    </el-form-item>
    <el-form-item label="寄件人国家" prop="fromCountry">
      <el-input v-model.trim="logisticsConf.fromCountry" />
    </el-form-item>
    <el-form-item label="寄件人国家简码" prop="fromCountryCode">
      <el-input v-model.trim="logisticsConf.fromCountryCode" />
    </el-form-item>
    <el-form-item label="寄件人省份" prop="fromProvince">
      <el-input v-model.trim="logisticsConf.fromProvince" />
    </el-form-item>
    <el-form-item label="寄件人城市" prop="fromCity">
      <el-input v-model.trim="logisticsConf.fromCity" />
    </el-form-item>
    <el-form-item label="寄件人公司" prop="fromCompany">
      <el-input v-model.trim="logisticsConf.fromCompany" />
    </el-form-item>
    <el-form-item label="寄件人街道1" prop="fromStreet1">
      <el-input v-model.trim="logisticsConf.fromStreet1" />
    </el-form-item>
    <el-form-item label="寄件人街道2" prop="fromStreet2">
      <el-input v-model.trim="logisticsConf.fromStreet2" />
    </el-form-item>
    <el-form-item label="寄件人邮箱" prop="fromEmail">
      <el-input v-model.trim="logisticsConf.fromEmail" />
    </el-form-item>
    <submit-button to="/conf/core/list" @submit="handleSubmit" />
  </el-form>
</template>

<script>
import { getLogisticsConf } from '@/api/logistics_conf'
import { getLogisticCompanyList } from '@/api/logistic_company'

export default {
  props: {
    isUpdate: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dataSource: {
        logisticCompanys: []
      },
      logisticsConf: {
        logisticsCompany: '',
        userId: '',
        userCode: '',
        token: '',
        loginName: '',
        loginPwd: '',
        clientId: '',
        clientSecret: '',
        code: '',
        redirectUri: '',
        refreshToken: '',
        orderNoPrefix: '',
        printType: '',
        fromContacter: '',
        fromPostCode: '',
        fromTel: '',
        fromCountry: '',
        fromCountryCode: '',
        fromProvince: '',
        fromCity: '',
        fromCompany: '',
        fromStreet1: '',
        fromStreet2: '',
        fromEmail: ''
      },
    }
  },
  created() {
    // 获取物流公司列表
    getLogisticCompanyList().then(({ data }) => {
      this.dataSource.logisticCompanys = data
    })
    // 获取物流配置对象
    if (this.$route.params.id) {
      getLogisticsConf(this.$route.params.id).then(({ data }) => {
        this.logisticsConf = data
      })
    }
  },
  methods: {
    // 处理提交
    handleSubmit() {
      this.$refs.submitForm.validate().then(() => {
        this.$emit('submit', this.logisticsConf)
      })
    },
  }
}
</script>
