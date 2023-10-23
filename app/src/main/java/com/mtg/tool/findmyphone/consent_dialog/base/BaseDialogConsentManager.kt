package com.mtg.tool.findmyphone.consent_dialog.base

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.common.control.manager.AppOpenManager
import com.common.control.manager.PurchaseManager
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import com.mtg.tool.findmyphone.R

abstract class BaseDialogConsentManager {
    protected var isDialogLoading = false
    protected var consentInformation: ConsentInformation? = null
    private var consentForm: ConsentForm? = null
    private var dialog: ProgressDialog? = null
    private var retryTimes = 0
    fun showDialog(
        activity: Activity,
        showProgress: Boolean,
        onDismissedListener: ConsentForm.OnConsentFormDismissedListener
    ) {
        if (isDialogLoading) {
            if (showProgress) {
                dialog = ProgressDialog(activity)
                dialog!!.setMessage(activity.getString(R.string.loading) + " ...")
                dialog!!.setCancelable(false)
                dialog!!.show()
            }
            Thread {
                while (isDialogLoading) {
                    try {
                        Thread.sleep(30)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                activity.runOnUiThread {
                    if (dialog != null) {
                        dialog!!.dismiss()
                    }
                    showDialog(activity, showProgress, onDismissedListener)
                }
            }.start()
        } else {
            if (consentForm != null) {
                showConsentDialog(consentForm!!, activity, onDismissedListener)
                consentForm = null
            } else {
                onDismissedListener.onConsentFormDismissed(null)
            }
            loadDialogForm(activity, null)
        }
    }

    fun loadDialogForm(
        activity: Activity,
        loadFailListener: UserMessagingPlatform.OnConsentFormLoadFailureListener?
    ) {
        if (isDialogLoading) {
            return
        }
        isDialogLoading = true
        consentInformation = getConsentInformation(activity)
        consentInformation!!.requestConsentInfoUpdate(
            activity,
            getParams(activity),
            {
                UserMessagingPlatform.loadConsentForm(
                    activity.application,
                    { consentForm: ConsentForm? ->
                        this.consentForm = consentForm
                        isDialogLoading = false
                    }) { formError: FormError ->
                    if (loadFailListener != null) {
                        Log.d("LoadConsentForm:", formError.message)
                        retryTimes++
                        if (retryTimes == 3) {
                            retryTimes = 0
                            loadFailListener.onConsentFormLoadFailure(formError)
                        }
                    }
                    isDialogLoading = false
                    loadDialogForm(activity, loadFailListener)
                }
            }
        ) { requestConsentError: FormError ->
            // Consent gathering failed.
            retryTimes++
            if (retryTimes == 3) {
                retryTimes = 0
                onLoadFail(requestConsentError, loadFailListener)
            }
            isDialogLoading = false
            loadDialogForm(activity, loadFailListener)
        }
    }

    protected fun onLoadFail(
        requestConsentError: FormError,
        listener: UserMessagingPlatform.OnConsentFormLoadFailureListener?
    ) {
        Log.w(
            "ConsentTag", String.format(
                "%s: %s",
                requestConsentError.errorCode,
                requestConsentError.message
            )
        )
        listener?.onConsentFormLoadFailure(null)
    }

    private fun showConsentDialog(
        consentForm: ConsentForm,
        activity: Activity,
        dismissedListener: ConsentForm.OnConsentFormDismissedListener?
    ) {
        EventLogger.firebaseLog(activity, "show_gdpr")
        val isConsentedPrev = canRequestAds(activity)
        try {
            consentForm.show(activity) { formError: FormError? ->
                if (canRequestAds(activity)) {
                    AppOpenManager.getInstance().enableAppResume()
                } else {
                    AppOpenManager.getInstance().disableAppResume()
                }
                trackingAdsType(activity, isConsentedPrev)
                dismissedListener?.onConsentFormDismissed(formError)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun trackingAdsType(activity: Activity, isConsentedPrev: Boolean) {
        if (canShowAds(activity)) {
            if (!isConsentedPrev && !isFirstTracking(activity)) {
                EventLogger.firebaseLog(activity, "accept_show_ads")
            }
            if (canShowPersonalizedAds(activity)) {
                EventLogger.firebaseLog(activity, "show_person_ads")
                return
            }
            if (canShowNonPersonalAds(activity)) {
                EventLogger.firebaseLog(activity, "show_non_person_ads")
                return
            }
        } else {
            if (isConsentedPrev) {
                EventLogger.firebaseLog(activity, "revoke_show_ads")
            }
            EventLogger.firebaseLog(activity, "not_show_ads")
        }
    }

    private fun isFirstTracking(activity: Activity): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val isFirst = prefs.getBoolean(IS_FIRST_TRACKING, true)
        if (isFirst) {
            val editor = prefs.edit()
            editor.putBoolean(IS_FIRST_TRACKING, false)
            editor.apply()
        }
        return isFirst
    }

    protected fun consentRequired(activity: Activity?): Boolean {
        val prefs =PreferenceManager.getDefaultSharedPreferences(activity)
        return prefs.getBoolean(IS_REQUIRED_CONSENT, false) && !PurchaseManager.getInstance().isPurchased
    }

    protected fun setRequiredConsentFirstOpen(activity: Activity) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val isFirst = prefs.getBoolean(IS_FIRST_REQUEST_CONSENT, true)
        if (isFirst) {
            val editor = prefs.edit()
            editor.putBoolean(IS_FIRST_REQUEST_CONSENT, false)
            editor.apply()
            editor.putBoolean(
                IS_REQUIRED_CONSENT,
                getConsentInformation(activity).consentStatus == ConsentInformation.ConsentStatus.REQUIRED
            )
            editor.apply()
        }
    }

    fun requestConsentInfor(
        activity: Activity,
        successListener: ConsentInformation.OnConsentInfoUpdateSuccessListener?,
        failureListener: ConsentInformation.OnConsentInfoUpdateFailureListener?
    ) {
        consentInformation = UserMessagingPlatform.getConsentInformation(activity.application)
        consentInformation?.requestConsentInfoUpdate(
            activity,
            getParams(activity),
            ConsentInformation.OnConsentInfoUpdateSuccessListener { successListener?.onConsentInfoUpdateSuccess() },
            ConsentInformation.OnConsentInfoUpdateFailureListener { requestConsentError: FormError ->
                // Consent gathering failed.
                failureListener?.onConsentInfoUpdateFailure(requestConsentError)
                Log.w(
                    "ConsentTag", String.format(
                        "%s: %s",
                        requestConsentError.errorCode,
                        requestConsentError.message
                    )
                )
            })
    }

    protected fun getParams(activity: Activity?): ConsentRequestParameters {
        return if (IS_SHOW_TEST_CONSENT) {
            val debugSettings =
                ConsentDebugSettings.Builder(activity)
                    .addTestDeviceHashedId("917B0AD01B291CD4C4FD096D8523BFCC")
                    .addTestDeviceHashedId("04477AAC8706BC1069DDBE9A1ECC1043")
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .build()
            ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .setConsentDebugSettings(debugSettings)
                .build()
        } else {
            ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .build()
        }
    }

    protected fun getConsentInformation(activity: Activity): ConsentInformation {
        return UserMessagingPlatform.getConsentInformation(activity.application)
    }

    fun canRequestAds(activity: Activity): Boolean {
        requestConsentInfor(activity, null, null)
        return canShowAds(activity)
    }

    private fun showToast(activity: Activity) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val consents = prefs.getString(KEY_PURPOSE_CONSENTS_STRING, "")

//        Toast.makeText(activity, "Can show Non_personalized Ads: " + canShowNonPersonalAds(activity), Toast.LENGTH_LONG).show();
//        Toast.makeText(activity, "Can show personalized Ads: " + canShowPersonalizedAds(activity), Toast.LENGTH_LONG).show();
//        Toast.makeText(activity, "Can show Ads: " + canShowAds(activity), Toast.LENGTH_LONG).show();
        val purposeConsent = prefs.getString(KEY_PURPOSE_CONSENTS_STRING, "")
        Toast.makeText(
            activity,
            "ConsentAt: 2" + hasAttribute(purposeConsent, 2),
            Toast.LENGTH_SHORT
        ).show()
        Toast.makeText(
            activity,
            "ConsentAt: 9" + hasAttribute(purposeConsent, 9),
            Toast.LENGTH_SHORT
        ).show()
        Toast.makeText(
            activity,
            "ConsentAt: 13" + hasAttribute(purposeConsent, 13),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun canShowNonPersonalAds(activity: Activity): Boolean {
        return !canShowPersonalizedAds(activity) && canShowAds(activity)
    }

    private fun canShowAds(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val purposeConsent = prefs.getString(KEY_PURPOSE_CONSENTS_STRING, "")
        val vendorConsent = prefs.getString(KEY_VENDOR_CONSENTS_STRING, "")
        val vendorLI = prefs.getString(KEY_VENDOR_LEGI_INSTEREST_STRING, "")
        val purposeLI = prefs.getString(KEY_PUR_LEGI_INTEREST_STRING, "")
        val googleId = 755
        val hasGoogleVendorConsent = hasAttribute(vendorConsent, googleId)
        val hasGoogleVendorLI = hasAttribute(vendorLI, googleId)

        // Minimum required for at least non-personalized ads
        return (hasConsentFor(intArrayOf(1), purposeConsent, hasGoogleVendorConsent)
                && hasConsentOrLegitimateInterestFor(
            intArrayOf(2, 7, 9, 10),
            purposeConsent,
            purposeLI,
            hasGoogleVendorConsent,
            hasGoogleVendorLI
        ))
    }

    fun canShowPersonalizedAds(context: Context?): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val purposeConsent = prefs.getString(KEY_PURPOSE_CONSENTS_STRING, "")
        val vendorConsent = prefs.getString(KEY_VENDOR_CONSENTS_STRING, "")
        val vendorLI = prefs.getString(KEY_VENDOR_LEGI_INSTEREST_STRING, "")
        val purposeLI = prefs.getString(KEY_PUR_LEGI_INTEREST_STRING, "")
        val googleId = 755
        val hasGoogleVendorConsent = hasAttribute(vendorConsent, googleId)
        val hasGoogleVendorLI = hasAttribute(vendorLI, googleId)
        return (hasConsentFor(intArrayOf(1, 3, 4), purposeConsent, hasGoogleVendorConsent)
                && hasConsentOrLegitimateInterestFor(
            intArrayOf(2, 7, 9, 10),
            purposeConsent,
            purposeLI,
            hasGoogleVendorConsent,
            hasGoogleVendorLI
        ))
    }

    private fun hasAttribute(input: String?, index: Int): Boolean {
        return input!!.length >= index && input[index - 1] == '1'
    }

    private fun hasConsentFor(
        purposes: IntArray,
        purposeConsent: String?,
        hasVendorConsent: Boolean
    ): Boolean {
        for (p in purposes) {
            if (!hasAttribute(purposeConsent, p)) {
                return false
            }
        }
        return hasVendorConsent
    }

    private fun hasConsentOrLegitimateInterestFor(
        purposes: IntArray,
        purposeConsent: String?,
        purposeLI: String?,
        hasVendorConsent: Boolean,
        hasVendorLI: Boolean
    ): Boolean {
        for (p in purposes) {
            if (hasAttribute(purposeLI, p) && hasVendorLI || hasAttribute(
                    purposeConsent,
                    p
                ) && hasVendorConsent
            ) {
                return true
            }
        }
        return false
    }

    fun resetDevice(activity: Activity) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity.application)
        consentInformation.reset()
    }

    companion object {
        private const val IS_SHOW_TEST_CONSENT = false
        private const val IS_REQUIRED_CONSENT = "IS_REQUIRED_CONSENT"
        private const val IS_FIRST_REQUEST_CONSENT = "IS_FIRST_REQUEST_CONSENT"
        private const val KEY_PURPOSE_CONSENTS_STRING = "IABTCF_PurposeConsents"
        private const val KEY_VENDOR_CONSENTS_STRING = "IABTCF_VendorConsents"
        private const val KEY_VENDOR_LEGI_INSTEREST_STRING = "IABTCF_VendorLegitimateInterests"
        private const val KEY_PUR_LEGI_INTEREST_STRING = "IABTCF_PurposeLegitimateInterests"
        private const val IS_FIRST_TRACKING = "IS_FIRST_TRACKING"
    }
}