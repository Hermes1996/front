<template>
    <submit-form  :is-update="true" @submit="updateOutStock" />
</template>

<script>
    import { updateOutStock } from '@/api/outstock'
    import SubmitForm from './components/submit_form'

    export default {
        name: 'OutStockEdit',
        components: {SubmitForm},
        methods: {
            // 修改出库单
            updateOutStock(outStockCustom) {
                updateOutStock(outStockCustom).then(() => {
                    this.$notify.success({title: '成功', message: '修改成功'});
                    this.$router.push('/inOutStock/outStock/list')
                })
            }
        }
    }
</script>
