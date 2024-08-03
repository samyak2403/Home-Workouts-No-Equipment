package com.rbs.workout.freak.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.rbs.workout.freak.AppSettings
import com.rbs.workout.freak.R
import com.rbs.workout.freak.bw.BuildWiderActivity
import com.rbs.workout.freak.databinding.ActivityHomeBinding
import com.rbs.workout.freak.databinding.ActivityLibraryBinding
import com.rbs.workout.freak.databinding.AdUnifiedBinding
import com.rbs.workout.freak.pojo.PWorkOutCategory
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import kotlinx.android.synthetic.main.activity_library.*
//import kotlinx.android.synthetic.main.activity_library.llAdView
//import kotlinx.android.synthetic.main.activity_library.llAdViewFacebook

class LibraryActivity : BaseActivity() {

    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
        initAction()
        //loadNativeAd()
        setSmallNativeAdd()

    }

    // Todo Common methods
    private fun init() {
//        if (LocalDB.getString(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
//                LocalDB.getString(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
//            CommonConstantAd.loadBannerGoogleAd(this, llAdView, ConstantString.GOOGLE_BANNER_TYPE_AD)
//            llAdViewFacebook.visibility = View.GONE
//            llAdView.visibility = View.VISIBLE
//        } else if (LocalDB.getString(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_FACEBOOK
//                &&
//                LocalDB.getString(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
//            llAdViewFacebook.visibility = View.VISIBLE
//            llAdView.visibility = View.GONE
//            CommonConstantAd.loadFacebookBannerAd(this, llAdViewFacebook)
//        } else {
//            llAdView.visibility = View.GONE
//            llAdViewFacebook.visibility = View.GONE
//        }
//        Utils.initAdd(this, adView)
    }

    private fun initAction() {

        imgBack.setOnClickListener { finish() }

        rltBuildWider.setOnClickListener {
            val workoutCategoryDetails = PWorkOutCategory()
            workoutCategoryDetails.catDefficultyLevel = ConstantString.Build_wider
            workoutCategoryDetails.catName = ConstantString.Build_wider
            workoutCategoryDetails.catSubCategory = ""
            workoutCategoryDetails.catDetailsBg = 0
            workoutCategoryDetails.catTypeImage = 0
            workoutCategoryDetails.catImage = R.drawable.build_wider
            workoutCategoryDetails.catTableName = ConstantString.tbl_bw_exercise

            val intent = Intent(this, BuildWiderActivity::class.java)
            intent.putExtra(ConstantString.key_workout_category_item, workoutCategoryDetails)
            startActivity(intent)
        }

    }


    // Native Ad



    lateinit var viewAds: FrameLayout

//    private fun loadNativeAd() {
//        if (!AppSettings.isUserPaid) {
//            binding!!.adBlockLibrary.visibility = View.VISIBLE
////            binding!!.bannerContainerAdmob.visibility = View.GONE
//
//
//            if (AppSettings.enableAdmobAds) {
////                binding!!.bannerContainerAdmob.visibility = View.VISIBLE
////                loadAdmobBannerAd()
//                //viewAds=findViewById(R.id.view_ads)
//
//                val adLoader=
//                    AdLoader.Builder(this, resources.getString(R.string.admob_native_id))
//                        .forNativeAd { nativeAd: NativeAd ->
//
//
//                            viewAds.visibility= View.VISIBLE
//
//
//                            val layoutInflater= getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//
//
//                            val viewUnifiedBinding= AdUnifiedBinding.inflate(layoutInflater)
//
//
//                            populateNativeAdView(nativeAd,viewUnifiedBinding)
//
//                            viewAds.removeAllViews()
//
//
//                            viewAds.addView(viewUnifiedBinding.root)
//
//
//
//
//
//                        }
//                        .withAdListener(object : AdListener(){
//
//                            override fun onAdFailedToLoad(p0: LoadAdError) {
//
//                                Toast.makeText(this@LibraryActivity, "Failed to load", Toast.LENGTH_SHORT).show()
//
//
//                            }
//
//                        })
//                        .withNativeAdOptions(
//                            NativeAdOptions.Builder().build()
//                        )
//                        .build()
//
//
//
//                adLoader.loadAds(AdRequest.Builder().build(),1)
//
//            }
//        } else {
//            binding!!.adBlockLibrary.visibility = View.GONE
//        }
//    }

    private fun populateNativeAdView(nativeAd: NativeAd, unifiedBinding: AdUnifiedBinding){

        val nativeAdView= unifiedBinding.root


        nativeAdView.headlineView=unifiedBinding.adHeadline
        nativeAdView.bodyView=unifiedBinding.adBody
        nativeAdView.callToActionView=unifiedBinding.adCallToAction
        nativeAdView.iconView=unifiedBinding.adAppIcon
        nativeAdView.priceView=unifiedBinding.adPrice
        nativeAdView.starRatingView=unifiedBinding.adStars
        nativeAdView.storeView=unifiedBinding.adStore
        nativeAdView.advertiserView=unifiedBinding.adAdvertiser



        unifiedBinding.adHeadline.text=nativeAd.headline


        if (nativeAd.body==null){

            unifiedBinding.adBody.visibility= View.INVISIBLE

        }else{

            unifiedBinding.adBody.visibility= View.VISIBLE

            unifiedBinding.adBody.text=nativeAd.body


        }

        if (nativeAd.callToAction==null){

            unifiedBinding.adCallToAction.visibility=View.INVISIBLE

        }
        else{

            unifiedBinding.adCallToAction.visibility=View.VISIBLE

            unifiedBinding.adCallToAction.text=nativeAd.callToAction


        }

        if (nativeAd.icon==null){

            unifiedBinding.adAppIcon.visibility=View.INVISIBLE

        }else{

            unifiedBinding.adAppIcon.visibility=View.VISIBLE

            unifiedBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)


        }

        if (nativeAd.price==null){

            unifiedBinding.adPrice.visibility= View.INVISIBLE



        }
        else{


            unifiedBinding.adPrice.visibility= View.VISIBLE


            unifiedBinding.adPrice.text= nativeAd.price



        }


        if (nativeAd.store==null){

            unifiedBinding.adStore.visibility=View.INVISIBLE

        }
        else{

            unifiedBinding.adStore.visibility=View.VISIBLE

            unifiedBinding.adStore.text=nativeAd.store


        }

        if (nativeAd.starRating==null){



            unifiedBinding.adStars.visibility= View.INVISIBLE

        }
        else{
            unifiedBinding.adStars.visibility= View.VISIBLE

            unifiedBinding.adStars.rating= nativeAd.starRating!!.toFloat()



        }


        if (nativeAd.advertiser==null){

            unifiedBinding.adAdvertiser.visibility= View.INVISIBLE



        }

        else{

            unifiedBinding.adAdvertiser.visibility= View.VISIBLE


            unifiedBinding.adAdvertiser.text= nativeAd.advertiser


        }


        nativeAdView.setNativeAd(nativeAd)

        val vc=nativeAd.mediaContent?.videoController



        if (vc!=null && vc.hasVideoContent()){

            vc.videoLifecycleCallbacks=
                object : VideoController.VideoLifecycleCallbacks(){

                    override fun onVideoEnd() {

                        super.onVideoEnd()
                    }


                }


        }


        else{



        }








    }

}
