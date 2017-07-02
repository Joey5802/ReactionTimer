package terranovaproductions.reactiontimer

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import com.google.android.gms.games.GamesStatusCodes
import com.google.android.gms.games.leaderboard.LeaderboardVariant
import com.google.android.gms.games.leaderboard.Leaderboards
import java.util.*


class ScoreFragment : Fragment() {
    lateinit var tvScore: TextView
    var time: Long = 0
    lateinit var scoreLayout: ConstraintLayout
    lateinit var scoreGrid: ListView
    lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        time = bundle.getLong("REACTION_TIME")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, bundle: Bundle?): View? {
        super.onCreateView(inflater, null, bundle)
        return inflater!!.inflate(R.layout.fragment_score, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val apiClient = (activity as MainActivity).apiClient
        loadScoreOfLeaderBoard(apiClient)
        if ((activity as MainActivity).apiClient.isConnected) {
            Games.Leaderboards.submitScore(apiClient,
                    getString(R.string.leaderboard_fastest_reactions),
                    time)
        }
        tvScore = activity.findViewById(R.id.score) as TextView
        scoreLayout = activity.findViewById(R.id.scoreLayout) as ConstraintLayout
        scoreGrid = activity.findViewById(R.id.scoreGrid) as ListView
        tvEmpty = activity.findViewById(R.id.empty) as TextView
        val viewLeaderboard = activity.findViewById(R.id.view_leaderboard) as Button
        viewLeaderboard.setOnClickListener {
            startActivityForResult(
                    Games.Leaderboards.getLeaderboardIntent((activity as MainActivity).apiClient,
                            getString(R.string.leaderboard_fastest_reactions)), 0);
        }
        val adView = activity.findViewById(R.id.adMob) as AdView

        //request TEST ads to avoid being disabled for clicking your own ads
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6AB02553E0F58E621A225D11F014ABFE") // Nexus 6P
                .build()
        adView.loadAd(adRequest)


        val tvheader = TextView(activity)
        tvheader.textSize = 12f
        tvheader.gravity = Gravity.CENTER
        tvheader.text = "High Scores!"

        scoreGrid.addHeaderView(tvheader)
        scoreGrid.emptyView = tvEmpty
        scoreGrid.isLongClickable = true

        val sTime = ArrayList<Long>()
        setAdapter(sTime)

//        saveScore.setOnClickListener {
//            saveScore.isEnabled = false
//            sTime.add(time)
//            setAdapter(sTime)
//            scoreGrid.invalidate()
//        }

        scoreLayout.setOnClickListener {
            activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, StartFragment())
                    .commitAllowingStateLoss()
        }

        if (arguments != null) {
            val sScore = time.toString() + "ms"
            tvScore.text = sScore
        }

    }

    fun setAdapter(sTime: ArrayList<Long>) {
        Collections.sort(sTime)
        Collections.reverseOrder<Any>()

        if (!sTime.isEmpty()) {
            if (sTime.size < 10) {
                val time_adapter = ArrayAdapter(
                        activity, android.R.layout.simple_list_item_1, sTime)
                scoreGrid.adapter = time_adapter
            } else {
                val time_adapter = ArrayAdapter(
                        activity, android.R.layout.simple_list_item_1, sTime.subList(0, 9))
                scoreGrid.adapter = time_adapter
            }
        }
    }

    private fun loadScoreOfLeaderBoard(apiClient: GoogleApiClient) {
        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(apiClient, getString(R.string.leaderboard_fastest_reactions), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback { scoreResult ->
            if (isScoreResultValid(scoreResult)) {
                Log.wtf("SCORE", scoreResult.score.rawScore.toString())
            }
        }
    }

    private fun isScoreResultValid(scoreResult: Leaderboards.LoadPlayerScoreResult?): Boolean {
        return scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.status.statusCode && scoreResult.score != null
    }

}

