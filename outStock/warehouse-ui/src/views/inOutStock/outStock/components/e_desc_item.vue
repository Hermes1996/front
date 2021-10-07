<template>
    <el-col
            :span="span"
            :xs="spanMap.xs"
            :sm="spanMap.sm"
            :md="spanMap.md"
            :lg="spanMap.lg"
            :xl="spanMap.xl"
            class="descriptions-item"
    >
        <div class="descriptions-item-content">
            <div class="descriptions-item-label">
                <div v-if="label">{{ label }}:</div>
                <div v-else>{{ label }}</div>
            </div>
            <div class="descriptions-item-value">
                <slot v-if="$slots.content" name="content"/>
                <div v-else class="default-value" :title="value">{{ value }}</div>
            </div>
        </div>
    </el-col>
</template>

<script>
    export default {
        name: 'ElDescriptionItem',
        props: {
            spanMap: {
                type: Object,
                required: false,
                default: () => {
                    return {}
                }
            },
            span: {
                type: Number,
                required: false,
                default: 6
            },
            label: {
                required: false,
            },
            value: {
                required: false,
                default() {
                    return ''
                }
            },
            header: {
                required: false,
                default() {
                    return ''
                }
            }
        }

    }
</script>

<style scoped lang="scss">
    .descriptions-item {
        span {
            display: inline-block;
        }

        .descriptions-item-content {
            display: flex;
            justify-content: flex-start;
            align-items: center;
            color: rgba(0, 0, 0, .65);
            font-size: 14px;
            font-weight: bolder;
            line-height: 1.5;
            border: 2px solid;
            text-align: center;
            width: 100%;

            .descriptions-item-label {
                flex-grow: 0;
                flex-shrink: 0;
            }

            .descriptions-item-value {
                flex-grow: 1;
                overflow: hidden;

                .default-value {
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
            }
        }
    }
</style>
