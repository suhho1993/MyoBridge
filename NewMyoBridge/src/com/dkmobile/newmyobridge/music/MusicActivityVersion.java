package com.dkmobile.newmyobridge.music;

import com.dkmobile.newmyobridge.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore.Audio;

public class MusicActivityVersion extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_activity);
	}
	
	public void playPauseMusic(){
		Context ctx = getApplicationContext();
		
		//set audioManager
		AudioManager mAudioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
		
		//set musicService intent
		Intent mediaIntent = new Intent("com.android.music.musicservicecommand");
		//if playing pause
		if(mAudioManager.isMusicActive()){
			mediaIntent.putExtra("command", "pause");
		}
		//vice versa
		else
			mediaIntent.putExtra("command", "play"); 
		
		ctx.sendBroadcast(mediaIntent);
	}
	
	public void backMusic(){
		Context ctx = getApplicationContext();
		
		AudioManager mAudioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
		//play previous when music is playing
		if(mAudioManager.isMusicActive()){
			Intent mediaIntent = new Intent("com.android.music.musicservicecommand");
			mediaIntent.putExtra("command", "previous");
			sendBroadcast(mediaIntent);
		}
	}
	
	public void skipMusic(){
		Context ctx = getApplicationContext();
		
		AudioManager mAudioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
		//play back when music is playing
		if(mAudioManager.isMusicActive()){
			Intent mediaIntent = new Intent("com.android.music.musicservicecommand");
			mediaIntent.putExtra("command", "next");
			sendBroadcast(mediaIntent);
		}
	}
}
