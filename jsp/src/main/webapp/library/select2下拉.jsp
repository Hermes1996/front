<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<html lang="en" class="no-js">

<div id="input_orderid">
    <label>订单号或跟踪号</label>
    <div><input type="text" class="form-control input-medium" name="orderid" id="orderid" onkeypress="if(event.keyCode==13) { inputnext(this); return false;}" tabindex="4"></div>
</div>
<input type="text" placeholder="跟踪号截取" class="form-control input-medium" id="substring-rule" tabindex="4">

<app:include type="javascript"/>

<script type="text/javascript" src="${CUSTOM_THEME_PATH }js/pages/trackingNumber.subs.new.js"></script>

<script language="javascript">

  // 全局diglog
  var diglog;

  $(document).ready(function () {
    var businessids_data = [{id: "", text: "不截取"}, {id: "8", text: "从第8位开始截取"}, {id: "+12", text: "截取前 12位"}, {
      id: "-4",
      text: "去除后4位"
    }]
    $("#substring-rule").select2({
      createSearchChoice: function (term, data) {
        if ($(data).filter(function () {
          return this.text.localeCompare(term) === 0;
        }).length === 0) {
          return {id: term, text: term};
        }
      },
      multiple: false,
      data: businessids_data
    });

  }); // end ready


  //扫描区数据提交
  function input_submit() {
    // 跟踪号去除空格
    var trackingNumber = $.trim($("#orderid").val());

    $("#orderid").val(trackingNumber);

    // 跟踪号截取
    var rule = $("#substring-rule").val();
    trackingNumberSubsNew(trackingNumber, rule);
  }

  function trackingNumberSubsNew(trackingNumber, rule) {
    if (rule) {
      if (rule.indexOf('+') != -1) {
        trackingNumber = trackingNumber.substring(0, rule.substring(1));
      } else if (rule.indexOf('-') != -1) {

        trackingNumber = trackingNumber.substring(0, trackingNumber.length - rule.substring(1));
      }
    }
  }

</script>


</body>

</html>