Android Webmoney Mobile Payment SDK V1
===

First Release 02.12.2006

---

Compatibility Notice
---

- Supports API level 11+ only.


Installation
---

1. Include library
---

- Maven dependency:

```
<dependency>
  <groupId>ru.webmoney.android</groupId>
  <artifactId>mobilepaymentsdk</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

or

- Gradle dependency:

   On development stage


2. Android Manifest
---

<manifest>
    <!-- Include following permission if you load images from Internet -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- Include following permission if you want to cache images on SD card -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    ...
</manifest>



License
---
Android Webmoney Mobile Payment SDK is licensed under Apache V2 license, so feel free to use it in any open-source or commercial project.


Contact
---
Please post all bugs, feature requests, etc into the issues section. As usual, pull requests are welcome.




