<template>
  <submit-form is-update @submit="updateLogisticsConf" />
</template>

<script>
import { updateLogisticsConf } from '@/api/logistics_conf'
import SubmitForm from './components/submit_form'

export default {
  name: 'LogisticsConfEdit',
  components: { SubmitForm },
  methods: {
    // 修改物流配置对象
    updateLogisticsConf(logisticsConf) {
      updateLogisticsConf(logisticsConf).then(() => {
        this.$success('修改成功')
        this.$router.push('/conf/core/list')
      })
    }
  }
}
</script>