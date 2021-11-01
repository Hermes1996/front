<template>
    <el-form ref="searchForm" class="search-form" :model="model" @submit.native.prevent>
        <el-row :gutter="20">
            <slot></slot>
        </el-row>
        <el-row type="flex" class="mt20">
            <el-col :md="16" v-if="!$isMobile()">
                <el-button v-if="tableRef" @click="reverse">反选</el-button>
                <slot name="toolbar"></slot>
            </el-col>
            <el-col :md="8" class="tr">
                <el-button type="primary" native-type="submit" icon="el-icon-search" @click="search" :disabled="disabled">搜索</el-button>
                <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
            </el-col>
        </el-row>
        <div v-if="$isMobile()" class="mt20">
            <el-button v-if="tableRef" @click="reverse">反选</el-button>
            <slot name="toolbar"></slot>
        </div>
    </el-form>
</template>

<script>
    export default {
        name: 'SearchForm',
        props: {
            model: {
                required: true,
                type: Object
            },
            tableRef: {
                type: [String, Boolean],
                default: 'dataTable'
            },
            disabled: Boolean
        },
        methods: {
            // 搜索按钮
            search() {
                this.$refs.searchForm.validate().then(() => {
                    this.model.current = 1
                    this.$emit('search')
                })
            },
            // 重置按钮
            reset() {
                this.$refs.searchForm.resetFields()
                this.$emit('reset')
            },
            // 反选按钮
            reverse() {
                const tableRef = this.$parent.$refs[this.tableRef]
                if (tableRef) {
                    const column = tableRef.columns.find(c => {
                        return c.selectable != null
                    })
                    tableRef.data.forEach(row => {
                        if (!column || column.selectable(row)) {
                            tableRef.toggleRowSelection(row)
                        }
                    })
                }
                this.$emit('reverse')
            }
        }
    }
</script>