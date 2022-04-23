package com.arajangstudio.jajanhub_partner.ui.merchant

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.arajangstudio.jajanhub_partner.databinding.ActivityPhotoViewBinding
import com.arajangstudio.jajanhub_partner.databinding.ItemPhotoViewBinding
import com.igreenwood.loupe.Loupe
import com.igreenwood.loupe.extensions.createLoupe
import com.igreenwood.loupe.extensions.setOnViewTranslateListener

class PhotoViewActivity : AppCompatActivity() {

    companion object {
        private const val ARGS_IMAGE_URLS = "ARGS_IMAGE_URLS"
        private const val ARGS_INITIAL_POSITION = "ARGS_INITIAL_POSITION"

        fun createIntent(context: Context, urls: ArrayList<String>, initialPos: Int): Intent {
            return Intent(context, PhotoViewActivity::class.java).apply {
                putExtra(ARGS_IMAGE_URLS, urls)
                putExtra(ARGS_INITIAL_POSITION, initialPos)
            }
        }
    }

    private lateinit var binding: ActivityPhotoViewBinding

    @Suppress("UNCHECKED_CAST")
    private val urls: List<String> by lazy { intent.getSerializableExtra(ARGS_IMAGE_URLS) as List<String> }
    private val initialPos: Int by lazy { intent.getIntExtra(ARGS_INITIAL_POSITION, 0) }
    private var adapter: ImageAdapter? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //android O fix orientation bug
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        initToolbar()
        initViewPager()
    }

    override fun onBackPressed() {
        adapter?.clear()
        super.onBackPressed()
    }

    override fun onDestroy() {
        adapter = null
        super.onDestroy()
    }

    private fun initViewPager() {
        adapter = ImageAdapter(this, urls)
        binding.viewpager.adapter = adapter
        binding.viewpager.currentItem = initialPos
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = ""
        }
    }

    private fun showToolbar() {
        binding.toolbar.animate()
            .setInterpolator(AccelerateDecelerateInterpolator())
            .translationY(0f)
    }

    override fun onSupportNavigateUp(): Boolean {
            finish()
        return true
    }

    override fun finish() {
        super.finish()
    }

    private fun hideToolbar() {
        binding.toolbar.animate()
            .setInterpolator(AccelerateDecelerateInterpolator())
            .translationY(-binding.toolbar.height.toFloat())
    }

    inner class ImageAdapter(var context: Context, var urls: List<String>) : PagerAdapter() {

        private var loupeMap = hashMapOf<Int, Loupe>()
        private var views = hashMapOf<Int, ImageView>()
        private var currentPos = 0

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val binding = ItemPhotoViewBinding.inflate(LayoutInflater.from(context))
            container.addView(binding.root)
            loadImage(binding.image, binding.container, position)
            views[position] = binding.image
            return binding.root
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
            super.setPrimaryItem(container, position, obj)
            this.currentPos = position
        }

        override fun getCount() = urls.size

        private fun loadImage(image: ImageView, container: ViewGroup, position: Int) {

                Glide.with(image.context).load(urls[position])
                    .onlyRetrieveFromCache(true)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            val loupe = createLoupe(image, container) {
                                maxZoom = 5.0f
                                dismissAnimationDuration = 250L
                                restoreAnimationDuration = 250L
                                flingAnimationDuration = 250L
                                scaleAnimationDuration = 375L
                                overScaleAnimationDuration = 375L
                                overScrollAnimationDuration = 250L
                                viewDragFriction = 1.0f
                                dragDismissDistanceInDp = 96
                                doubleTapZoomScale = 0.5f

                                setOnViewTranslateListener(
                                    onStart = { hideToolbar() },
                                    onRestore = { showToolbar() },
                                    onDismiss = { finish() }
                                )
                            }

                            loupeMap[position] = loupe
                            if (position == initialPos) {
                                startPostponedEnterTransition()
                            }
                            return false
                        }

                    }).into(image)
        }

        fun clear() {
            // clear refs
            loupeMap.forEach {
                val loupe = it.value
                // clear refs
                loupe.cleanup()
            }
            loupeMap.clear()
        }
    }

}