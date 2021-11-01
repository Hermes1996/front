<template>
    <submit-form ref="addForm" @submit="createOutStock" />
</template>

<script>
    import {createOutStock} from '@/api/outstock'
    import SubmitForm from './components/submit_form'

    export default {
        components: {SubmitForm},
        methods: {
            // 新增出库单
            createOutStock(outStockCustom) {
                createOutStock(outStockCustom).then(() => {
                    this.$refs.addForm.$refs.outStockForm.resetFields();
                    this.$notify.success({title: '成功', message: '成功新增出库单'});
                    this.$router.push('/inOutStock/outStock/list');
                    this.$router.go(0)
                })
            }
        }
    }
</script>
