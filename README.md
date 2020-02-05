# Climate Application

Project to load weather based on current location and rectangular zone formed by multiple cities using open weather map api.

## Project Structure

<b>ui package - </b> consists of activity, adapter and viewmodel

<b>domain.model package - </b> contains model class for application UI only

<b>framework package - </b> contains service calls and repository to call api

<b>di package - </b> to make dependency injection in the application


## Application Flow

On App Launch -> Checking for location permission and getting last known location. Then forecast api with respect to 
the location is called and UI is updated with date wise grouping of climates. 
On Menu action (Multiple places) -> Select 3-7 places from google places auto complete API and create rectangular box based on 
the latitude and longitude of all the selected places. Load the data in UI specific to cities.

## Screenshots

<a href="https://github.com/PrateekSharma1712/ClimateApplication/tree/master/screenshots">Screenshots link</a>

<table style={border:"none"}><tr><td><img src="https://github.com/PrateekSharma1712/ClimateApplication/blob/master/screenshots/no_internet.png" alt="If No Internet"/></td><td><img src="https://github.com/PrateekSharma1712/ClimateApplication/blob/master/screenshots/weather_forecast.png" alt="Landing page with forecast data"/></td><td><img src="https://github.com/PrateekSharma1712/ClimateApplication/blob/master/screenshots/add_place.png" alt="Add place"/></td></tr><tr><td><img src="https://github.com/PrateekSharma1712/ClimateApplication/blob/master/screenshots/added_places.png" alt="Added places"/></td><td><img src="https://github.com/PrateekSharma1712/ClimateApplication/blob/master/screenshots/multiple_city_weather.png" alt="Multiple Cities Weather"/></td></tr></table>
