package com.ajithvgiri.phoneval

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import com.ajithvgiri.libphoneval.PhoneNumberUtil
import com.ajithvgiri.libphoneval.model.PhoneModel
import com.ajithvgiri.phoneval.utils.AppUtils
import com.ajithvgiri.phoneval.utils.CheckPermissionResult
import com.ajithvgiri.phoneval.utils.LogType
import com.ajithvgiri.phoneval.utils.PermissionHandler
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet



class MainActivity : AppCompatActivity() {

    companion object {

        private val TAG = MainActivity::class.java.simpleName
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var view: View
    private lateinit var phoneNumberUtil: PhoneNumberUtil
    private val newContactList = HashSet<PhoneModel>()
    private val newArrayContactList = ArrayList<PhoneModel>()
    private val ctsContactList = ArrayList<String>()
    private var newValidatedNumbers: HashSet<PhoneModel> = HashSet()
    private var start = ""
    private var end = ""

    override fun onStart() {
        super.onStart()
        start = Date(System.currentTimeMillis()).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phoneNumberUtil = PhoneNumberUtil.createInstance(this)

        view = window.decorView.findViewById(android.R.id.content)

        PermissionHandler.checkPermission(this, Manifest.permission.READ_CONTACTS) { result ->
            when (result) {
                CheckPermissionResult.PermissionGranted -> {
                    getContacts()
                }
                CheckPermissionResult.PermissionDisabled -> {
                    AppUtils.snackMessage(view, getString(R.string.no_permission_granted))
                }
                CheckPermissionResult.PermissionAsk -> {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                        Companion.PERMISSION_REQUEST_CODE
                    )
                }
                CheckPermissionResult.PermissionPreviouslyDenied -> {
                    AppUtils.snackMessage(view, getString(R.string.permission_denied))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val readContactPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writeContactPermissionAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (readContactPermissionAccepted && writeContactPermissionAccepted) {
                    AppUtils.snackMessage(view, getString(R.string.permission_success))
                    getContacts()
                } else {
                    AppUtils.snackMessage(view, getString(R.string.permission_failure))
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                            greetings()
                            return
                        }
                    }
                }
            }
        }
    }


    fun greetings(){
        print("Hello, world !")
    }

    /**
     * Ajith v Giri
     * https://ajithvgiri.com
     */


    private fun getContacts() {
        AppUtils.printLog(TAG, "Contact Fetching Start ", LogType.INFO)

        val countryCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0].country
        } else {
            Locale.getDefault().country
        }

        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue = tm.networkCountryIso

        println("Country code $countryCode")
        println("Country code $countryCodeValue")

        doAsync {
            Log.d(TAG, "ContactSync Start ")
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            )
            val selection = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "= 1"
            val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            val contentResolver = contentResolver

            val cur = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                null,
                sort
            )

            val totalContactsArrayList = ArrayList<PhoneModel>()
            val totalContactsHashList = HashSet<PhoneModel>()
            val formattedFinalContactsList = ArrayList<PhoneModel>()
            val validFinalContactsList = ArrayList<PhoneModel>()

            if (cur != null && cur.count > 0 && cur.moveToFirst()) {
                do {
                    val rawId = cur.getLong(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))
                    val contactId = cur.getLong(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phoneNo = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    if (!phoneNo.isNullOrEmpty()) {
                        val phoneModel = PhoneModel(rawId,contactId,name,phoneNo,null)
                        totalContactsArrayList.add(phoneModel)
                        totalContactsHashList.add(phoneModel)
//                        try {
//                            val parsedNumber = phoneNumberUtil.parse(phoneNo, countryCodeValue.toUpperCase())
//                            val formatted = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
//                            formattedFinalContactsList.add(formatted)
//                            if (phoneNumberUtil.isValidNumber(parsedNumber)){
//                                validFinalContactsList.add(formatted)
//                            }else{
//                                println("not a valid number - $formatted")
//                            }
//                        } catch (e: NumberParseException) {
//                            e.message
//                        } catch (e: Exception) {
//                            e.message
//                        }
                    }
                } while (cur.moveToNext())

                cur.close()


                val parseNumber = phoneNumberUtil.parseUsingModel(totalContactsHashList,countryCodeValue.toUpperCase())

                val formatted = phoneNumberUtil.formatModel(parseNumber,PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)

                formatted.forEach {
                    println("parseNumber ${it.phone}")
                }

                val validatedNumbers =phoneNumberUtil.checkValidNumbersModel(formatted,countryCodeValue.toUpperCase())

                Log.d(TAG,"Total contacts verification in phoneVal Hashset = ${totalContactsHashList.size}")
                Log.d(TAG,"Total contacts verification in phoneVal Hashset = ${totalContactsHashList.distinctBy { it.phone }.size}")

                Log.d(TAG,"Total contacts verification in phoneVal  = ${totalContactsArrayList.size}")
                Log.d(TAG,"Total contacts verification in phoneVal distinct() = ${totalContactsArrayList.distinctBy { it.phone }.size}")

                Log.d(TAG,"Total contacts formatted in phoneVal = ${formatted.size}")
                Log.d(TAG,"Total contacts formatted in phoneVal distinct() = ${formatted.distinctBy { it.phone }.size}")

                Log.d(TAG,"Total contacts valid in phoneVal = ${validatedNumbers.size}")
                Log.d(TAG,"Total contacts valid in phoneVal distinct() = ${validatedNumbers.distinctBy { it.phone }.size}")

            }else{
                Log.d(TAG,"No contacts in phoneVal ")
            }

            uiThread {
                textTotalContacts.text = "${newContactList.size}"
                textViewStartEndTime.text = "$start ----- $end"
                textViewValidContacts.text = "${newValidatedNumbers.size}"
            }
        }


    }
}
