package demo.aide.karaoke;

import android.content.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class KaraokePlayer implements SeekBar.OnSeekBarChangeListener {

	private Context mContext;
	private MediaPlayer player;
	private boolean isPaused = false;
	private boolean pass = false;
	private int mCue = 0;
	private int rawSoundFile;
	private SeekBar mSeekBar = null;
	private Handler mHandler = new Handler();

	private Karaoke mKaraoke;
	private TextView mFirstLine, mSecondLine;

	public KaraokePlayer(Context context) {
		mContext = context;
		init();
	}

	private void init() {
		mKaraoke = new Karaoke();
	}

	@Override
	public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
		if(fromUser) {
			sb.setMax(player.getDuration());
			mCue = sb.getProgress();
			mKaraoke.seekLineDelayed(mCue);
			player.seekTo(mCue);
			player.start();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar p1) {
		// Not used
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekbar) {
		// Not used
	}

	private MediaPlayer getMusicPlayer() {
		if(player != null){
			player.reset();
			player.release();
			player = null;
		}
		player = player.create(mContext, rawSoundFile);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					
				}
			});
		if(mSeekBar != null) {
			mSeekBar.setMax(player.getDuration());
			mSeekBar.setProgress(player.getCurrentPosition());
			mSeekBar.setOnSeekBarChangeListener(this);
			updateProgressBar();
		}
		cueHandler.removeCallbacks(mCueTracker);
		cueHandler.postDelayed(mCueTracker,CUE_TRACK_DELAY);

		return player;
	}
	
	public void play() {
		try {
			if(player != null && player.isPlaying() && !pass ) {
				pause();
				return;
			}
			if(player != null && isPaused && !pass) {
				player.seekTo(mCue);
				player.start();
				isPaused = false;
				cueHandler.postDelayed(mCueTracker, CUE_TRACK_DELAY);
				return;
			}
			player = getMusicPlayer();
			player.start();
			isPaused = false;
			pass = false;

		} catch (Exception e){
			Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
		}

	} 
	
	private void pause() {
		if(player != null && player.isPlaying()) {
			player.pause();
			isPaused = true;
			mCue = player.getCurrentPosition();
			cueHandler.removeCallbacks(mCueTracker);
			mKaraoke.pause();
		}
	}

	private void updateProgressBar() {
        mHandler.postDelayed(new SeekBarUpdater(), 100);        
    }

	public void setSeekBar(SeekBar seekBar){
		mSeekBar = seekBar;
	}
	
	public boolean isPlaying() {
		return player == null ? false : player.isPlaying();
	} 
	
	private void stop() {
		
		if(player != null) {
			player.release();
			player = null;
		}
		mKaraoke.stop();
		mKaraoke = new Karaoke();
	}
	
	
	public void resume() {
		cueHandler.removeCallbacks(mCueTracker);
		cueHandler.postDelayed(mCueTracker,CUE_TRACK_DELAY);
		updateProgressBar();
	}

	public void shutDown() {
		cueHandler.removeCallbacks(mCueTracker);
		stop();
	}
	
	public void loadKaraokeFiles(String lyricsFileFromAssets, int audioDataFileIndex, int soundFileIdFromRaw) {
		stop();
		rawSoundFile = soundFileIdFromRaw;
		mKaraoke.feedLyrics(getLyrics(lyricsFileFromAssets), audioDataFileIndex);
	}
	
	public void locateKaraokeTextViews(TextView firstLine, TextView secondLine) {
		mFirstLine = firstLine; mSecondLine = secondLine;
	}
	
	private ArrayList<String> getLyrics(String filename) {
		ArrayList<String> lyrics  = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getAssets().open(filename)));
			String line;
			while ((line = br.readLine()) != null) {
				lyrics.add(line.replace("$$","$ $"));
			}
			br.close();
		} catch (IOException e) {
			Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
		}

		return lyrics;
	}

	public String getCurrentPosition() {
		if(!isPlaying()) {
			play();
		}
		return player.getCurrentPosition() + "";
	}

	private class SeekBarUpdater implements Runnable {
		@Override
		public void run() {
			if(mSeekBar != null && player != null) {
				int progress = player.getCurrentPosition();
				mSeekBar.setProgress(progress);
				mHandler.postDelayed(this, 100);
			} else {
				mHandler.removeCallbacks(this);
			}
		}
	} // SeekBarUpdater
	
	private void sendCue() {
		mKaraoke.updateCue(player.getCurrentPosition());
	}
	
	private class CueTracker implements Runnable {
		@Override
		public void run() {
			sendCue();
			cueHandler.postDelayed(this,CUE_TRACK_DELAY);
		}
	}

	private Handler cueHandler = new Handler();
	private CueTracker mCueTracker = new CueTracker();
	private long CUE_TRACK_DELAY = 1;

	private class Karaoke {

		private int[][] mAudio;
		private int karaokeLineIndex = -1;
		private int lineStartA = -1;
		private int lineEndA = -1;
		private int lineStartB = -1;
		private int lineEndB = -1;

		private boolean isATurn = true;

		private KaraokeLine mKaraokeLineA;
		private KaraokeLine mKaraokeLineB;
		

		private ArrayList<String> mLyrics = new ArrayList<>();

		public Karaoke() {
			ready();
		}

		private void ready() {
			mKaraokeLineA = new KaraokeLine();
			mKaraokeLineB = new KaraokeLine();
		}
		
		private void feedLyrics(ArrayList<String> lyrics, int audioDataFileIndex) {
			
			karaokeLineIndex = -1;
			
			if(lyrics.isEmpty()) {
				return;
			}
			
			mAudio = AudioData.getInstance().loadAudioData(audioDataFileIndex);
			mLyrics.clear();
			for(String line : lyrics) {
				mLyrics.add(line);
			}
			
			prepareA(0);

		}

		private void prepareA(int lineIndex) {
			mKaraokeLineA = new KaraokeLine();
			String line = mLyrics.get(lineIndex);
			int[] audio = mAudio[lineIndex];
			mKaraokeLineA.loadData(line,audio);
			lineStartA = mKaraokeLineA.getLineStart();
			lineEndA = mKaraokeLineA.getLineEnd();
			mFirstLine.setText(mKaraokeLineA.getLyricsLine());
		}

		private void prepareB(int lineIndex) {
			mKaraokeLineB = new KaraokeLine();
			String line = mLyrics.get(lineIndex);
			int[] audio = mAudio[lineIndex];
			mKaraokeLineB.loadData(line,audio);
			lineStartB = mKaraokeLineB.getLineStart();
			lineEndB = mKaraokeLineB.getLineEnd();
			mSecondLine.setText(mKaraokeLineB.getLyricsLine());
		}

		private void prepareAorB() {
			isATurn = ! isATurn;
			if(karaokeLineIndex + 1 < mLyrics.size()) {
				if(isATurn) {
					//Toast.makeText(mContext, "Preparing A ...", Toast.LENGTH_LONG).show();
					prepareA(karaokeLineIndex+1);
				} else {
					//Toast.makeText(mContext, "Preparing B ... ", Toast.LENGTH_LONG).show();
					prepareB(karaokeLineIndex+1);
				}
			}
		}
		private void updateCue(int cue) {
			// Kcue = cue;
			if(isATurn) {
				if(lineStartA > cue && lineStartA < cue + 200) {
					KhandlerA.removeCallbacks(KrunnableA);
					startKaraokeA();
					prepareAorB();
				}
			} else {
				if(lineStartB > cue && lineStartB < cue + 200) {
					KhandlerB.removeCallbacks(KrunnableB);
					startKaraokeB();
					prepareAorB();
				} 
			}
		}

		private void startKaraokeA() {
			swapKaraokeColor();
			karaokeLineIndex++;
			mKaraokeLineA.buildKaraoke();
			loopKaraokeA();
		}
		private void startKaraokeB() {
			swapKaraokeColor();
			karaokeLineIndex++;
			mKaraokeLineB.buildKaraoke();
			loopKaraokeB();
			
		}
		private void loopKaraokeA() {
			
			if(mKaraokeLineA.hasNextRun()) {
				KaraokeData KD = mKaraokeLineA.getKaraoke();
				
				// Karaoke output (first line)
				mFirstLine.setText(KD.karaoke, TextView.BufferType.SPANNABLE);
				mFirstLine.append(KD.nonKaraoke);
			
				long speed = mKaraokeLineA.getSpeed();
				KhandlerA.postDelayed(KrunnableA,speed);
			} else if(mKaraokeLineA.hasMoreChunk()){
				mKaraokeLineA.buildKaraoke();
				loopKaraokeA();
			} 
		}
		private void loopKaraokeB() {
			
			if(mKaraokeLineB.hasNextRun()) {
				KaraokeData KD = mKaraokeLineB.getKaraoke();
				
				// Karaoke output (second line)
				mSecondLine.setText(KD.karaoke, TextView.BufferType.SPANNABLE);
				mSecondLine.append(KD.nonKaraoke);
			
				long speed = mKaraokeLineB.getSpeed();
				KhandlerB.postDelayed(KrunnableB,speed);
			} else if(mKaraokeLineB.hasMoreChunk()){
				mKaraokeLineB.buildKaraoke();
				loopKaraokeB();
			}
		}
		
		private void swapKaraokeColor() {
			int[] colors = { Color.argb(255,0,191,255), Color.argb(255,255,105,180), Color.argb(255,238,203,173),Color.argb(255,127,255,212), Color.GREEN, Color.TRANSPARENT };
			KaraokeData.KaraokeColor = colors[new Random().nextInt(colors.length)];
			int[] backgroundColors = { Color.argb(200,0,0,0), Color.TRANSPARENT , Color.TRANSPARENT ,Color.TRANSPARENT , Color.TRANSPARENT , Color.TRANSPARENT };
			KaraokeData.KaraokeBackgroundColor = backgroundColors[new Random().nextInt(backgroundColors.length)];
		}
		private void stop() {
			KhandlerA.removeCallbacks(KrunnableA);
			KhandlerB.removeCallbacks(KrunnableB);
			lineStartA = -1; lineEndA = -1;
			lineStartB = -1; lineEndB = -1;
			mFirstLine.setText(" ");
			mSecondLine.setText(" ");
			isATurn = true;
		}
		
		private void pause() {
			KhandlerA.removeCallbacks(KrunnableA);
			KhandlerB.removeCallbacks(KrunnableB);
		}
		
		private void seekLineDelayed(int cue) {
			seekedCue = cue;
			LineSeekHandler.removeCallbacks(LineSeekRunnable);
			LineSeekHandler.postDelayed(LineSeekRunnable, 500);
		}
		
		private void seekLineNow(int cue) {
			int index = 0;
			for(index = mAudio.length-1; index > 0; index--) {
				if(cue >= mAudio[index][0] && index+1 < mLyrics.size()) {
					stop();
					karaokeLineIndex = index;
					prepareA(index+1);
					break;
				}
			}
		}
		
		private Handler KhandlerA = new Handler();
		private Handler KhandlerB = new Handler();
		private Handler LineSeekHandler = new Handler();
		
		private KRunnableA KrunnableA = new KRunnableA();
		private KRunnableB KrunnableB = new KRunnableB();
		private LineSeekRunnable LineSeekRunnable = new LineSeekRunnable();
		
		private class KRunnableA implements Runnable {
			@Override
			public void run() {
				loopKaraokeA();
			}
		}
		private class KRunnableB implements Runnable {
			@Override
			public void run() {
				loopKaraokeB();
			}
		}
		
		private int seekedCue = -1;
		
		private class LineSeekRunnable implements Runnable {
			@Override
			public void run() {
				seekLineNow(seekedCue);
			}
		}
	} // Karaoke
}//Main
