# Karaokely
A demo android app for karaoke-like lyrics displaying by using `MediaPlayer` object, `SpannableString` , `Handler`  and `Runnable` . 
And maybe this project gives or at least, tries to give solutions to [stackoverflow](https://www.stackoverflow.com) questions like [this](https://stackoverflow.com/questions/42628761/im-creating-a-karaoke-like-app-in-android-studio#comment72385499_42628761), and [this](https://stackoverflow.com/questions/6050750/android-karaoke-text), and [this](https://stackoverflow.com/questions/12025055/how-should-i-time-highlighting-of-text-with-audio-for-a-karaoke-like-application).

### How To
Wonder how to use? Well, a lyrics text file in `assets` folder,
```java
$When I find myself in times of trouble$
$Mother Mary$ $comes to me$
$Speaking words of wisdom$
$Let it be$
$And in my hour of$ $darkness$
$She is standing$ $right in front of me$
$Speaking$ $words of wisdom$
$Let it be$
$Let it be,$ $let it be,$ $let it be,$ $let it$ $be,$
$Whisper words of wisdom$
...
...
```

is mapped with the `int` values (let's call them "cues") from `getCurrentPosition()` method of `MediaPlayer` object and these cues are pre-hardcoded in `AudioData.java` class as follows;


```java
private int[][] letItBe() {
		return new int[][] {
			
			{ 13533, 16268 },
			{ 17000, 17500, 18496, 19220 },
			{ 20181, 22066 },
			{ 22757, 23000 },
			{ 26671, 27437, 27837, 28200 },
			// 5
			{ 29106, 29627, 30427, 31817 },
			{ 32789, 33086, 33686, 34640 },
			{ 35489, 36021 },
			{ 38709, 39309, 	40017, 40617, 	41605, 42205,		43221, 43823, 44023, 44323 },
			{ 46261, 47888 },
			// 10
                      ...
                      ...

```

where a `"$"` symbol in lyrics stands for a `"cue"` in `AudioData.java`. That's it and you hardly have to touch any other karaoke-related classes in the project.

### A Word for Wises
Believe it or not, the smoothness of the running karaoke-like text/lyrics is determined by how well you time-spilt your chunks of lyrics or texts, **Not** by the codes implemented. Not buying? OK, you're welcome to tweak some "cue" values in `AudioData.java` and see them in action in the demo app yourself.

### To-Do
- To clean up messed codes.
- To add methods in `KaraokePlayer` so it can map and play sound files and lyrics text files from other sources other than `res/raw` folder and `assets` folder.

### LICENSE
"The best things in the world are always FREE," as they say, these codes are FREE for any uses and will always be. But if you love this idea and implementation, and more importantly find this useful in your projects, you can show your love by donating.
