/*
 * Copyright (C) 2022 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.humongouswin.insidefun.browser

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.humongouswin.insidefun.R
import com.github.humongouswin.insidefun.browser.VideoListAdapter.ItemClickListener
import com.github.humongouswin.insidefun.mediaplayer.LocalPlayerActivity
import com.github.humongouswin.insidefun.utils.MediaItem
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

/**
 * A fragment to host a list view of the video catalog.
 */
class VideoBrowserFragment : Fragment(), ItemClickListener,
    LoaderManager.LoaderCallbacks<List<MediaItem>?> {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: VideoListAdapter? = null
    private var mEmptyView: View? = null
    private var mLoadingView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.video_browser_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        mEmptyView = view.findViewById(R.id.empty_view)
        mLoadingView = view.findViewById(R.id.progress_indicator)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView!!.layoutManager = layoutManager
        mAdapter = VideoListAdapter(this, this.requireContext())
        mRecyclerView!!.adapter = mAdapter
        LoaderManager.getInstance(this).initLoader(0, null, this)

        val video_url = requireActivity().intent.getStringExtra("video_url")
        if (video_url != null) {
            sendGet(video_url)
        }
    }

    override fun itemClicked(v: View?, item: MediaItem?, position: Int) {
        val transitionName = getString(R.string.transition_image)
        val viewHolder =
            mRecyclerView!!.findViewHolderForLayoutPosition(position) as VideoListAdapter.ViewHolder?
        val imagePair = Pair
            .create(viewHolder!!.imageView as View, transitionName)
        val options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(requireActivity(), imagePair)
        val intent = Intent(activity, LocalPlayerActivity::class.java)
        intent.putExtra("media", item!!.toBundle())
        intent.putExtra("shouldStart", false)
        ActivityCompat.startActivity(requireActivity(), intent, options.toBundle())
    }

    private fun buildMediaInfo(
        title: String, studio: String, subTitle: String,
        duration: Int, url: String, mimeType: String?, imgUrl: String, bigImageUrl: String
    ): MediaItem {
        val media = MediaItem()
        media.url = url
        media.title = title
        media.subTitle = subTitle
        media.studio = studio
        media.addImage(imgUrl)
        media.addImage(bigImageUrl)
        media.contentType = mimeType
        media.duration = duration
        return media
    }

    private var mediaList: ArrayList<MediaItem>? = null
    fun sendGet(url: String){
        Thread {
            mediaList = ArrayList()
            val url = URL(url)
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"  // optional default is GET
                inputStream.bufferedReader().use {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        it.lines().forEach { line ->
                            val jsonObject = JSONObject(line)
                            val movies = jsonObject.getJSONArray("movies")
                            for (i in 0 until movies.length()) {
                                val movieJsonObject = movies.getJSONObject(i)
                                val title = movieJsonObject.get("title").toString()
                                val imageUrl = movieJsonObject.get("thumbnail").toString()
                                val bigImageUrl = movieJsonObject.get("thumbnail").toString()
                                val studio = "Google IO - 2014"
                                val subTitle = movieJsonObject.get("shortDescription").toString()
                                val contentJsonObject = movieJsonObject.getJSONObject("content")
                                val duration = contentJsonObject.get("duration") as Int
                                val videosJsonArray = contentJsonObject.getJSONArray("videos")
                                val videoJsonObject = videosJsonArray.getJSONObject(0)
                                val videoUrl = videoJsonObject.get("url").toString()
                                val mimeType = "videos/mp4"
                                mediaList!!.add(
                                    buildMediaInfo(
                                        title, studio, subTitle, duration, videoUrl,
                                        mimeType, imageUrl, bigImageUrl
                                    )
                                )
                            }

                           requireActivity().runOnUiThread {
                               mAdapter!!.setData(mediaList)
                               mLoadingView!!.visibility = View.GONE
                               mEmptyView!!.visibility = if (null == mediaList || mediaList!!.isEmpty()) View.VISIBLE else View.GONE
                           }
                        }
                    }
                }
            }
        }.start()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<MediaItem>?> {
        return VideoItemLoader(activity, CATALOG_URL)
    }

    override fun onLoadFinished(loader: Loader<List<MediaItem>?>, data: List<MediaItem>?) {
//        mAdapter!!.setData(data)
//        mLoadingView!!.visibility = View.GONE
//        mEmptyView!!.visibility = if (null == data || data.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onLoaderReset(loader: Loader<List<MediaItem>?>) {
//        mAdapter!!.setData(null)
    }

    companion object {
        private const val TAG = "VideoBrowserFragment"
        private const val CATALOG_URL =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/f.json"
    }


}