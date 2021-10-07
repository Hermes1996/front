<template>
    <el-col v-bind="bind">
        <el-form-item :label="label" :prop="prop" v-bind="$attrs">
            <div v-if="and != null || not != null" class="and-not">
                <el-checkbox v-if="and != null" v-model="andModel">全</el-checkbox>
                <el-checkbox v-if="not != null" v-model="notModel">反</el-checkbox>
            </div>
            <slot></slot>
        </el-form-item>
    </el-col>
</template>

<script>
    export default {
        name: 'SearchFormItem',
        inheritAttrs: false,
        props: {
            span: Number,
            xs: Number,
            sm: Number,
            md: Number,
            lg: Number,
            xl: Number,
            label: {
                required: true,
                type: String
            },
            prop: {
                required: true,
                type: String
            },
            and: {
                type: Boolean,
                default: undefined
            },
            not: {
                type: Boolean,
                default: undefined
            }
        },
        computed: {
            bind() {
                const span = this.span
                const xs = this.xs
                const sm = this.sm
                const md = this.md
                const lg = this.lg
                const xl = this.xl
                if (span || xs || sm || md || lg || xl) {
                    return { span, xs, sm, md, lg, xl }
                }
                return { xs: 12, sm: 8, md: 6, lg: 4 }
            },
            andModel: {
                get() {
                    return this.and
                },
                set(and) {
                    this.$emit('update:and', and)
                }
            },
            notModel: {
                get() {
                    return this.not
                },
                set(not) {
                    this.$emit('update:not', not)
                }
            }
        }
    }
</script>

<style scoped>
    .and-not {
        position: absolute;
        top: 0;
        right: 0;
    }
    .and-not .el-checkbox {
        margin-right: 10px;
    }
    .and-not .el-checkbox:last-of-type {
        margin-right: 0;
    }
    .and-not ::v-deep .el-checkbox__label {
        padding-left: 5px;
    }
</style>