package demo.aide.karaoke;

import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.util.*;

public class KaraokeLine {

	private String mLine;
	private int [] mAudio;
	private String[] mStringChunks;
	private long[] mSpeeds;
	private int totalChunks = 0;

	private int chunkIndex = -1;
	private int runCounts = -1;
	private long speed = 0;


	public KaraokeLine() {

	}

	public void loadData(String line, int[] audio) {
		mLine = line;
		mAudio = audio;
		reset();
		init();
	}
	private void reset() {
		charIndex = 0;
		runCounts = -1;
		chunkIndex = -1;
	}
	private void init() {
		StringBuilder sb = new StringBuilder();
		sb.append(mLine.substring(1 + (mLine.split("\\$")[0]).length() , mLine.lastIndexOf("$")));
		mLine = sb.toString().replace("$","");
		mStringChunks = sb.toString().split("\\$");
		totalChunks = mStringChunks.length-1;
		mSpeeds = new long[1 + totalChunks];
		for(int i = 0; i < mSpeeds.length; i++) {
			mSpeeds[i] = mAudio[i+1] - mAudio[i];
		}
	}

	private void processNextChunk() {
		chunkIndex++;
		chunk = mStringChunks[chunkIndex];
		chunkLength = chunk.length();
		speed = mSpeeds[chunkIndex] / chunk.length()-1;
	}

	public String getLyricsLine() {
		return mLine;
	}

	public int getLineStart() {
		return mAudio[0];
	}

	public int getLineEnd() {
		return mAudio[mAudio.length-1];
	}

	public boolean hasMoreChunk() {
		return chunkIndex < totalChunks;
	}

	public long getNextSpeed() {
		return speed;
	}

	public boolean hasNextRun() {
		return  runCounts++ < chunk.length();
	}

	private int charIndex = 0;
	private StringBuilder sb = new StringBuilder();
	private int chunkLength;
	private String chunk;

	public KaraokeData getKaraoke() {

		KaraokeData KD = new KaraokeData();
		if(charIndex <= chunkLength) {
			sb.append(chunk.charAt(charIndex));
			SpannableString karaoke = new SpannableString(sb.toString());
			karaoke.setSpan(new BackgroundColorSpan(KaraokeData.KaraokeBackgroundColor) , 0, sb.toString().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			karaoke.setSpan(new ForegroundColorSpan(KaraokeData.KaraokeColor) , 0, sb.toString().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			KD.karaoke = karaoke;
			KD.nonKaraoke = (mLine.substring(sb.toString().length(), mLine.length()));
			charIndex++;
		}

		return KD;
	}

	public void buildKaraoke() {
		charIndex = 0;
		runCounts = 0;
		processNextChunk();
	}
}
