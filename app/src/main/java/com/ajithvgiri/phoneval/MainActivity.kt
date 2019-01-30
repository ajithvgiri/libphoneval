package com.ajithvgiri.phoneval

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ajithvgiri.libphoneval.NumberParseException
import com.ajithvgiri.libphoneval.PhoneNumberUtil
import com.ajithvgiri.libphoneval.Phonenumber
import com.ajithvgiri.phoneval.utils.AppUtils
import com.ajithvgiri.phoneval.utils.CheckPermissionResult
import com.ajithvgiri.phoneval.utils.LogType
import com.ajithvgiri.phoneval.utils.PermissionHandler
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity() {

    companion object {

        private val TAG = MainActivity::class.java.simpleName
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var view: View
    private lateinit var phoneNumberUtil: PhoneNumberUtil
    private val parsedContactList = HashSet<Phonenumber.PhoneNumber>()
    private var validatedNumbers: HashSet<String> = HashSet()
    private var start = ""
    private var end = ""

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
                            return
                        }
                    }
                }
            }
        }
    }


    private fun getContacts() {
        AppUtils.printLog(TAG, "Contact Fetching Start ", LogType.INFO)

        start = Date(System.currentTimeMillis()).toString()

        doAsync{
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val selection = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1"
            //val selectionArgs = arrayOf("1")
            val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"

            val contentResolver = contentResolver
            val cur = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                null,
                sort
            )
            //val qkopyContactList = ArrayList<QkopyContacts>()

            if (cur != null && cur.count > 0 && cur.moveToFirst()) {
                parsedContactList.clear()
                do {
                    var phoneNo = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    if (!phoneNo.isNullOrEmpty()) {
                        try {
                            val parsedNumber = phoneNumberUtil.parse(phoneNo, "IN")
                            parsedContactList.add(parsedNumber)
                        } catch (numberParseException: NumberParseException) {
                            AppUtils.printLog(TAG, numberParseException.message, LogType.ERROR)
                        } catch (e: Exception) {
                            AppUtils.printLog(TAG, e.message, LogType.ERROR)
                        }
                    }
                } while (cur.moveToNext())

                cur.close()

                end = Date(System.currentTimeMillis()).toString()
                AppUtils.printLog(TAG, "Contact Fetching Finish ", LogType.INFO)
                AppUtils.printLog(TAG, "Total Contacts Count ${parsedContactList.size}", LogType.INFO)

                validatedNumbers = phoneNumberUtil.validNumbers(parsedContactList)

                AppUtils.printLog(TAG, "Total Filtered Contacts Count ${validatedNumbers.size}", LogType.INFO)
            } else {
                AppUtils.printLog(TAG, " No phone contact found for ", LogType.WARNING)
            }

            uiThread {
                textTotalContacts.text = "${parsedContactList.size}"
                textViewStartEndTime.text = "$start ----- $end"
                textViewValidContacts.text = "${validatedNumbers.size}"
            }
        }




    }
}
