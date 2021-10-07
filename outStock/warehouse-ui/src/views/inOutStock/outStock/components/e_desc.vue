<template>
    <div class="descriptions">
        <div v-if="Boolean(title)" class="descriptions-title">{{ title }}</div>
        <div class="descriptions-view">
            <el-row class="descriptions-row">
                <slot v-if="$slots.default"/>
                <div v-else style="text-align: center; color: grey;">暂无数据</div>
            </el-row>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'ElDescription',
        props: {
            title: {
                type: String,
                required: false,
                default: ''
            }
        }
    }
</script>

<style scoped lang="scss">
    .descriptions {
        .descriptions-title {
            align-items: center;
            color: rgba(0, 0, 0, .85);
            font-size: 24px;
            font-weight: bolder;
            border: 2.5px solid;
            border-bottom: 0;
            text-align: center;
            padding-bottom: 6px;
            padding-top: 6px;
            width: 90%;
        }

        .descriptions-view {
            width: 90%;
            overflow: hidden;
            border: 2.5px solid;

            .descriptions-row {
                width: 110%;
            }
        }

    }
</style>