 function blockUI(el, centerY, timeout) {

  var html = '<div class="loading-message loading-message-boxed"><img src="' + this.getGlobalImgPath() + 'loading-spinner-grey.gif" align=""><span class=""><strong>&nbsp;&nbsp;' + ('加载中, 请稍后...') + '</strong></span></div>';

  if(!el) {
    // page blocking
    el = $(window);
    if (el.height() <= ($(window).height())) {
      centerY = true;
    }
    el.block({
      message: html,
      timeout : timeout,
      css: {
        border: '0',
        padding: '0',
        backgroundColor: 'none'
      },
      overlayCSS: {
        backgroundColor: '#555',
        opacity: 0.05,
        cursor: 'wait'
      }
    });
  }
  else {
    // element blocking
    el = jQuery(el);
    el.block({
      message: html,
      timeout : timeout,
      centerY: centerY != undefined ? centerY : true,
      css: {
        top: '10%',
        border: '0',
        padding: '0',
        backgroundColor: 'none'
      },
      overlayCSS: {
        backgroundColor: '#555',
        opacity: 0.05,
        cursor: 'wait'
      }
    });
  }

}

// wrapper function to  un-block element(finish loading)
 function unblockUI(el) {

  if(!el) {
    el = $(window);
  }
  else {
    el = jQuery(el);
  }

  el.unblock({
    onUnblock: function () {
      jQuery(el).removeAttr("style");
    }
  });
}

// initializes uniform elements
 function initUniform(els) {
   if (els) {
     jQuery(els).each(function () {
       if ($(this).parents(".checker").size() == 0) {
         $(this).show();
         $(this).uniform();
       }
     });
   } else {
     handleUniform();
   }
 }

