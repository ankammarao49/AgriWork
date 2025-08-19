package com.example.agriwork.data.utils

import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.mutableStateOf

class VoiceInputHelper(private val activity: Activity) {

    // Holds both live (partial) and final recognized text
    var recognizedText = mutableStateOf("")
    var isListening = mutableStateOf(false)
    var rmsLevel = mutableStateOf(0f)

    private var speechRecognizer: SpeechRecognizer? = null
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    private fun playTone(type: Int, duration: Int) {
        try {
            toneGenerator.startTone(type, duration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playStartSound() = playTone(ToneGenerator.TONE_PROP_BEEP, 150)
    private fun playStopSound() = playTone(ToneGenerator.TONE_PROP_ACK, 200)


    fun startListening() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    isListening.value = true
                    playStartSound() // 🔊 beep when mic opens
                }

                override fun onBeginningOfSpeech() {
                    recognizedText.value = ""
                }

                override fun onRmsChanged(rmsdB: Float) {
                    rmsLevel.value = rmsdB
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    isListening.value = false
                    playStopSound() // 🔊 beep when speech ends
                }

                override fun onError(error: Int) {
                    recognizedText.value = ""
                    isListening.value = false
                    playStopSound() // 🔊 also beep on error
                }

                override fun onResults(results: Bundle?) {
                    val matches =
                        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        recognizedText.value = matches[0]
                    }
                    isListening.value = false
                    playStopSound() // 🔊 beep when final result comes
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val partial =
                        partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!partial.isNullOrEmpty()) {
                        recognizedText.value = partial[0]
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // change to "hi-IN" for Hindi, etc.
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        isListening.value = false
        playStopSound() // 🔊 beep on manual stop
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        toneGenerator.release()
    }
}
