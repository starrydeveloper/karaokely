# Karaokely
A demo android app for karaoke-like lyrics displaying by using `MediaPlayer` object, `SpannableString` , `Handler`  and `Runnable` .

### How To
Wonder how to use? Well,
A lyrics text file in `assets` folder,


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

```java



