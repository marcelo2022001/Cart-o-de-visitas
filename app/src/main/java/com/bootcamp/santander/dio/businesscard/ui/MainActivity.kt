package com.bootcamp.santander.dio.businesscard.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bootcamp.santander.dio.businesscard.App
import com.bootcamp.santander.dio.businesscard.R
import com.bootcamp.santander.dio.businesscard.databinding.ActivityMainBinding
import com.bootcamp.santander.dio.businesscard.util.Image
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as App).repository)
    }
    private val adapter by lazy { BusinessCardAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpPermissions()
        binding.rvCards.adapter = adapter
        getAllBusinessCard()
        insertListeners()
    }

    private fun setUpPermissions() {
        // write permission to access the storage
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
    }

    private fun insertListeners() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddBusinessCardActivity::class.java)
            startActivity(intent)
        }
        adapter.listenerShare = { card ->

            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.label_quer_compratilhar))
                .setNegativeButton(getString(R.string.label_cancelar)) { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.label_compartilhar)) { dialog, which ->
                    Image.share(this@MainActivity, card)
                }

            dialog.show()
        }
    }

    private fun getAllBusinessCard() {
        mainViewModel.getAll().observe(this) { businessCards ->
            adapter.submitList(businessCards)
        }
    }


}