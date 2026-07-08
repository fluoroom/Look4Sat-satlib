# Look4Sat: Satellite tracker

[![Look4Sat CI](https://github.com/rt-bishop/Look4Sat/actions/workflows/release.yml/badge.svg)](https://github.com/rt-bishop/Look4Sat/actions/workflows/release.yml)

### !!! This fork must be downloaded from [releases page](https://github.com/fluoroom/Look4Sat-satlib/releases), NOT app stores, until (maybe) the main Look4Sat project adopts these changes.

[<img src="https://play.google.com/intl/en_gb/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="80">](https://play.google.com/store/apps/details?id=com.rtbishop.look4sat)
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/com.rtbishop.look4sat/)

### Radio satellite tracker and pass predictor for Android, inspired by Gpredict

<p float="left">
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" width="192"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width="192"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" width="192"/>
<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" width="192">
</p>

### Track satellite passes with ease!

Thanks to [Celestrak](https://celestrak.com/) and [SatNOGS](https://satnogs.org/) you have access to over 9000 active satellites.\
You can search the entire database by NORAD Catalog Number or the satellite's name.

Orbital positions and passes are calculated relative to your location.\
To get reliable data make sure to set the station position via the app Settings.

The application is built using Kotlin, Coroutines, Jetpack Compose and Navigation.\
It is now and always will be completely ad-free and open-source.

## Main features:

*  Predicting satellite positions and passes for up to 10 days
*  Showing the list of currently active and upcoming satellite passes
*  Showing the active pass progress, polar trajectory and transceivers info
*  Showing the satellite positional data, footprint and ground track on the map
*  Custom TLE satellite data import is available via Three Line Element .txt files
*  Offline first: calculations are made offline. Weekly TLE data update is recommended.

## This fork

This fork adds **satlib** — a universal local-network API that exposes complete, real-time satellite pass data so any external application can consume it over HTTP, without device-specific integrations inside the tracker.

### [satlib](https://github.com/fluoroom/satlib)

While a pass is active on the radar screen, any device on the same network can poll:

```
GET http://<phone-ip>:4534/
```

and receive a live JSON snapshot updated every second:

```json
{
  "satName": "ISS (ZARYA)",
  "azimuthDeg": 247.35,
  "elevationDeg": 12.84,
  "altitudeKm": 421.10,
  "txFrequencyHz": 145827340,
  "rxFrequencyHz": 145826100,
  "ctcssTxToneHz": 67.0,
  "ctcsRxToneHz": null,
  "mode": "FM",
  "aosTime": 1752012345000,
  "losTime": 1752012945000,
  ...
}
```

Full field reference, idle state semantics, and versioning rules are documented in [SATLIB.md](SATLIB.md).

---

## Star History

<a href="https://www.star-history.com/?repos=rt-bishop%2FLook4Sat&type=timeline&legend=top-left">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/chart?repos=rt-bishop/Look4Sat&type=timeline&theme=dark&legend=top-left" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/chart?repos=rt-bishop/Look4Sat&type=timeline&legend=top-left" />
   <img alt="Star History Chart" src="https://api.star-history.com/chart?repos=rt-bishop/Look4Sat&type=timeline&legend=top-left" />
 </picture>
</a>
