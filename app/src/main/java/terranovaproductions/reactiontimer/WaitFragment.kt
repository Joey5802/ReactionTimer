package terranovaproductions.reactiontimer

import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.util.*

class WaitFragment : Fragment() {
    internal var stop: Boolean = false
    lateinit var handler: Handler

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, bundle: Bundle?): View? {
        super.onCreateView(inflater, null, bundle)
        return inflater!!.inflate(R.layout.fragment_wait, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        stop = false
        val r = Random()

        var randomNum = r.nextInt(7000 - 3000) + 3000
        randomNum = Math.abs(randomNum)

        val tvWait = activity.findViewById<View>(R.id.tvWait) as TextView
        val tvTap = activity.findViewById<View>(R.id.tryagain) as Button

        val waitLayout = activity.findViewById<View>(R.id.waitLayout) as ConstraintLayout
        waitLayout.setOnClickListener {
            stop = true
            tvWait.text = getString(R.string.too_soon)
            tvTap.visibility = View.VISIBLE
        }

        tvTap.setOnClickListener {
            activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, StartFragment())
                    .commitAllowingStateLoss()
        }

        handler = Handler()
        handler.postDelayed({
            if (!stop) {
                activity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, ReactionFragment())
                        .commitAllowingStateLoss()

            }
        }, randomNum.toLong())
    }

}