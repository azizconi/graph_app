package com.example.graph_app.presentation.chart

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.example.graph_app.R
import com.example.graph_app.core.utils.BaseFragment
import com.example.graph_app.databinding.FragmentChartBinding
import com.example.graph_app.domain.model.PointModel
import com.example.graph_app.presentation.common.CoordinatesAdapter
import com.example.graph_app.presentation.common.getDialog
import java.io.File
import java.io.FileOutputStream

class ChartFragment : BaseFragment<FragmentChartBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChartBinding::inflate

    private val adapter = CoordinatesAdapter()

    private val args: ChartFragmentArgs by navArgs()
    private val points by lazy { args.points.toList().sortedBy { it.x } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupToolbar()
        setupRecyclerView()

    }

    private fun setupToolbar() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        requireContext().getDialog(
                            message = "Вы действительно хотите сохранить график",
                            positiveButtonText = "Да",
                            negativeButtonText = "Нет",
                            onPositiveButtonClick = { saveChart() }
                        ).show()
                        true
                    }

                    else -> false
                }

            }
        }
    }

    private fun setupRecyclerView() {
        adapter.items = PointModel.toPointList(points)

        binding.graphView.setPoints(points)
        binding.coordinatesRecyclerView.adapter = adapter
    }

    private fun saveChart() {
        val bitmap = binding.graphView.bitmap ?: binding.graphView.getGraphBitmap()
        val fileName = "graph_${System.currentTimeMillis()}.png"

        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(picturesDir, fileName)
            contentValues.put(MediaStore.Images.Media.DATA, file.absolutePath)
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }

        if (uri != null) {
            try {
                val outputStream = resolver.openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    Toast.makeText(requireContext(), "Изображение сохранено в галерею", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Не удалось открыть поток для записи", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Не удалось создать URI для изображения", Toast.LENGTH_SHORT).show()
        }
    }
}