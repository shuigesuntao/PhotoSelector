package com.kelin.photoselector.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.ui.PlayerView
import com.kelin.photoselector.databinding.FragmentKelinPhotoSelectorPlayVideoBinding
import java.io.File

/**
 * **描述:** 视频播放页面。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2020/8/19 5:35 PM
 *
 * **版本:** v 1.0.0
 */
internal class PlayVideoFragment : BasePhotoSelectorFragment<FragmentKelinPhotoSelectorPlayVideoBinding>() {

    companion object {

        private const val KEY_VIDEO_URI = "key_kelin_photo_selector_video_uri"

        fun configurationPlayVideoIntent(intent: Intent, uri: String) {
            intent.putExtra(KEY_VIDEO_URI, uri)
        }
    }

    private val exoPlayer: ExoPlayer by lazy { ExoPlayer.Builder(requireContext()).build() }
    /**
     * 用来记录用户是否手动按下了暂停按钮。
     */
    private var manualPause = false

    private val playerEventListener by lazy { PlayerEventListener() }

    private val uri by lazy {
        requireArguments().getString(KEY_VIDEO_URI)?.let { path ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N || path.startsWith("http")) {
                Uri.parse(path)
            } else {
                Uri.fromFile(File(path))
            }
        } ?: throw NullPointerException("The uri must not be null!")
    }

    override fun generateViewBinding(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean): FragmentKelinPhotoSelectorPlayVideoBinding {
        return  FragmentKelinPhotoSelectorPlayVideoBinding.inflate(inflater, container, attachToParent)
    }

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exoPlayer.apply {
            addAnalyticsListener(EventLogger())
            addListener(playerEventListener)
            playWhenReady = true
            addMediaItem(MediaItem.fromUri(uri))
//            setMediaSource(
//                ProgressiveMediaSource.Factory(
//                    DefaultHttpDataSource.Factory()
//                        .setUserAgent("exoplayer-codelab")
//                        .setAllowCrossProtocolRedirects(false)
//                ).createMediaSource()
//            )
            prepare()
        }
        vb.pvKelinPhotoSelectorVideoPlayer.run {
            player = exoPlayer

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("VideoPlayer", "onResume：${manualPause}")
        if (!manualPause) {
            exoPlayer.playWhenReady = true
            vb.pvKelinPhotoSelectorVideoPlayer.hideController()
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.playWhenReady = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.apply {
            removeListener(playerEventListener)
            release()
        }
    }

    private inner class PlayerEventListener : Player.Listener{
        override fun onPlayerError(error: PlaybackException) {
            Toast.makeText(applicationContext, "播放失败", Toast.LENGTH_SHORT).show()
        }
    }
}