package terranovaproductions.reactiontimer

import android.content.ContentValues
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
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
                .setViewForPopups(findViewById<View>(R.id.container))
                .enableAutoManage(this, {
                    Log.e(ContentValues.TAG, "Could not connect to Play games services")
                }).build()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, StartFragment())
                .commitAllowingStateLoss()

    }
}
