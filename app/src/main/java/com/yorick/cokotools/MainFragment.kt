package com.yorick.cokotools

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yorick.cokotools.databinding.FragmentFirstBinding
import com.yorick.cokotools.util.Utils

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
        binding.buttonEngineerMode.visibility = View.GONE
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
                            // Respond to negative button press
                        }
                        .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                            // Respond to negative button press
                        }
                        .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                            // Respond to positive button press
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