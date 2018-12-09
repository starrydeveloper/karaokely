package demo.aide.karaoke;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.pm.*;

public class KaraokeActivity extends Activity {
	
	private TextView mFirstLine, mSecondLine, mCueLine;
	private KaraokePlayer mKaraokePlayer;
	private StringBuilder cueStringBuilder;
	private SeekBar mSeekBar;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);
	
		View pane = findViewById(R.id.pane);
		pane.setBackgroundColor(Color.argb(30,0,0,255));
		
		mCueLine = (TextView) findViewById(R.id.cueTv);
		mFirstLine = (TextView) findViewById(R.id.firstLine);
		mSecondLine = (TextView) findViewById(R.id.secondLine);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		
		cueStringBuilder = new StringBuilder();
		cueStringBuilder.append("cue : ");
		
		mKaraokePlayer = new KaraokePlayer(this);
		// Auto link our SeekBar to our Player : No more coding required for Seekbar to work;
		mKaraokePlayer.setSeekBar(mSeekBar);
		// Link our 2 TextViews for 2 Karaoke-like lines
		mKaraokePlayer.locateKaraokeTextViews(mFirstLine, mSecondLine);
		setupMyKaraoke();
		// Auto click "Play" button
		startPlaying(findViewById(R.id.playButton));
		
    }
	
	private void setupMyKaraoke() {
		
		if(isBeatles) {
			mKaraokePlayer.loadKaraokeFiles("beatles", // lyrics file name from assets folder
											2, // audio data file index [in AudioData.java]
											R.raw.beatles // id of sound file from res/raw folder
											);
		} else {
			mKaraokePlayer.loadKaraokeFiles("mltr", // lyrics file name from assets folder
											1, // audio data file index [in AudioData.java]
											R.raw.mltr // id of sound file from res/raw folder
											);
		}
		
	}
	
	private boolean isBeatles = false;
	
	public void selectSinger(View v) {
		isBeatles = ! isBeatles;
		((Button)v).setText(isBeatles ? "MLTR":"BEATLES");
		//Re-setup or re-prepare
		setupMyKaraoke();
		// auto click "Play" button
		startPlaying(findViewById(R.id.playButton));
	}
	
	public void startPlaying(View v) {
		mKaraokePlayer.play();
		((Button)v).setText(mKaraokePlayer.isPlaying() ? "Pause" : "Play");
		((Button)findViewById(R.id.toggle)).setText(isBeatles ? "MLTR" : "BEATLES");
	}
	
	public void showCue(View v) {
		String cue = mKaraokePlayer.getCurrentPosition();
		cueStringBuilder.append(" [ " + cue + " ] ");
		mCueLine.setText(cueStringBuilder.toString());
	}

	@Override
	public void onBackPressed() {
		if(mKaraokePlayer != null) {
			mKaraokePlayer.shutDown();
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		if(mKaraokePlayer != null) {
			mKaraokePlayer.shutDown();
		}
		super.onDestroy();
	}
	
}
