# VirtualSoftKeys

## Overview

A simple , safe  and easy softKeys ( navigation bar).
This project just was used for my device to solve hardware button problem.
If you have any idea for any thing, please let me know!


## Features
*  As system navigation bar
*  No any extra permission
*  Support stylus


##Todo
1. More setting at  navigation bar and touch view
2. More color and icon for choice
3. Add teaching page


## Known issues
In some devices (e.g. ASUS Z500M)  , accessibilty  has some problem , I can't solve it .Just waiting devices's compamy to fix it . 

The bug is:
1. if you open accessibilty on setting page  when App had be onDestory, onServiceConnected can't  be called!
2. After devices suspend and wake up , sometimes the touch view can't be touched in some postion!
3. Accessibilty service was turn off but it still show turn on in accessibilty setting page after system reboot or apk update!

You can start accessibilty service for solving all bug ! 


## Screenshot
![](/screenshot/1.png) 
![](/screenshot/2.png) 
![](/screenshot/3.png) 


##APK
[![](/screenshot/google-play-badge.png) ](https://play.google.com/store/apps/details?id=tw.com.daxia.virtualsoftkeys)


## License

Copyright 2016 Daxia

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
