# TheMovieDB
Sample demonstrartion of using the moviedb api to get list of the most recent movies 
# Repo contains
- App source code
- Sample application
- Screenshots
# Sample contains
- Main screen which shows recycler view that display list of movies
- Filter menu item to create a filteration criteria for the loaded movies
- Hard refresh button to reset the db and also recover from the offline state (turn on internet while app is showing empty state)
- Details screen which displays the Large image , movie description 
# Features
- Remote movies featching with lazy loading 
- Filteration for the fetching process with minmium date abd maxmium date 
- Cache for max 60 records to demonstrate local data respitory 
- Caching for images
- Pretty ui with shared element animation
# Modules built with
- MVP Design pattern
- Dagger dependency injection
- Reactive programming RxJava , RxAndroid
- Retrofit networking 
- Butterknife views injection
- Glide for images loading and caching 
- Mockito , Junit , PowerMock for unit testing 
- SQLITE for local db
# Screenshots 
![Alt text](https://github.com/H-Sayed/TheMovieDB/blob/master/screenshots/Screenshot_20180214-231513.png?raw=true "Home Screen")
![Alt text](https://github.com/H-Sayed/TheMovieDB/blob/master/screenshots/Screenshot_20180214-231521.png?raw=true "Home Screen with filter")
![Alt text](https://github.com/H-Sayed/TheMovieDB/blob/master/screenshots/Screenshot_20180214-231529.png?raw=true "Details screen")
