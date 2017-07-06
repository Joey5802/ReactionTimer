package terranovaproductions.reactiontimer

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.games.Games
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.replace
import org.jetbrains.anko.db.select
import java.text.SimpleDateFormat
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

        if (apiClient.isConnected) {
            Games.Achievements.unlock(apiClient, getString(R.string.achievement_first_play))
            if (time in 300..400) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_under_400_milliseconds))
            }
            if (time in 200..300) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_under_300_milliseconds))
            }
            if (time in 100..200) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_under_200ms))
            }
            if (time in 50..100) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_under_100_milliseconds))
            }
            if (time <= 50) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_under_50_milliseconds))
            }
            if (time >= 600000) {
                Games.Achievements.unlock(apiClient, getString(R.string.achievement_forgot_it_was_running_eh))
            }

            if ((activity as MainActivity).apiClient.isConnected) {
                Games.Leaderboards.submitScore(apiClient,
                        getString(R.string.leaderboard_fastest_reactions),
                        time)
            }
        }

        tvScore = activity.findViewById<View>(R.id.score) as TextView
        scoreLayout = activity.findViewById<View>(R.id.scoreLayout) as ConstraintLayout
        scoreGrid = activity.findViewById<View>(R.id.scoreGrid) as ListView
        tvEmpty = activity.findViewById<View>(R.id.empty) as TextView

        activity.database.use {
            select("Score", "score").exec {
                val cursor = this
                val scoresCount = cursor.count

                if (scoresCount < 5) {
                    val scores = mutableMapOf<Long, Int>()
                    var index = 0
                    while (cursor.moveToNext()) {
                        scores.put(index.toLong(), cursor.getInt(0))
                        index++
                    }
                    val list = scores.values.toMutableList()
                    list.sort()
                    if (scoresCount != 0 && time < list[0]) {
                        Toast.makeText(activity, "Congratulations! You beat your high score!", Toast.LENGTH_SHORT).show()
                    }
                    scores.put(3, time.toInt())
                    setAdapter(scores.values)
                    insertScore(time)
                } else {
                    val scores = mutableMapOf<Long, Int>()
                    try {
                        var index = 0
                        while (cursor.moveToNext()) {
                            scores.put(index.toLong(), cursor.getInt(0))
                            index++
                        }
                        setAdapter(scores.values)
                        var maximum = 0
                        var keyHighest: Long = 0
                        scores.forEach {
                            if (it.value >= maximum) {
                                maximum = it.value
                                keyHighest = it.key
                            }
                        }
                        if (time < maximum) {
                            replaceSlowest(keyHighest, time)
                            val list = scores.values.toMutableList()
                            list.sort()
                            if (time < list[0]) {
                                Toast.makeText(activity, "Congratulations! You beat your high score!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.i("Score", "Didn't get on high score list")
                        }
                        setAdapter(scores.values)
                    } catch (e: Exception) {
                        Log.i("ScoreFragment", e.message)
                    }
                }
            }
        }

        val viewLeaderboard = activity.findViewById<View>(R.id.view_leaderboard) as Button
        val viewAchievements = activity.findViewById<View>(R.id.view_achievements) as Button
        viewLeaderboard.setOnClickListener {
            startActivityForResult(
                    Games.Leaderboards.getLeaderboardIntent(apiClient,
                            getString(R.string.leaderboard_fastest_reactions)), 0)
        }

        viewAchievements.setOnClickListener {
            startActivityForResult(Games.Achievements.getAchievementsIntent(apiClient),
                    1)

        }
        val adView = activity.findViewById<View>(R.id.adMob) as AdView

        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6AB02553E0F58E621A225D11F014ABFE") // Nexus 6P
                .build()
        adView.loadAd(adRequest)


        val tvheader = TextView(activity)
        tvheader.textSize = 12f
        tvheader.gravity = Gravity.CENTER
        tvheader.text = getString(R.string.high_scores)

        scoreGrid.addHeaderView(tvheader)
        scoreGrid.emptyView = tvEmpty
        scoreGrid.isLongClickable = true

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

    fun insertScore(score: Long) {
        activity.database.use {
            insert("Score", "score" to score.toInt(), "date" to SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.getDefault()).format(Date()))
        }
    }

    fun replaceSlowest(scoreID: Long, score: Long) {
        val idKey = scoreID + 1
        activity.database.use {
            replace("Score", "_id" to idKey, "score" to score)
        }
    }

    fun setAdapter(values: MutableCollection<Int>) {
        val sTime = LinkedList<Int>(values.toMutableList())
        Collections.sort(sTime)

        if (!sTime.isEmpty()) {
            scoreGrid.adapter = ArrayAdapter<Int>(activity, android.R.layout.simple_list_item_1, sTime)
        }
    }

}