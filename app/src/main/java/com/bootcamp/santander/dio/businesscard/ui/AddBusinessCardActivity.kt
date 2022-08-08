package com.bootcamp.santander.dio.businesscard.ui

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import com.bootcamp.santander.dio.businesscard.App
import com.bootcamp.santander.dio.businesscard.R
import com.bootcamp.santander.dio.businesscard.data.BusinessCard
import com.bootcamp.santander.dio.businesscard.databinding.ActivityAddBusinessCardBinding
import com.google.android.material.snackbar.Snackbar

class AddBusinessCardActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddBusinessCardBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as App).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        insertListeners()
    }

    private fun insertListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }
        binding.btnConfirm.setOnClickListener {
            validate()?.let { businessCard ->
                mainViewModel.insert(businessCard)
                Toast.makeText(this, R.string.label_show_success, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

    private fun validate(): BusinessCard? {
        val nome = binding.tilNome.editText?.text
        val empresa = binding.tilEmpresa.editText?.text
        val telefone = binding.tilTelefone.editText?.text
        val email = binding.tilEmail.editText?.text
        val fundoPersonalizado = binding.tilCor.editText?.text

        if (!nome.isNullOrBlank()) {
            if (!telefone.isNullOrBlank()) {
                if (!email.isNullOrBlank()) {
                    if (!empresa.isNullOrBlank()) {
                        if (!fundoPersonalizado.isNullOrBlank()) {
                            return BusinessCard(
                                nome = nome.toString(),
                                empresa = empresa.toString(),
                                telefone = telefone.toString(),
                                email = email.toString(),
                                fundoPersonalizado = fundoPersonalizado.toString()
                            )
                        } else {
                            showSnackbar(R.string.message_type_background_color)
                        }
                    } else {
                        showSnackbar(R.string.message_type_the_company_name)
                    }
                } else {
                    showSnackbar(R.string.message_type_your_email)
                }
            } else {
                showSnackbar(R.string.message_type_your_phone_number)
            }
        } else {
            showSnackbar(R.string.message_type_your_name)
        }
        return null
    }

    private fun showSnackbar(@StringRes message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}