package terranovaproductions.reactiontimer

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Tristan on 6/20/2015.
 * It serves as the start page for the app.
 */

class StartFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, bundle: Bundle?): View? {
        super.onCreateView(inflater, null, bundle)
        return inflater!!.inflate(R.layout.fragment_start, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mainLayout = activity.findViewById<View>(R.id.startLayout) as ConstraintLayout

        mainLayout.setOnClickListener {
            activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, WaitFragment())
                    .commitAllowingStateLoss()
        }
    }

}
