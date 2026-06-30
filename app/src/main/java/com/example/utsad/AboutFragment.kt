package com.example.utsad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDateAbout = view.findViewById<TextView>(R.id.tv_date_about)
        val dateFormat = java.text.SimpleDateFormat("dd MMMM\nyyyy", java.util.Locale.forLanguageTag("id-ID"))
        tvDateAbout.text = dateFormat.format(java.util.Date())

        //Setup Instagram Buttons
        view.findViewById<TextView>(R.id.btn_instagram_1).setOnClickListener {
            openUrl("https://www.instagram.com/rjoshn")
        }
        view.findViewById<TextView>(R.id.btn_instagram_2).setOnClickListener {
            openUrl("https://www.instagram.com/rafifauziz")
        }
        view.findViewById<TextView>(R.id.btn_instagram_3).setOnClickListener {
            openUrl("https://www.instagram.com/torikh_abdullah")
        }
        view.findViewById<TextView>(R.id.btn_instagram_4).setOnClickListener {
            openUrl("https://www.instagram.com/fanidwrn")
        }
        view.findViewById<TextView>(R.id.btn_instagram_5).setOnClickListener {
            openUrl("https://www.instagram.com/damardhnii")
        }

        //Setup LinkedIn Buttons
        view.findViewById<TextView>(R.id.btn_linkedin_1).setOnClickListener {
            openUrl("https://www.linkedin.com/search/results/all/?keywords=Rapolo%20Joshua%20Napitupulu")
        }
        view.findViewById<TextView>(R.id.btn_linkedin_2).setOnClickListener {
            openUrl("https://www.linkedin.com/in/rafi-fauzi-alfariz-0570aa2bb/")
        }
        view.findViewById<TextView>(R.id.btn_linkedin_3).setOnClickListener {
            openUrl("https://www.linkedin.com/in/torikh-abdullah-naser-80a738320/")
        }
        view.findViewById<TextView>(R.id.btn_linkedin_4).setOnClickListener {
            openUrl("https://www.linkedin.com/in/fanidwiariyanti/")
        }
        view.findViewById<TextView>(R.id.btn_linkedin_5).setOnClickListener {
            openUrl("https://www.linkedin.com/in/damar-kusumawardhani/")
        }
    }
    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tidak dapat membuka tautan", Toast.LENGTH_SHORT).show()
        }
    }
}
