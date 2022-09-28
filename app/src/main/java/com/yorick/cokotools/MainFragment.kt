package com.yorick.cokotools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yorick.cokotools.databinding.FragmentMainBinding
import com.yorick.cokotools.util.Utils

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
//        binding.buttonEngineerMode.visibility = View.GONE
        binding.buttonLockBands.setOnClickListener {
            context?.let {
                val activityExisting = Utils.isActivityExisting(
                    it,
                    "com.vivo.networkstate",
                    "com.vivo.networkstate.MainActivity"
                )
                if(!activityExisting){
                    MaterialAlertDialogBuilder(it)
                        .setTitle(resources.getString(R.string.compose_needed))
                        .setMessage(resources.getString(R.string.download_network_state))
                        .setNeutralButton(resources.getString(R.string.cancel)){ dialog, which ->
                        }
                        .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->

                        }
                        .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->

                        }
                        .show()
                }else{
                    Toast.makeText(it,resources.getString(R.string.compose_satisfied), LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}