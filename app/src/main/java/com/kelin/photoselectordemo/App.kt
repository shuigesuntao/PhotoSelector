package com.kelin.photoselectordemo

import android.app.Application
import android.os.Build
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.video.VideoFrameDecoder
import com.kelin.photoselector.PhotoSelector

/**
 * **描述:** Application
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/7/23 10:47 AM
 *
 * **版本:** v 1.0.0
 */
class App : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        PhotoSelector.init(this,"${packageName}.fileProvider", true, albumTakePictureEnable = false)
    }

    override fun newImageLoader(context:PlatformContext): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                //添加GIF支持
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }
}