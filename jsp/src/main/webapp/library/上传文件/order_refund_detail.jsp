<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-cn" class="no-js">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta charset="utf-8" />
<title>订单退款详情</title>
<link href="${CUSTOM_THEME_PATH }css/pages/amazon.refund.detail.css" rel="stylesheet">
</head>

<body style="padding: 0px; margin: 0px">

	<input class="printbtn" type="button" onclick="saveAsPDF();" value="另存为PDF" />
	<input class="printbtn" type="button" onclick="downImg();" value="生成图片">
	<input class="printbtn" type="button" onclick="batchAutoUploadRefund();" value="一键上传退款截图">

	<div class="download-mask" style="display: none;">
	<div class="loader">
	<div class ="mt30 loader-tip">图片正在生成中...
	</div>
	<div class ="mt5">
	<font class ="loader-num mr10">1</font>/
	<font class ="loader-all">0</font>
	</div>
	</div>
	</div>
	<div id="print_content" style="background-color: white;">
		<jsp:useBean id="now" class="java.util.Date" />
		<c:forEach items="${domain.orders }" var="order" varStatus="loopStatus">
		<c:set value="${domain.orderShippedRuleMap[order.orderId]  }" var="orderShippedRule"></c:set>
			<div style="width: 370mm; height: 320mm;" class="delhivery" id="delhivery-${loopStatus.index }">
		</c:forEach>
	</div>

    <app:include type="javascript"/>
	<script src="${CUSTOM_THEME_PATH }js/jsPDF/html2canvas.min.js" type="text/javascript"></script>
	<script src="${CUSTOM_THEME_PATH }js/jsPDF/jsPdf.debug.js" type="text/javascript"></script>

	<script type="text/javascript">
        // 打印PDF格式
        function saveAsPDF() {
            debugger

            var content = $("#print_content");
            var pdf = new jsPDF('l', 'pt', [ 1400, 1200 ]);

            var count = $(".delhivery").length;

            if (count <= 0) {
                return;
            }

            var height = 1200 * count;

            var options = {
                width : 1400,
                height : height,
                pagesplit : true,
                useCORS : true,
                background : '#FFFFFF'
            };

            pdf.addHTML(content, options, function() {
                pdf.save('content.pdf');
            });
        }
        
        function downImg(){
            App.blockUI();
            $(".download-mask").show();
            var downloadNum = 1;
            var imgLength = $(".delhivery").length;
            doScreenShot(0);
            $('.loader-all').html(imgLength);
            function doScreenShot(num){
                html2canvas($("#delhivery-" +num)[0], {
                    useCORS : true,
                    background:'#FFFFFF',
                    onrendered: function(canvas){
                        var imgName = $("#trackNumber-" +num).val();
                        var imgData = canvas.toDataURL('image/png')
                        download(imgData, imgName);
                        $('.loader-num').html(num + 1);
                        if((num + 1) == imgLength){
                            $('.loader-tip').html('图片已生成');
                        }
                    }
                });
            }

            function download(src, imgName){
                var fname = imgName +".png"
                var blob = b64toBlob(src);
                if(navigator.msSaveBlob){
                    navigator.msSaveBlob(blob, fname);
                }else{
                    var $a = $("<a></a>").attr("href", URL.createObjectURL(blob)).attr("download", fname);
                    $a[0].click();
                    var num = downloadNum++;
                    if(num < imgLength){
                        doScreenShot(num);
                    }
                }

                function b64toBlob(b64Data){
                    var byteString = atob(b64Data.split(',')[1])
                    var mimeString = b64Data.split(',')[0].split(':')[1].split(';')[0]
                    var ab = new ArrayBuffer(byteString.length)
                    var ia = new Uint8Array(ab)
                    for (var i = 0; i < byteString.length; i++) {
                      ia[i] = byteString.charCodeAt(i)
                    }
                    return new Blob([ab], {
                      type: mimeString
                    })
                }
        }

        App.unblockUI();
     }

     // 批量上传退款截图
    function batchAutoUploadRefund() {
      var imgLength = $(".delhivery").length;
      if(imgLength === 0){
        alert("没有截图可上传");
        return
      }

      App.blockUI();
      var num = 0;

	  for (let i = 0; i < imgLength; i++) {
		  createAndUploadFile(num);
		  num++;
	  }

      App.unblockUI();
    }

    // 生成pdf截图并上传
    function createAndUploadFile(num) {
      html2canvas($("#delhivery-" + num)[0], {
        useCORS: true,
        background: '#FFFFFF',
        onrendered: function (canvas) {
          var contentWidth = canvas.width;
          var contentHeight = canvas.height;
          var fileName = $("#trackNumber-" + num).val();
          if(!fileName){
			  var orderId = $("#orderId-" + num).val();
			  alert(orderId + "跟踪号为空");
			  return
		  }
          var pageData = canvas.toDataURL('image/jpeg', 1.0);
          var pdf = new jsPDF('l', 'pt', [ 1400, 1200 ]);
          pdf.addImage(pageData, 'JPEG', 5, 5, contentWidth * 0.9, contentHeight);
          // 获取base64pdf
		  var pdfData = pdf.output('datauristring');
		  var file = dataPdfToFile(pdfData, fileName + ".pdf");
		  var formDate = new FormData();
		  formDate.append("file",  file);
          uploadFile(formDate , num);
        }
      });
    }

    //转换base64pdf为文件
	function dataPdfToFile(pdfData, fileName) {
		var arr = pdfData.split(","),
			mime = arr[0].match(/:(.*?);/)[1],
			bstr = atob(arr[1]),
			n = bstr.length,
			u8arr = new Uint8Array(n);
		while (n--) {
			u8arr[n] = bstr.charCodeAt(n);
		}
		return new File([u8arr], fileName, {type: mime});
	}

	// 上传文件
    function uploadFile(formDate , num) {
      $.ajax({
        url: "${CONTEXT_PATH}order/claim/upload/file",
        data : formDate,
        type: "POST",
	    processData : false,
	    contentType : false,
        success: function (r) {
          var orderId = $("#orderId-" + num).val();
          const errors = r.errors;
          if (r.status == '200') {
			  alert(orderId + "上传成功");
          } else {
            if (errors.length > 0) {
              var errorMsg = '';
              for (var i = 0; i < errors.length; i++) {
                errorMsg += errors[i].message + '<br>';
              }
              alert(orderId +'上传失败，' + errorMsg);
            } else {
              alert(orderId + '上传失败，' + r.message);
            }
          }
        },
        error: function () {
          alert("服务器错误,请重新上传");
        }
      });
    }
    </script>
</body>
<!-- END BODY -->
</html>