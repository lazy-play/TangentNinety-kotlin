function img_replace(source, replaceSource) {
    $('img[zhimg-src*="'+source+'"]').each(function () {
        $(this).attr('src', replaceSource);
    });
}
var DEFAULT_LOADING_IMAGE_URI = "file:///android_asset/default_pic_content_image_download";

function onLoaded(isBackground) {
	var allImage = document.querySelectorAll("img");
	allImage = Array.prototype.slice.call(allImage, 0);

	allImage.forEach(function(image) {
	if(!isBackground){
	image.addEventListener('click',function(){
            onImageClick(this)
        },false);
        if(image.src.indexOf(DEFAULT_LOADING_IMAGE_URI)>=0){
		ZhihuDaily.loadDownloadImage(image.getAttribute("zhimg-src"));
		}
		}
	});
}
function onImageClick(pImage) {
//    	console.log(pImage.getBoundingClientRect().bottom);
	if (pImage.src.indexOf(DEFAULT_LOADING_IMAGE_URI)>=0) {
	pImage.src=pImage.getAttribute("zhimg-src");
	} else {
	var rect=pImage.getBoundingClientRect();
		ZhihuDaily.openImage(pImage.src,rect.left,rect.top,rect.right,rect.bottom);
	}
};
