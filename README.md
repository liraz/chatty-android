![Chatty logo](https://github.com/liraz/chatty-android/blob/master/app/src/main/res/drawable/logo_main.png?raw=true)

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

#
Android chat application using Firebase, EventBus, Realm, OneSignal, Chatkit, Butterknife & Dagger

### Features

1. Login using Email, Phone number or Facebook
2. Signup using Email, Phone number or Facebook
3. Email verification process using Firebase SDK
4. Phone number verification process using Firebase SDK
5. Phone number zone detection and format
6. Recent chats view with principal menu - updated in realtime
7. Chat page
   1. Image, emoji icons and current location can be sent to the chat
   2. Chat bubbles with user image and automatic letter image & timestamp
   3. Multiple poeple chat
   4. A group chat - can be edited and add additional members from edit screen
   5. Chat is updated in realtime and sending notifications to all participants using OneSignal SDK
   6. Images can be viewed
   7. Typing indicator shows while someone in the group is currently typing
8. Create new group
9. Intiate a private chat with a contact in your phone
10. Mechanism that searches for facebook friends or contacts in your phone that are available in app Firebase DB
   1. Matched contacts are shown in the app when trying to initiate a group or a chat, like Telegram does.
11. Architecture is IOC for all components of the app
12. Application data is available offline even if there's no connection to Firebase using RealmDB
   1. Contacts, messages & recents chats are persisted in user's device
13. Messages are encrypted and decrypted on the other side

### Using the following libraries

* https://github.com/mikepenz/MaterialDrawer - side menu in the app
* https://github.com/mikepenz/Android-Iconics - automatic high quality icons library
* https://github.com/akashandroid90/ImageLetterIcon - generating letter images for users without a profile picture
* https://github.com/Clans/FloatingActionButton - for floating action buttons
* https://github.com/traex/RippleEffect - for ripple effects on components and views
* https://github.com/vanniktech/Emoji - for showing emoji icons and emoji keyboard
* https://github.com/stfalcon-studio/ChatKit - for showing chat bubbles
* https://github.com/realm/realm-android-adapters - for Realm integration in Android
* https://github.com/pchmn/MaterialChipsInput - for users selections using a chips input - used in the new group creation process
* https://github.com/heetch/Android-country-picker - a picker for countries when selecting phone area codes
* https://github.com/afollestad/material-dialogs - for showing material dialogs
* https://github.com/81813780/AVLoadingIndicatorView - loaders library - used for the typing indicator
* https://github.com/googlei18n/libphonenumber - used for phone number validation and format
* https://github.com/ParkSangGwon/TedPermission - for better async permissions support on Android 6+ devices
* https://github.com/google/dagger - Dagger framework
* https://github.com/JakeWharton/butterknife - Butterknife framework
* https://github.com/greenrobot/EventBus - for dispatching and listening to events between services & views
* https://github.com/scottyab/AESCrypt-Android - for messages encryption
* https://github.com/bumptech/glide - for automatic & easy aync images
* https://github.com/OneSignal/OneSignal-Android-SDK - OneSignal SDK for android

### Some screenshots from the app

<p align="center">
  <img src="app/src/main/assets/screen1.png?raw=true" title="Menu" width="250"/>
  <img src="app/src/main/assets/screen2.png?raw=true" title="Initial not logged in screen" width="250"/>
  <img src="app/src/main/assets/screen3.png?raw=true" title="Sign up with phone" width="250"/>
</p>
<p align="center">
  <img src="app/src/main/assets/screen4.png?raw=true" title="Sign up with email" width="250"/>
  <img src="app/src/main/assets/screen5.png?raw=true" title="Add profile photo" width="250"/>
  <img src="app/src/main/assets/screen6.png?raw=true" title="Verification" width="250"/>
</p>
