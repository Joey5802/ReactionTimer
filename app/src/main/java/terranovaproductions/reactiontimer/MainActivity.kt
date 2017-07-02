package terranovaproductions.reactiontimer

import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games


class MainActivity : AppCompatActivity() {
    lateinit var apiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        apiClient = GoogleApiClient.Builder(applicationContext)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .enableAutoManage(this, {
                    Log.e(ContentValues.TAG, "Could not connect to Play games services")
//                    activity.finish()
                }).build()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, StartFragment())
                .commitAllowingStateLoss()

    }
}
