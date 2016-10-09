![Alt text](/app/src/main/res/drawable-xhdpi/ic_launcher.png?raw=true "Icon")
# Browsary
_Very simple Web browser with (kind of) Thesaurus feature_

It is a web browser which allows users to select a word when browsing website and automatically get some information about it, such as synonyms, antonyms, etc.
It gets that information from the [Big Huge Thesaurus API](http://words.bighugelabs.com/api.php).
Free (and fair) use of this API is limited to 1000 words per day.

App compatible with Android 4.0.3 or more (Ice Cream Sandwich - API 15)

## Features
### Thesaurus
Each time you select an English word on a website, you will instantly get some information about it, which is particularly convenient for non-native English reader who wants to improve their reading skill in this language

![Alt text](/screenshot/thesaurus_vertical.png?raw=true "Thesaurus")

If your Android version device is 4.3 or below (Jelly Bean - API 18), you will have to click on the `copy` icon after selection to get the information

![Alt text](/screenshot/thesaurus_eg_before.png?raw=true "Thesaurus")
![Alt text](/screenshot/thesaurus_eg_after.png?raw=true "Thesaurus")

_Browsary also include some basic features_
### Bookmarks
- Retrieve your favorite website from the right menu by click on the menu button (right in the navigation bar) or by swiping right to left from the right border
- Just add some favorite website by click on the star (in the navigation bar)

![Alt text](/screenshot/bookmark_menu.png?raw=true "Bookmark (menu)")
![Alt text](/screenshot/bookmark_star.png?raw=true "Bookmark (star icon)")

### History
- By the same way as the bookmark (from the right) you can get your history, just change tab side (bottom)
- You can also quickly access some specific website you already browsed by typing/searching

![Alt text](/screenshot/history_menu.png?raw=true "History (menu)")
![Alt text](/screenshot/history_search.png?raw=true "History (search)")

### Tab
- You can navigate through different websites with many tabs
- Simple button allows you to easily add and delete tab

![Alt text](/screenshot/tab.png?raw=true "Tab (menu from left side)")
![Alt text](/screenshot/tab_button.png?raw=true "Tab (button add/delete)")

### Navigation bar
- Include autocompletion from history

![Alt text](/screenshot/navbar_autocompletion.png?raw=true "Autocompletion")

- Construct URL, no need to write `http://` or even `www`

![Alt text](/screenshot/url_construct_before.png?raw=true "Construct URL (before)")
![Alt text](/screenshot/url_construct_after.png?raw=true "Construct URL (after)")

- Direct search from Google

![Alt text](/screenshot/navbar_search.png?raw=true "Search (before)")
![Alt text](/screenshot/navbar_search_google.png?raw=true "Search (result)")

### Misc
- Implicit intent makes Browasry launchable from other apps

![Alt text](/screenshot/implicit_intent.png?raw=true "Implicit intent")

- Handle different orientation (horizontal/vertical) and size of screens (here screenshot from Nexus 7 in landscape)

![Alt text](/screenshot/n7_landscape.png?raw=true "Nexus 7 (landscape)")

## Known issues
- Code is maybe a bit ugly

(Code refactoring must be done - it was my second android app)
