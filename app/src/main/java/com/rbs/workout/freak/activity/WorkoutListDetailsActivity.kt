package com.rbs.workout.freak.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.R
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.pojo.PWorkOutDetails
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import com.rbs.workout.freak.utils.Utils
import kotlinx.android.synthetic.main.activity_workout_list_details.*

class WorkoutListDetailsActivity : BaseActivity() {



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.slide_down)
    }

    lateinit var workOutCategoryData: ArrayList<PWorkOutDetails>
    lateinit var mContext: Context
    private var currentPos: Int = 0
    private var typeOfControl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_list_details)
        mContext = this

        workOutCategoryData = intent.getSerializableExtra(ConstantString.key_workout_list_array) as ArrayList<PWorkOutDetails>
        currentPos = intent.getIntExtra(ConstantString.key_workout_list_pos, 0)
        typeOfControl = intent.getStringExtra(ConstantString.key_workout_details_type) as String

        defaultSetup()

        setBannerAdd(adMobView)
        initAction()


    }

    /* Todo Common settings methods */
    private fun defaultSetup() {
        val doWorkOutPgrAdpt = DoWorkoutPagerAdapter()
        viewPagerWorkoutDetails.adapter = doWorkOutPgrAdpt
        viewPagerWorkoutDetails.currentItem = currentPos
        imgbtnDone.text = (1 + currentPos).toString().plus(" / ").plus(workOutCategoryData.size)

        if (typeOfControl != ConstantString.val_is_workout_list_activity) {
            rltBottomControl.visibility = View.GONE
        }

    }

    private fun initAction() {
        imgbtnBack.setOnClickListener {
            onBackPressed()
            //overridePendingTransition(R.anim.none, R.anim.slide_down)
        }

        imgbtnNext.setOnClickListener {
            if (viewPagerWorkoutDetails.currentItem < workOutCategoryData.size)
                viewPagerWorkoutDetails.currentItem = viewPagerWorkoutDetails.currentItem + 1
        }

        imgbtnPrev.setOnClickListener {
            if (viewPagerWorkoutDetails.currentItem > 0)
                viewPagerWorkoutDetails.currentItem = viewPagerWorkoutDetails.currentItem - 1
        }

        imgbtnVideo.setOnClickListener {
            try {
                val dbHelper = DataHelper(mContext)
                val strVideoLink = dbHelper.getVideoLink(Utils.ReplaceSpacialCharacters(workOutCategoryData[viewPagerWorkoutDetails.currentItem].title))
                if(strVideoLink != "") {
                    val str = "https://www.youtube.com/watch?v=$strVideoLink"
                    openYoutube(str)
    //                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$strVideoLink")))
                } else{
                    Toast.makeText(this,getString(R.string.error_video_not_exist),Toast.LENGTH_SHORT).show()
                }
            } catch (e: ActivityNotFoundException){
                e.printStackTrace()
                Toast.makeText(this,"Youtube player not available on this device",Toast.LENGTH_LONG).show()
            }
        }

        viewPagerWorkoutDetails.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) { }

            override fun onPageScrollStateChanged(p0: Int) { }

            override fun onPageSelected(pos: Int) {
                imgbtnDone.text = (1 + pos).toString().plus(" / ").plus(workOutCategoryData.size)
            }
        })
    }

    /* Todo adapter */
    inner class DoWorkoutPagerAdapter : androidx.viewpager.widget.PagerAdapter() {

        override fun isViewFromObject(convertView: View, anyObject: Any): Boolean {
            return convertView === anyObject as RelativeLayout
        }

        override fun getCount(): Int {
            return workOutCategoryData.size
        }

        private fun getItem(pos: Int): PWorkOutDetails {
            return workOutCategoryData[pos]
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item: PWorkOutDetails = getItem(position)
            val itemView = LayoutInflater.from(mContext).inflate(R.layout.workout_details_row, container, false)
            val txtWorkoutTitle: TextView = itemView.findViewById(R.id.txtWorkoutTitle)
            val txtWorkoutDetails: TextView = itemView.findViewById(R.id.txtWorkoutDetails)
            val viewfliperWorkout: ViewFlipper = itemView.findViewById(R.id.imgWorkoutDemo)
            val adView: AdView = itemView.findViewById(R.id.adView)

//            Utils.initAdd(mContext, adView)

            txtWorkoutTitle.text = item.title
            txtWorkoutDetails.text = item.descriptions.replace("\\n", System.getProperty("line.separator")!!).replace("\\r", "")

            viewfliperWorkout.removeAllViews()

            val listImg: ArrayList<String> = Utils.getAssetItems(mContext, Utils.ReplaceSpacialCharacters(item.title))
            for (i in 0 until listImg.size) {
                val imgview = ImageView(mContext)
//                Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(mContext).load(listImg[i]).into(imgview)

                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.gravity = Gravity.START
                imgview.layoutParams = layoutParams

                // imgview.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
                viewfliperWorkout.addView(imgview)
            }

            viewfliperWorkout.isAutoStart = true
            viewfliperWorkout.setFlipInterval(mContext.resources.getInteger(R.integer.viewfliper_animation))
            viewfliperWorkout.startFlipping()

            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as RelativeLayout)
        }
    }

    override fun setBannerAdd(adContainer: View) {
        if (sessionManager.getBooleanValue(Const.Adshow)) {
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = sessionManager.getStringValue(Const.bannerAdId)
            (adContainer as RelativeLayout).addView(adView)
            adView.loadAd(adRequest)
        }
    }

}
