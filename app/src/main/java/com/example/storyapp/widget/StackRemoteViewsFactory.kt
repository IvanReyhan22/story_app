package com.example.storyapp.widget

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.ui.model.StoriesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private var stories: List<Story?> = emptyList()
    private val widgetItems = ArrayList<Bitmap>()
    private lateinit var viewModel: StoriesViewModel

    override fun onCreate() {
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(Application())
            .create(StoriesViewModel::class.java)
    }

    override fun onDataSetChanged() {
        getData()
    }

    private fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            val client = ApiConfig.getApiService(mContext).getAllStories(0, 1, 10)

            try {
                if (client.error == false) {
                    stories = client.listStory
                    stories.forEach { story ->
                        val bitmap = Glide.with(mContext)
                            .asBitmap()
                            .load(story?.photoUrl)
                            .submit()
                            .get()
                        widgetItems.add(bitmap)
                    }
                }
//                val response = client.execute()
//                if (response.isSuccessful) {
//                    val body: StoryResponse = response.body()!!
//                    if (body.error == false) {
//                        stories = body.listStory
//                        stories.forEach { story ->
//                            val bitmap = Glide.with(mContext)
//                                .asBitmap()
//                                .load(story?.photoUrl)
//                                .submit()
//                                .get()
//                            widgetItems.add(bitmap)
//                        }
//                    }
//                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = widgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, widgetItems[position])


        val extras = bundleOf(
            StoriesWidget.EXTRA_ITEM_NAME to stories[position]?.name
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}