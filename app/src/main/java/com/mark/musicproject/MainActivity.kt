package com.mark.musicproject

import android.app.Service
import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mark.musicproject.databinding.ActivityMainBinding
import com.mark.musicproject.db.models.TrackItem
import com.mark.musicproject.services.PlayerService

class MainActivity : AppCompatActivity(), TrackItemsAdapter.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TrackItemsAdapter

    private var mBound: Boolean = false
    private var service: PlayerService? = null

    private val db = MusicApplication.instance.db

    private val serviceConnector = object : ServiceConnection{
        override fun onServiceConnected(component: ComponentName?, binder: IBinder?) {
            service = (binder as PlayerService.ServiceBinder).service
            mBound = true
        }

        override fun onServiceDisconnected(component: ComponentName?) {
            mBound = false
            service = null
        }
    }


    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                PlayerService.ACTION_PAUSE -> pauseTrack()
                PlayerService.ACTION_PLAY -> playTrack()
                PlayerService.ACTION_STOP -> stopPlaying()
            }
        }
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel=ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.tracks.observe(this,{
            adapter.items.clear()
            adapter.items.addAll(it)
            adapter.notifyDataSetChanged()
        })

        viewModel.state.observe(this,{
            when(it){
                is MainViewModel.State.TrackSelected->{
                    service?.stop()
                    service?.play(it.track)
                    it.track.isSelected = true
                    it.track.isPlaying = true
                    adapter.items.getOrNull(adapter.prevPlayed)?.let {item->
                        item.isSelected = false
                        item.isPlaying = false
                        adapter.notifyItemChanged(adapter.prevPlayed)
                    }
                    adapter.prevPlayed = it.position
                    adapter.notifyItemChanged(it.position)
                    binding.layoutPlayerController.visibility = View.VISIBLE
                    binding.buttonPlay.isSelected = true
                }
            }
        })

        initAdapter()
        bindToService()
        initItems()
        getItems()

        registerReceiver(receiver, IntentFilter().apply {
            addAction(PlayerService.ACTION_STOP)
            addAction(PlayerService.ACTION_PLAY)
            addAction(PlayerService.ACTION_PAUSE)
        })

        binding.buttonPlay.setOnClickListener {
            adapter.items.getOrNull(adapter.prevPlayed)?.let {
                if(it.isPlaying)
                    pauseTrack()
                else
                    playTrack()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_add_track){
            startActivity(Intent(this, TrackAddActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pauseTrack(){
        service?.pause()
        binding.buttonPlay.isSelected = false
        adapter.items.getOrNull(adapter.prevPlayed)?.let {
            it.isPlaying = false
            adapter.notifyItemChanged(adapter.prevPlayed)
        }
    }

    private fun playTrack(){
        service?.resume()
        binding.buttonPlay.isSelected = true
        adapter.items.getOrNull(adapter.prevPlayed)?.let {
            it.isPlaying = true
            adapter.notifyItemChanged(adapter.prevPlayed)
        }
    }

    private fun stopPlaying(){
        service?.stop()
        binding.buttonPlay.isSelected = false
        binding.layoutPlayerController.visibility = View.GONE
        adapter.items.getOrNull(adapter.prevPlayed)?.let {
            it.isPlaying = false
            it.isSelected = false
            adapter.notifyItemChanged(adapter.prevPlayed)
            adapter.prevPlayed = -1
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)
        unbindFromService()
    }

    private fun initAdapter() {
        adapter = TrackItemsAdapter(arrayListOf())
        adapter.listener = this
        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(this)
    }

    private fun bindToService() {
        if(!mBound)
            bindService(
                Intent(this, PlayerService::class.java),
                serviceConnector,
                Service.BIND_AUTO_CREATE
            )
    }

    private fun unbindFromService() {
        if(mBound){
            unbindService(serviceConnector)
            mBound = false
            service = null
        }
    }

    private fun initItems() {
        val musicDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val musicFiles = musicDir?.listFiles() ?: arrayOf()
        val tracks = musicFiles.map {
            val fileName = it.name.split(".").firstOrNull() ?: ""
            val fileNameSplit = fileName.split("-")
            TrackItem(
                fileNameSplit.firstOrNull() ?: "",
                fileNameSplit.getOrNull(1) ?: "",
                Uri.fromFile(it).toString()
            )
        }
        viewModel.addTracks(tracks)
    }

    private fun getItems(){
        viewModel.getTracks()
    }

    override fun onClick(item: TrackItem, position: Int) {
        if(item.isSelected){
            if (item.isPlaying){
                pauseTrack()
            } else {
                playTrack()
            }
        } else{
            viewModel.selectTrack(item, position)
        }
    }
}