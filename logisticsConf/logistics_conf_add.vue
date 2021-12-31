<template>
  <submit-form @submit="createLogisticsConf" />
</template>

<script>
import { createLogisticsConf } from '@/api/logistics_conf'
import SubmitForm from './components/submit_form'

export default {
  name: 'LogisticsConfAdd',
  components: { SubmitForm },
  methods: {
    // 创建物流配置对象
    createLogisticsConf(logisticsConf) {
      createLogisticsConf(logisticsConf).then(() => {
        this.$success('创建成功')
        this.$router.push('/conf/core/list')
      })
    }
  }
}
</script>
