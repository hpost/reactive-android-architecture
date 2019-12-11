package cc.femto.android.common.widget

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * A [PagerAdapter] that returns a view corresponding to one of the sections/tabs/pages.
 * This provides the data for the [androidx.viewpager.widget.ViewPager].
 */
abstract class ViewPagerAdapter : PagerAdapter() {

    /**
     * Get view corresponding to a specific position.
     * @param position Position to fetch view for.
     * @return View for specified position.
     */
    abstract fun getItem(position: Int): View

    /**
     * Get number of pages the [androidx.viewpager.widget.ViewPager] should render.
     * @return Number of views to be rendered as pages.
     */
    abstract override fun getCount(): Int

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = getItem(position)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = view === obj
}
