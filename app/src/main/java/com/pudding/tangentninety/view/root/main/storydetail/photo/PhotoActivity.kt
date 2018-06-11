package com.pudding.tangentninety.view.root.main.storydetail.photo

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.*
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.Toast

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pudding.tangentninety.BuildConfig
import com.pudding.tangentninety.GlideApp
import com.pudding.tangentninety.R
import com.pudding.tangentninety.base.BaseActivity

import butterknife.BindView
import com.pudding.tangentninety.weight.photoview.PhotoView

/**
 * Created by Error on 2017/7/13 0013.
 */

class PhotoActivity : BaseActivity<PhotoContract.View, PhotoPresent>(), PhotoContract.View {
    @BindView(R.id.photo_view)
    lateinit var photo: PhotoView
    @BindView(R.id.root)
    lateinit var root: FrameLayout
    private var backguound: Int = 0
    private lateinit var imageUrl: String
    private lateinit var webPhotoRect: RectF
    private var webPhotoTopTowebTop: Float = 0f
    private var animateScale: Float = 0f
    private var imageLeft: Float = 0f

    private var isAnimate = false


    override val layout: Int = R.layout.activity_photoview

    override fun setDayNightTheme() {}

    private fun mateImageWidth() {
        val imageRect: RectF = photo.displayRect
        animateScale = (webPhotoRect.right - webPhotoRect.left) / photo.width
        imageLeft = webPhotoRect.left - (photo.width - imageRect.width() * animateScale) / 2
        photo.minimumScale = animateScale - 0.1f
        val visiableTop = if (webPhotoTopTowebTop > 0) webPhotoRect.top else webPhotoRect.top - webPhotoTopTowebTop
        val visiableBottom = if (webPhotoRect.bottom > photo.height) photo.height.toFloat() else webPhotoRect.bottom
        if ((visiableBottom - visiableTop) / animateScale > photo.height) {
            photo.translationY = webPhotoRect.top - webPhotoTopTowebTop - photo.height / (2 * animateScale) + photo.height / 2 + (visiableBottom - visiableTop - photo.height * animateScale) / 2
            photo.scrollBy(0, (-webPhotoTopTowebTop / animateScale).toInt() + photo.paddingTop + ((visiableBottom - visiableTop - photo.height * animateScale) / 2).toInt())
        } else {
            val normalTop = if (webPhotoTopTowebTop >= 0) 0f else webPhotoTopTowebTop
            photo.translationY = webPhotoRect.top - normalTop - (photo.height / 2) + (photo.height / 2) * animateScale
            photo.scrollBy(0, (-normalTop / animateScale + photo.paddingTop + imageRect.top).toInt())
        }
        photo.translationX = imageLeft
        photo.scaleX = animateScale
        photo.scaleY = animateScale
    }

    private fun downloadImage() {
        mPresenter.downloadImage(imageUrl)
    }

    private fun canSaveImage() {
        photo.setOnLongClickListener(View.OnLongClickListener { v ->
            if (isDestroyed)
                return@OnLongClickListener false
            v.showContextMenu()
            false
        })
    }

    private fun openAnimate() {
        val scrollY = PropertyValuesHolder.ofInt("scrollY", 0)
        val translationY = PropertyValuesHolder.ofFloat("translationY", 0f)
        val translationX = PropertyValuesHolder.ofFloat("translationX", 0f)
        val scalex = PropertyValuesHolder.ofFloat("scaleX", 1f)
        val scaley = PropertyValuesHolder.ofFloat("scaleY", 1f)
        val animator1 = ObjectAnimator.ofPropertyValuesHolder(photo, scrollY, translationX, translationY, scalex, scaley)
        val animator2 = ObjectAnimator.ofArgb(root, "backgroundColor", backguound)
        val set = AnimatorSet()
        set.playTogether(animator1, animator2)
        set.duration = 400
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                isAnimate = true
            }

            override fun onAnimationEnd(animation: Animator) {
                isAnimate = false
            }
        })
        set.start()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (isAnimate) {
            true
        } else super.dispatchTouchEvent(ev)
    }

    private fun finishAnimate() {
        if (isAnimate)
            return
        isAnimate = true
        val beforeRect: RectF = photo.displayRect!!
        val centerY = photo.height / 2 + photo.paddingTop
        var scaleTop: Float
        if (beforeRect.height()/ photo.scale * animateScale > photo.height - photo.paddingTop - photo.paddingBottom) {
            scaleTop = centerY - (centerY - beforeRect.top - photo.paddingTop) / photo.scale * animateScale
            val scaleBottom = (beforeRect.bottom - centerY + photo.paddingTop) / photo.scale * animateScale + centerY
            if (scaleTop > photo.paddingTop) {
                scaleTop = photo.paddingTop.toFloat()
            } else if (scaleBottom < photo.height - photo.paddingBottom) {
                scaleTop += photo.height - photo.paddingBottom - scaleBottom
            }
        } else {
            scaleTop=(beforeRect.top+beforeRect.bottom)/2+photo.paddingTop-beforeRect.height()*animateScale/photo.scale/2
        }
        photo.setScale(animateScale, true)
        val animator = ValueAnimator.ofInt((scaleTop - webPhotoTopTowebTop).toInt())
        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            internal var oldValue = 0

            override fun onAnimationUpdate(animation: ValueAnimator) {
                val value = animation.animatedValue as Int
                val newValue = value - oldValue
                photo.scrollBy(0, newValue)
                oldValue = value
            }
        })
        val translationY = PropertyValuesHolder.ofFloat("translationY", webPhotoRect.top - webPhotoTopTowebTop)
        val translationX = PropertyValuesHolder.ofFloat("translationX", imageLeft)
        val animator1 = ObjectAnimator.ofPropertyValuesHolder(photo, translationY, translationX)
        val animator2 = ObjectAnimator.ofArgb(root, "backgroundColor", backguound, Color.TRANSPARENT)
        animator2.interpolator = AccelerateInterpolator()
        val set = AnimatorSet()
        set.playTogether(animator, animator1, animator2)
        set.duration = 400
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        set.start()
    }

    override fun onBackPressedSupport() {
        finishAnimate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.P){
            val lp = window.attributes;
            lp.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp;
        }
        super.onCreate(savedInstanceState)
    }

    override fun initEventAndData(savedInstanceState: Bundle?) {
        imageUrl = intent.getStringExtra(IMAGEURL_KEY)
        webPhotoRect = intent.getParcelableExtra(RECTF_KEY)
        webPhotoTopTowebTop = intent.getFloatExtra(TOP_KEY, 0f)
        backguound = Color.BLACK
        photo.setOnCreateContextMenuListener(this)
        photo.setOnClickListener{finishAnimate()}
        photo.setLoadImageSuccessListener{
            canSaveImage()
            mateImageWidth()
            openAnimate()
        }
        GlideApp.with(this).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.DATA).into(photo)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.menu_large_image, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        BuildConfig.VERSION_CODE)
            } else {
                downloadImage()
            }
            R.id.change_background -> {
                backguound = if (backguound == Color.BLACK) Color.WHITE else Color.BLACK
                root.setBackgroundColor(backguound)
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            BuildConfig.VERSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadImage()
                } else {
                    Toast.makeText(this, R.string.image_save_failed, Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun showResult(result: String) {
        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show()
    }

    override fun initInject() {
        activityComponent.inject(this)
    }

    companion object {
        const val IMAGEURL_KEY = "imageUrl"
        const val RECTF_KEY = "RectF"
        const val TOP_KEY = "webTop"

        fun startPhotoActivity(activity: Activity, url: String, left: Float, top: Float, right: Float, bottom: Float, webPhotoTopTowebTop: Float) {
            if (url.endsWith("jpg") || url.endsWith("png") || url.endsWith("gif")) {
                val rect = RectF(left, top, right, bottom)
                val intent = Intent(activity, PhotoActivity::class.java)
                intent.putExtra(IMAGEURL_KEY, url)
                intent.putExtra(RECTF_KEY, rect)
                intent.putExtra(TOP_KEY, webPhotoTopTowebTop)
                activity.startActivity(intent)
            }
        }
    }
}
