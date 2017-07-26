![logo](https://github.com/Tee7even/Presents/blob/master/.github/presents.png?raw=true)
Presents is a Nukkit plugin to place chests, that players can open and get random items.
**Please notice, that this plugin is no more maintained by me and therefore won't work with the newer versions of Nukkit, since its API is changed very often.**
*But*, you are free to make your own fork and update it to the latest version, anyone can do that. If you'll succeed with that, you can contact me at [nukkit.ru](nukkit.ru/members/tee7even.7/) or [nukkit.io](forums.nukkit.io/members/tee7even.17/), so I can pass [the resource at official forums](forums.nukkit.io/resources/presents.49/). Also you can contact me there, if you can and are willing to maintain this repository.
Things to think about and improve:
* The `ChestManager` is just a bunch of static functions and variables and so is the `TextTagManager` and the `Message`.
* The functions of the `ChestManager` are basically useless outside of the plugin. Not a good sign.
* `StepTasks` can be shrunk to one.
* I18n: the `Message` class can be improved and the standard translations can be provided.
* Chests can be saved in a database or something else. Luckily there is the `DataProvider` interface, but it's not ideal (looking at that `save` method). `YamlProvider` can be expanded to save to more formats, since it's based on `Config` which supports multiple formats.