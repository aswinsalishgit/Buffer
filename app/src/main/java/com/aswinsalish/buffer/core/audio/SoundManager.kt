package com.aswinsalish.buffer.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.aswinsalish.buffer.R

enum class SoundType {
    CLICK, BOT_WON, USER_WON, TOGGLE_ON, TOGGLE_OFF, SWIPE
}

object SoundManager {
    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null
    private val soundMap = mutableMapOf<SoundType, Int>()

    var isSfxEnabled = true
    var isMusicEnabled = true
        set(value) {
            field = value
            if (value) {
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
        }
    
    var musicVolume = 0.5f
        set(value) {
            field = value
            mediaPlayer?.setVolume(value, value)
        }

    fun init(context: Context) {
        if (soundPool == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()

            soundMap[SoundType.CLICK] = soundPool!!.load(context, R.raw.sfx_click, 1)
            soundMap[SoundType.BOT_WON] = soundPool!!.load(context, R.raw.sfx_bot_won, 1)
            soundMap[SoundType.USER_WON] = soundPool!!.load(context, R.raw.sfx_user_won, 1)
            soundMap[SoundType.TOGGLE_ON] = soundPool!!.load(context, R.raw.sfx_toggle_on, 1)
            soundMap[SoundType.TOGGLE_OFF] = soundPool!!.load(context, R.raw.sfx_toggle_off, 1)
            soundMap[SoundType.SWIPE] = soundPool!!.load(context, R.raw.sfx_swipe, 1)
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.music).apply {
                isLooping = true
                setVolume(musicVolume, musicVolume)
                if (isMusicEnabled) {
                    start()
                }
            }
        }
    }

    fun playSound(type: SoundType) {
        if (!isSfxEnabled) return
        soundMap[type]?.let { soundId ->
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun resumeMusic() {
        if (isMusicEnabled && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
