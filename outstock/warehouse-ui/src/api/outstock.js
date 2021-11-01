import {request} from 'bessky-ui'
import ElementUI from 'element-ui';


export function getOutStockPageList(data) {
  return request({
    url: 'out/stocks/list/page',
    method: 'post',
    data
  });
}

export function getOutStock(id) {
  return request({
    url: 'out/stocks/' + id,
    method: 'get'
  })
}

export function createOutStock(data) {
  return request({
    url: 'out/stocks/create',
    method: 'post',
    data
  })
}

export function updateOutStock(data) {
  return request({
    url: 'out/stocks/update',
    method: 'post',
    data
  })
}

export function deleteOutStock(id) {
  return request({
    url: 'out/stocks/delete',
    method: 'post',
    params: {id}
  })
}

export function importOutStock(data) {
  return request({
    url: 'out/stocks/import',
    method: 'post',
    data
  })
}

export function getOutStockTypeList() {
  return request({
    url: 'out/stocks/type/list',
    method: 'get'
  });
}

export function getOutStockStateList() {
  return request({
    url: 'out/stocks/state/list',
    method: 'get'
  })
}

export function verifyOutStock(type, remark, temporaryWarehouseId) {

  // 临时仓库限制
  if (type == 3 || type == 5 || type == 9 ) {
    if (!temporaryWarehouseId) {
      ElementUI.Message({
        message: '库存清点、其它出库、包材出库类型出库,临时仓库不能为空',
        type: 'error'
      });
      return false;
    }
  }

  // 其他类型出库
  if (type == 5) {
    let count = 0;
    const regex = /[\u4E00-\u9FA5]/i;
    if (!remark) {
      ElementUI.Message({
        message: '其他类型出库,出库单备注必须输入两位以上中文',
        type: 'error'
      });
      return false;
    }
    for (let i = 0; i < remark.length; i++) {
      if (regex.test(remark.charAt(i))) {
        count += 1;
      }
    }
    if (count < 2) {
      ElementUI.Message({
        message: '其他类型出库,出库单备注必须输入两位以上中文',
        type: 'error'
      });
      return false;
    }
  }

  return true;
}
// this.$message.error使用失败；或者返回字符串，引用部分根据字符串报错
