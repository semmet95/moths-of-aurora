## Moths-of-Aurora

Android app dedicated to Norwegian singer/song-writer Aurora Aksnes.

Never miss any of [Aurora's](https://aurora-music.com) activities on different social media platforms, viz, Facebook, Instagram, Twitter and Youtube.

The app uses `firebase cloud functions` to notify the user as soon as it finds any new activity on any of the above mentioned social platforms.
The user is also notified each time a new live show add to her official site.

All the data displayed in the app is stored in a [Firebase Realtime Database](https://moths-of-aurora.firebaseio.com/.json) that is updated regularly by the app's [backend](https://github.com/singh-95/moths-of-aurora_backend).

The app has another feature that let's the user create playlists of their favorite Aurora related videos on Youtube and share them with other users of the app. I used the [Paper](https://github.com/pilgr/Paper) library to create and share the playlists as files.

### Screenshots

<img width="200" alt="Facebook" src="screenshots/facebook.png?raw=true"> <img width="200" alt="Instagram" src="screenshots/instagram.png?raw=true"> <img width="200" alt="Twitter" src="screenshots/twitter.png?raw=true"> <img width="200" alt="Videos" src="screenshots/videos.png?raw=true"> <img width="200" alt="PLaylists" src="screenshots/playlists.png?raw=true"> <img width="200" alt="Playlist" src="screenshots/playlist.png?raw=true"> <img width="200" alt="NowPlaying" src="screenshots/nowplaying.png?raw=true"> <img width="200" alt="Tickets" src="screenshots/tickets.png?raw=true"> <img width="200" alt="Settings" src="screenshots/settings.png?raw=true">

### Release Notes

#### Version 1.2

Changes introduced:
- Complete UI overhaul
- Under the hood improvements