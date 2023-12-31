package com.apogee.geomaster.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apogee.geomaster.R
import com.apogee.geomaster.databinding.SatelliteItemLayoutBinding
import com.apogee.geomaster.model.SatelliteModel

typealias listener = (data: SatelliteModel) -> Unit

class SatelliteScreenAdaptor(private val itemClicked: listener) :
    ListAdapter<SatelliteModel, SatelliteScreenAdaptor.SatelliteViewHolder>(diffUtil) {
    inner class SatelliteViewHolder(private val binding: SatelliteItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isCheck = true
        fun setData(data: SatelliteModel, itemClicked: listener) {
            binding.cardView.setOnClickListener {
                itemClicked.invoke(data)
                binding.checkInfo.isVisible = isCheck
                isCheck = !isCheck
            }
            binding.satelliteInfo.text = data.satelliteName
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SatelliteModel>() {
            override fun areItemsTheSame(
                oldItem: SatelliteModel,
                newItem: SatelliteModel
            ) = oldItem.satelliteName == newItem.satelliteName

            override fun areContentsTheSame(
                oldItem: SatelliteModel,
                newItem: SatelliteModel
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SatelliteViewHolder {
        val binding =
            SatelliteItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SatelliteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SatelliteViewHolder, position: Int) {
        val currItem = getItem(position)
        val animation=AnimationUtils.loadAnimation(holder.itemView.context, R.anim.enter_anim_layout)
        holder.itemView.startAnimation(animation)
        currItem?.let {
            holder.setData(it, itemClicked)
        }
    }

}