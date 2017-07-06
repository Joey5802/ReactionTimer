package terranovaproductions.reactiontimer

import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class ReactionFragment : Fragment() {
    var init: Long = 0
    var now: Long = 0
    var time: Long = 0
    var stop: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, bundle: Bundle?): View? {
        super.onCreateView(inflater, null, bundle)
        return inflater!!.inflate(R.layout.fragment_reaction, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layout = LinearLayout(activity)
        layout.orientation = LinearLayout.VERTICAL
        val handler = Handler()

        val reactionView = activity.findViewById<View>(R.id.reactionView) as ConstraintLayout
        val currentTime = activity.findViewById<View>(R.id.reaction_time) as TextView

        val updater = object : Runnable {
            override fun run() {
                if (!stop) {
                    now = System.currentTimeMillis()
                    time = now - init
                    currentTime.text = time.toString()
                    handler.postDelayed(this, 30)
                }
            }
        }
        stop = false
        init = System.currentTimeMillis()
        handler.post(updater)

        reactionView.setOnClickListener {
            stop = true
            val f = ScoreFragment()
            val b = Bundle()
            b.putLong("REACTION_TIME", time)
            f.arguments = b
            if (activity.supportFragmentManager != null) {
                activity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, f)
                        .commitAllowingStateLoss()
            }
        }
    }
}
