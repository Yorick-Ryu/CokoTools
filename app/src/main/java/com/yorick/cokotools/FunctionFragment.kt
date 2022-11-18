package com.yorick.cokotools

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yorick.cokotools.databinding.FragmentFunctionBinding
import com.yorick.cokotools.util.Utils

class FunctionFragment : Fragment() {
    private var _binding: FragmentFunctionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFunctionBinding.inflate(inflater, container, false)
        binding.buttonLockBands.setOnClickListener {
            Utils.jumpActivity(
                requireContext(),
                resources.getString(R.string.lock_bands_package),
                resources.getString(R.string.lock_bands_activity),
                resources.getString(R.string.download_network_state)
            )
        }

        binding.buttonShowSeconds.setOnClickListener {
            Utils.jumpActivity(
                requireContext(),
                resources.getString(R.string.show_seconds_package),
                resources.getString(R.string.show_seconds_activity),
                okMsg = resources.getString(R.string.show_seconds_tips)
            )
        }

        binding.buttonEngineerMode.setOnClickListener {
            Utils.jumpActivity(
                requireContext(),
                resources.getString(R.string.engineer_mode_package),
                resources.getString(R.string.engineer_mode_activity),
                resources.getString(R.string.download_engineer_model)
            )
        }
        binding.buttonFuelSummary.setOnClickListener {
            Utils.jumpActivity(
                requireContext(),
                resources.getString(R.string.fuel_summary_package),
                resources.getString(R.string.fuel_summary_activity),
                okMsg = resources.getString(R.string.fuel_summary_tips)
            )
        }

        binding.buttonZenMode.setOnClickListener {
            Utils.jumpActivity(
                requireContext(),
                resources.getString(R.string.zen_mode_package),
                resources.getString(R.string.zen_mode_activity),
            )
        }

        binding.buttonShowWifiKeys.setOnClickListener {
            // 系统为Android12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Utils.baseDialog(
                    requireContext(),
                    title = resources.getString(R.string.title_tip),
                    msg = resources.getString(R.string.msg_high_version),
                    neutral = resources.getString(R.string.open_wifi_list),
                    neutralCallback = {
                        Utils.jumpActivity(
                            requireContext(),
                            resources.getString(R.string.show_wifi_keys_package),
                            resources.getString(R.string.show_wifi_list_activity),
                        )
                    },
                    positiveCallback = {},
                    negative = ""
                )
            } else {
                Utils.jumpActivity(
                    requireContext(),
                    resources.getString(R.string.show_wifi_keys_package),
                    resources.getString(R.string.show_wifi_keys_activity),
                    okMsg = resources.getString(R.string.show_wifi_key_tips)
                )
            }
        }

        binding.buttonMaxCharging.setOnClickListener {
            Utils.jumpActivity(
                requireContext(),
                resources.getString(R.string.max_charging_package),
                resources.getString(R.string.max_charging_activity),
                resources.getString(R.string.download_fuel_summary),
                resources.getString(R.string.max_charging_tips)
            )
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}