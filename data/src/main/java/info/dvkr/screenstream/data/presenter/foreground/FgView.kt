package info.dvkr.screenstream.data.presenter.foreground

import android.media.projection.MediaProjection
import android.support.annotation.Keep
import android.view.Display
import info.dvkr.screenstream.domain.eventbus.EventBus
import rx.Observable
import java.net.InetSocketAddress


interface FgView {
    // From ForegroundService to FgPresenter
    @Keep sealed class FromEvent {
        @Keep object Init : FromEvent()
        @Keep data class StartHttpServer(val serverAddress: InetSocketAddress,
                                         val favicon: ByteArray,
                                         val logo: ByteArray,
                                         val baseIndexHtml: String,
                                         val basePinRequestHtml: String,
                                         val pinRequestErrorMsg: String,
                                         val jpegByteStream: Observable<ByteArray>) : FromEvent()

        @Keep object StopHttpServer : FromEvent()
        @Keep data class StartImageGenerator(val display: Display,
                                             val mediaProjection: MediaProjection) : FromEvent()

        @Keep object StopStreamComplete : FromEvent()
        @Keep object HttpServerRestartRequest : FromEvent()
        @Keep object ScreenOff : FromEvent()
        @Keep data class CurrentInterfaces(val interfaceList: List<EventBus.Interface>) : FromEvent()
    }

    // Events from ForegroundService to FgPresenter
    fun fromEvent(): Observable<FromEvent>

    // To ForegroundService from FgPresenter
    @Keep open class ToEvent { // Open for ForegroundService.LocalEvent
        @Keep object StartHttpServer : ToEvent()
        @Keep data class NotifyImage(val notifyType: String) : ToEvent()
        @Keep data class StopStream(val isNotifyOnComplete: Boolean = true) : ToEvent()
        @Keep object AppExit : ToEvent()
        @Keep object CurrentInterfacesRequest : ToEvent()
        @Keep data class Error(val error: Throwable) : ToEvent()
        @Keep object SlowConnectionDetected : ToEvent()
    }

    // Events to ForegroundService from FgPresenter
    fun toEvent(event: ToEvent)

    // Events to ForegroundService from FgPresenter
    fun toEvent(event: ToEvent, timeout: Long)
}