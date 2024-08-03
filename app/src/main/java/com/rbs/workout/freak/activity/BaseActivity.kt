package com.rbs.workout.freak.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.rbs.workout.freak.Const
import com.rbs.workout.freak.R
import com.rbs.workout.freak.SessionManager
import com.rbs.workout.freak.database.DataHelper
import com.rbs.workout.freak.interfaces.CallbackListener
import com.rbs.workout.freak.interfaces.ConfirmDialogCallBack
import com.rbs.workout.freak.reminder.ReminderActivity
import com.rbs.workout.freak.report.ReportActivity
import com.rbs.workout.freak.settings.SettingActivity
import com.rbs.workout.freak.utils.CommonConstantAd
import com.rbs.workout.freak.utils.ConstantString
import com.rbs.workout.freak.utils.LocalDB
import com.rbs.workout.freak.utils.LocaleManager
import kotlinx.android.synthetic.main.activity_base.*
import java.text.DecimalFormat
import java.util.*

open class BaseActivity : AppCompatActivity(), AdapterView.OnItemClickListener, ConfirmDialogCallBack {


    lateinit var adView: AdView
    lateinit var adRequest: AdRequest
    open var TAG = "base_Activity"
    open lateinit var sessionManager: SessionManager

    // Todo Object declaratin
    open lateinit var drawerLayout: DrawerLayout
    lateinit var menuAdapter: MenuAdapter

    private lateinit var context: Context
    private lateinit var listOfMenuItem: ListView
    private lateinit var arrDrawerItem: ArrayList<String>
    private lateinit var arrDrawerImg: ArrayList<Int>
    private lateinit var txtExit: TextView


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.setLocale(newBase))



    }

    open fun setBannerAdd(adContainer: View) {
        adRequest = AdRequest.Builder().build()
        adView = AdView(this)
        sessionManager = SessionManager(this)
        MobileAds.initialize(
            this
        ) { }

        if (sessionManager.getBooleanValue(Const.Adshow)) {
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = sessionManager.getStringValue(Const.bannerAdId)
            (adContainer as RelativeLayout).addView(adView)
            adView.loadAd(adRequest)
        }
    }

    open fun setSmallNativeAdd() {
        adRequest = AdRequest.Builder().build()
        adView = AdView(this)

        sessionManager = SessionManager(this)

        if (sessionManager.getBooleanValue(Const.Adshow) == true) {
            val adLoader = sessionManager.let {
                AdLoader.Builder(this, it.getStringValue(Const.nativeAdId))
                    .forNativeAd { nativeAd ->
                        Log.d(TAG, "onNativeAdLoaded: ")
                        // Show the ad.
                        showCustomNativeSmall(nativeAd)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d(TAG, "onAdFailedToLoad: ")
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(
                        NativeAdOptions.Builder() // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build()
                    )
                    .build()
            }
            adRequest.let {
                if (adLoader != null) {
                    adLoader.loadAd(it)
                }
            }
        }
    }

    open fun showCustomNativeSmall(nativeAd: NativeAd) {
        // Set the media view.
        val frameLayout = findViewById<FrameLayout>(R.id.fl_native)
        val adView = layoutInflater
            .inflate(R.layout.small_native_ad_layout, null) as NativeAdView


        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.native_ad_title)
        adView.bodyView = adView.findViewById(R.id.native_ad_body)
        adView.callToActionView = adView.findViewById(R.id.nativeSponsoredTextView)
        adView.iconView = adView.findViewById(R.id.native_ad_main_image)


        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView?)!!.text = nativeAd.headline


        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView?)!!.text = nativeAd.body
        }
        if (nativeAd.icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView?)!!.setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }
        if (nativeAd.body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView?)!!.text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            Log.d(TAG, "showCustomNativeSmall: buttonText " + nativeAd.callToAction)
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as TextView?)!!.text = nativeAd.callToAction
        }


        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
        frameLayout.removeAllViews()
        frameLayout.addView(adView)
    }


    fun getNavigationSize(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        Log.e(
                "TAG",
                "getNavigationSize:IFF:::  ${resources.getDimensionPixelSize(resourceId)}      ${isSoftNavigationBarAvailable()} "
        )
        return if (isSoftNavigationBarAvailable()) {
            if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else {
                0
            }
        } else {
            0
        }
    }




    fun Context.isSoftNavigationBarAvailable(): Boolean {
        val navBarInteractionModeId = resources.getIdentifier(
                "config_navBarInteractionMode",
                "integer",
                "android"
        )
        if (navBarInteractionModeId > 0 && resources.getInteger(navBarInteractionModeId) > 0) {
            // nav gesture is enabled in the settings
            return false
        }
        val appUsableScreenSize = Point()
        val realScreenSize = Point()
        val defaultDisplay = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        defaultDisplay.getSize(appUsableScreenSize)
        defaultDisplay.getRealSize(realScreenSize)
        return appUsableScreenSize.y < realScreenSize.y
    }

    private var hasImmersive = false
    private var cached = false

    @SuppressLint("NewApi")
    open fun hasImmersive(ctx: Context): Boolean {
        if (!cached) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                hasImmersive = false
                cached = true
                return false
            }
            val d = (ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val realDisplayMetrics = DisplayMetrics()
            d.getRealMetrics(realDisplayMetrics)
            val realHeight: Int = realDisplayMetrics.heightPixels
            val realWidth: Int = realDisplayMetrics.widthPixels
            val displayMetrics = DisplayMetrics()
            d.getMetrics(displayMetrics)
            val displayHeight: Int = displayMetrics.heightPixels
            val displayWidth: Int = displayMetrics.widthPixels
            hasImmersive = realWidth > displayWidth || realHeight > displayHeight
            cached = true
        }
        return hasImmersive
    }



    override fun setContentView(layoutResID: Int) {
        drawerLayout = LayoutInflater.from(this).inflate(R.layout.activity_base, null) as DrawerLayout
        val activityContainer = drawerLayout.findViewById(R.id.activity_content) as FrameLayout
        LayoutInflater.from(this).inflate(layoutResID, activityContainer, true)
        super.setContentView(drawerLayout)

        adRequest = AdRequest.Builder().build()
        adView = AdView(this)
        sessionManager = SessionManager(this)
        MobileAds.initialize(
            this
        ) { }



        sessionManager = SessionManager(this)

        context = this
        txtExit = findViewById(R.id.txtExit)
        txtExit.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            confirmationDialog(this, this, "", getString(R.string.exit_confirmation))
        }
        listOfMenuItem = findViewById(R.id.listOfMenuItem)
        llBase.post(Runnable {
            val resources: Resources = resources
            val width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    280f,
                    resources.displayMetrics
            )
            val params =
                    llBase.layoutParams
            params.width = width.toInt()
            llBase.layoutParams = params
        })
        setCommunicationListAdapter()

        if (LocalDB.getString(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_GOOGLE) {
            CommonConstantAd.googlebeforloadAd(this)
        } else if (LocalDB.getString(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_FACEBOOK) {
            CommonConstantAd.facebookbeforeloadFullAd(this)
        }
    }

    fun confirmationDialog(
            content: Context,
            confirmCallBack: ConfirmDialogCallBack,
            strTitle: String,
            strMsg: String
    ): Boolean {

        val builder1 = AlertDialog.Builder(content)
        builder1.setTitle(strTitle)
        builder1.setMessage(strMsg)
        builder1.setCancelable(true)


        builder1.setPositiveButton("Yes") { dialog, _ ->
            dialog.cancel()
            confirmCallBack.Okay()
        }

        builder1.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
            confirmCallBack.cancel()
        }


        val alert11 = builder1.create()
        alert11.show()
        alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

        return false
    }

    // Todo Setup drawer data
    private fun setCommunicationListAdapter() {

        arrDrawerItem = ArrayList()
        arrDrawerItem.add(ConstantString.Drawer_Home)
        arrDrawerItem.add(ConstantString.Drawer_Library)
        arrDrawerItem.add(ConstantString.Drawer_Report)
        arrDrawerItem.add(ConstantString.Drawer_Reminder)
        arrDrawerItem.add(ConstantString.Drawer_Settings)
        arrDrawerItem.add(ConstantString.Drawer_RestartProgress)
//        arrDrawerItem.add(ConstantString.Drawer_History)

//        arrDrawerItem.add("Send Feedback")
//        arrDrawerItem.add("Share This App")
//        arrDrawerItem.add("Rate This App")
//        arrDrawerItem.add("More App")
//        arrDrawerItem.add("Disclaimer")
//        arrDrawerItem.add("Privacy Policy")
//        arrDrawerItem.add("Exit")

        arrDrawerImg = ArrayList()
        arrDrawerImg.add(R.drawable.ic_training_plan)
        arrDrawerImg.add(R.drawable.ic_menu_library)
        arrDrawerImg.add(R.drawable.ic_menu_report)
        arrDrawerImg.add(R.drawable.ic_menu_reminder)
        arrDrawerImg.add(R.drawable.ic_menu_setting)
        arrDrawerImg.add(R.drawable.ic_menu_restart_progress)

//        arrDrawerImg.add(R.drawable.round_home_white_24)
//        arrDrawerImg.add(R.drawable.round_photo_black_24)
//        arrDrawerImg.add(R.drawable.round_perm_contact_calendar_white_24)
//        arrDrawerImg.add(R.drawable.round_share_white_24)
//        arrDrawerImg.add(R.drawable.round_star_white_24)
//        arrDrawerImg.add(R.drawable.round_apps_white_24)
//        arrDrawerImg.add(R.drawable.round_exit_to_app_white_24)

        listOfMenuItem.onItemClickListener = this
        menuAdapter = MenuAdapter()
        listOfMenuItem.adapter = menuAdapter
        setListViewHeightBasedOnItems(listOfMenuItem)

    }

    // Todo declare menu adapter
    inner class MenuAdapter : BaseAdapter() {

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val menuView = LayoutInflater.from(context).inflate(R.layout.cell_of_drawer_item, null)
            val imgItem = menuView.findViewById(R.id.imgItem) as ImageView
            val txtItem = menuView.findViewById(R.id.txtItem) as TextView
            imgItem.setImageResource(arrDrawerImg[p0])
            txtItem.text = arrDrawerItem[p0]
            return menuView
        }

        override fun getItem(p0: Int): Any {
            return p0
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return arrDrawerItem.size
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        drawerLayout.closeDrawer(GravityCompat.START)
        when {
            arrDrawerItem[position] === "Send Feedback" -> contactUs()
//            arrDrawerItem[pos1] === "My Work" -> startActivity(Intent(context,MyWorkActivity::class.java))
            arrDrawerItem[position] === "Rate This App" -> rateUs()
            arrDrawerItem[position] === "Share This App" -> shareAppLink()
            arrDrawerItem[position] === "More App" -> moreApp()
            arrDrawerItem[position] === ConstantString.Drawer_Home -> {
            }
            arrDrawerItem[position] === ConstantString.Drawer_Library -> {
                startActivity(Intent(this, LibraryActivity::class.java))
            }
            arrDrawerItem[position] === ConstantString.Drawer_Report -> {
                startActivity(Intent(this, ReportActivity::class.java))
            }
            arrDrawerItem[position] === ConstantString.Drawer_Reminder -> {
                startActivity(Intent(this, ReminderActivity::class.java))
            }
            arrDrawerItem[position] === ConstantString.Drawer_Settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
            arrDrawerItem[position] === ConstantString.Drawer_RestartProgress -> {
                confirmDialog(this)
            }
            arrDrawerItem[position] === ConstantString.Drawer_History -> {
//                startActivity(Intent(this, HistoryActivity::class.java))
            }
            arrDrawerItem[position] === "Exit" -> finish()
        }
    }

    // Todo More methods
    private fun moreApp() {
        val uri = Uri.parse("https://play.google.com/store/apps/developer?id=Ninety+Nine+Apps")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun rateUs() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
            )
        }
    }

    private fun shareAppLink() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        val link = "https://play.google.com/store/apps/details?id=$packageName"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.app_name)))
    }

    private fun contactUs() {
        try {
            val sendIntentGmail = Intent(Intent.ACTION_SEND)
            sendIntentGmail.type = "plain/text"
            sendIntentGmail.setPackage("com.google.android.gm")
            sendIntentGmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("rbs01569@gmail.com"))
            sendIntentGmail.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
            startActivity(sendIntentGmail)
        } catch (e: Exception) {
            val sendIntentIfGmailFail = Intent(Intent.ACTION_SEND)
            sendIntentIfGmailFail.type = "*/*"
            sendIntentIfGmailFail.putExtra(Intent.EXTRA_EMAIL, arrayOf("rbs01569@gmail.com"))
            sendIntentIfGmailFail.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
            if (sendIntentIfGmailFail.resolveActivity(packageManager) != null) {
                startActivity(sendIntentIfGmailFail)
            }
        }
    }

    // Todo set ListView height base on child
    private fun setListViewHeightBasedOnItems(listView: ListView): Boolean {
        val listAdapter = listView.adapter
        if (listAdapter != null) {
            val numberOfItems = listAdapter.count

            // Get total height of all items.
            var totalItemsHeight = 0
            for (itemPos in 0 until numberOfItems) {
                val item = listAdapter.getView(itemPos, null, listView)
                item.measure(0, 0)
                totalItemsHeight += item.measuredHeight
            }

            // Get total height of all item dividers.
            val totalDividersHeight = listView.dividerHeight * (numberOfItems - 1)

            // Set list height.
            val params = listView.layoutParams
            params.height = totalItemsHeight + totalDividersHeight
            listView.layoutParams = params
            listView.requestLayout()
            return true
        } else {
            return false
        }
    }

    // Todo Confirm to restart progress Fullbody and lower body Day progress
    private fun confirmDialog(content: Context): Boolean {

        val bDialog = AlertDialog.Builder(content)
//        bDialog.setTitle(getString(R.string.restart_progress_title))
        bDialog.setCancelable(true)


        bDialog.setPositiveButton("Yes") { dialog, id ->
            DataHelper(this).restartProgress()
            LocalDB.clearAllData(this)
            startActivity(Intent(this, HomeActivity::class.java))
            dialog.cancel()
            return@setPositiveButton
        }

        bDialog.setNegativeButton("No") { dialog, id ->
            dialog.cancel()
            return@setNegativeButton
        }

        val conDialog = bDialog.create()
        conDialog.show()
        conDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        conDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);


        return false
    }

    fun openInternetDialog(callbackListener: CallbackListener, isSplash: Boolean) {
        if (!isOnline()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("No internet Connection")
            builder.setCancelable(false)
            builder.setMessage("Please turn on internet connection to continue")
            builder.setNegativeButton("Retry") { dialog, _ ->

                if (!isSplash) {
                    openInternetDialog(callbackListener, false)
                }
                dialog!!.dismiss()
                callbackListener.onRetry()


              /*  dialog!!.dismiss()
                openInternetDialog(callbackListener)
                callbackListener.onRetry()*/

            }
            builder.setPositiveButton("Close") { dialog, _ ->
                dialog!!.dismiss()
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(homeIntent)
                finishAffinity()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun isOnline(): Boolean {
        var outcome = false
        try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.allNetworkInfo
            for (tempNetworkInfo in networkInfo) {
                if (tempNetworkInfo.isConnected) {
                    outcome = true
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return outcome
    }

    fun openYoutube(strVideoLink: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strVideoLink))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.google.android.youtube");
        startActivity(intent)
    }

    override fun Okay() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
        finishAffinity()
    }

    override fun cancel() {

    }

    fun adDimLightProgressDialog(context: Context): Dialog {

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(R.layout.anim_ad_progress)

        val alDialog = builder.create()
//        alDialog.show()

        alDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        return alDialog
    }

    fun getDecimal(value:Double):String{
        return DecimalFormat("##.##").format(value)
    }
}
