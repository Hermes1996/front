<%--
  Created by IntelliJ IDEA.
  User: win10
  Date: 2022/1/20
  Time: 15:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

function uploadFile() {
const flieDiglog = dialog({
title: '上传退款截图',
width: 600,
height: 350,
top: 150,
url: CONTEXT_PATH + "order/claim/upload/file",
okValue: '提交',
ok: function () {
const uploadWindow = $(this.iframeNode.contentWindow.document.body);
const submitForm = uploadWindow.find("#submit-form");
const param = submitForm.serialize();
$.ajax({
url: submitForm.attr('action'),
type: submitForm.attr('method'),
data: param,
success: function (data) {
alert(data.message);
if (data.status = 200) {
window.location.reload();
}
},
error: function (e) {
alert("操作失败，请关闭重试");
return false;
}
});
}
});
flieDiglog.show();
}

java:
@RequestMapping(value = "upload/file", method = {RequestMethod.GET})
public String toUploadFile(@ModelAttribute("domain") OrderClaimDo domain)
{
return "order/order_claim_upload_file";
}

ajax页面
<form id="submit-form" action="${CONTEXT_PATH}order/claim/save/file" method="post">
    <input type="file" name="file" multiple/>
</form>

</body>
</html>
