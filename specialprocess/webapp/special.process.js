function initList() {
  App.init();

  // 渐显
  $(window).scroll(function (e) {
    if ($(window).scrollTop() > 100) {
      $("#d-top").fadeIn(500);
    } else {
      $("#d-top").fadeOut(500);
    }
  });

  // 点击回到顶部的元素
  $("#d-top").click(function (e) {
    // 以1秒的间隔返回顶部
    $('body, html').animate({
      scrollTop: 0
    });
  });

  // 子选择
  ruleIds.change(function () {
    // 行变颜色
    var $this = $(this);
    var tr = $this.closest("tr");
    if ($this.prop("checked")) {
      tr.find("td").css("background-color", "#B1DB87");
    } else {
      tr.find("td").css("background-color", "");
    }
  });

  // 下拉复选框
  $("#state").multiSelect({
    oneOrMoreSelected: '*',
    selectAll: false
  });

  // 下拉复选框
  $("#warehouseId").multiSelect({
    oneOrMoreSelected: '*',
    selectAll: false
  });
}

// 全选
var ruleIds = $("#data-table").find("input[name='specialProcessIds']");
function checkAllItems() {
  ruleIds.each(function() {
    $(this).attr("checked", "checked");
    $(this).closest("tr").find("td").css("background-color", "#B1DB87");
  })
}
// 反选
function checkNegative() {
  ruleIds.each(function() {
    if ($(this).attr("checked")) {
      $(this).removeAttr("checked");
      $(this).closest("tr").find("td").css("background-color", "");
    } else {
      $(this).attr('checked', 'checked');
      $(this).closest("tr").find("td").css("background-color", "#B1DB87");
    }
  })
}

function getCheckedOrders() {
  var checkedOrders = $("#data-table").find('input[name="specialProcessIds"]:checked');
  return checkedOrders;
}

// 批量驳回
function batchReject(specialProcessId) {
  var param = "&specialProcessIds=" + specialProcessId;

  if (specialProcessId == -1) {
    var checkedOrders = getCheckedOrders();

    if (checkedOrders.length == 0) {
      $.error("至少选择一条数据");
      return;
    }
    param = checkedOrders.serialize();
  }

  var diglog = dialog({
    id : "confirm-diglog",
    title : "驳回操作",
    width : 280,
    content : '驳回原因<br><textarea  rows="3" cols="33" id="rejectReason"  maxlength="254"/>',
    okValue : '确认',
    ok : function() {
      var rejectReason = $("#rejectReason").val();

      $.postJson(CONTEXT_PATH + "special/process/approvals/reject/batch?" + param + "&rejectReason=" + rejectReason, function(data) {
        if (data.status != 200) {
          $.error(data.message);
        } else {
          $.success("操作成功", function() {
            window.location.reload();
          });
        }
      });
    },
    cancelValue : '取消',
    cancel : function() {
    },
  });
  diglog.showModal();
}

// 批量废弃
function batchDiscard() {
  var checkedOrders = getCheckedOrders();

  if (checkedOrders.length == 0) {
    $.error("至少选择一条数据");
    return;
  }

  var param = checkedOrders.serialize();

  var diglog = dialog({
    id : "confirm-diglog",
    title : "批量废弃",
    width : 280,
    content : '废弃原因<br><textarea  rows="3" cols="33" id="rejectReason"  maxlength="254"/>',
    okValue : '确认',
    ok : function() {
      var rejectReason = $("#rejectReason").val();

      $.postJson(CONTEXT_PATH + "special/process/approvals/discards/batch?" + param + "&rejectReason=" + rejectReason, function(data) {
        if (data.status != 200) {
          $.error(data.message);
        } else {
          $.success("操作成功", function() {
            window.location.reload();
          });
        }
      });
    },
    cancelValue : '取消',
    cancel : function() {
    },
  });
  diglog.showModal();
}

// 更新特殊加工状态
function updateState(specialProcessId, targetState) {
  var name = "";
  var reason = null;
  if (targetState == 2) {
    name = "是否移采购经理审批?";
  } else if (targetState == 3) {
    name = "是否移仓库经理审批?";
  } else if (targetState == 4) {
    name = "是否完成?";
  } else if (targetState == 5) {
    var diglog = dialog({
      id: "paid-diglog",
      title: '废弃审批单',
      width: 300,
      height: 30,
      content: '原因 <span class="required">*</span><input class="form-control" id="reason" />',
      okValue: '确认',
      ok: function () {
        if (confirm("是否废弃?")) {
          reason = $("#reason").val();
          if (!reason) {
            $.error("请填写原因");
            return;
          }
          $.postJson(CONTEXT_PATH + "special/process/approvals/updateState", {
            specialProcessId: specialProcessId,
            targetState: targetState,
            reason: reason
          }, function () {
            $.successTip("操作成功", function () {
              $("#search-form").submit();
              return true;
            });
          });
        }
      },
      cancelValue: '取消',
      cancel: function () {
      },

    });
    diglog.showModal();
    return
  }

  $.confirm(name, function () {
    $.postJson(CONTEXT_PATH + "special/process/approvals/updateState", {
      specialProcessId: specialProcessId,
      targetState: targetState,
    }, function () {
      $.successTip("操作成功", function () {
        $("#search-form").submit();
        return true;
      });
    });
  });
}

// 修改备注
$('#data-table .location-remark').editable({
  url : CONTEXT_PATH + 'special/process/approvals/quick/update',
  type : 'text',
  params : function(params) {
    var customParams = {
      'specialProcess.remark' : params.value,
      'specialProcess.id' : params.pk
    };
    $.extend(params, customParams);
    return params;
  }
});

// 导出excel
$("#export").exportExcel({
  url: CONTEXT_PATH + "special/process/approvals/export",
  formId: "submit-form",
  checkedName: "specialProcessIds"
});

// 特殊加工审批日志
function viewLog(specialProcessId) {
  var diglog = dialog({
    title : '特殊加工审批日志',
    width : 800,
    url : CONTEXT_PATH + "special/process/approvals/log?specialProcessId=" + specialProcessId,
    cancelValue : '取消',
    cancel : function() {
    }
  });
  diglog.show();
}