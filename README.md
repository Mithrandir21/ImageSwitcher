# ImageSwitcher
An Android library allowing for easy, configuration-less, touch view switching.


### Usage


The functionality can be added to both Views and ViewGroups, but the touch makes most sense on a <b>Transparent ViewGroup</b>.


##### Example
###### Activity / Fragment - Setup
```
ViewGroup touchViewGroup = [Get the Transparent ViewGroup]

DraweeView imageA = [Get ImageA];
DraweeView imageB = [Get ImageB];
DraweeView imageC = [Get ImageC];

ArrayList<Uri> imageUri = [Get Image URIs]

// 1. Create the (convenience class) containing the switching functions.
ImageSwitcher imageSwitcher = new ImageSwitcher(imageUri, imageA, imageB, imageC);

// 2. Create Touch listener.
SwitchListener switchListener = new SwitchListener(context, imageSwitcher.viewTracker, imageSwitcher);

// 3. Add the Touch listener .
touchViewGroup.setOnTouchListener(switchListener);
```

###### Layout
```
<FrameLayout
  android:id="@+id/transparent_touch_area"
  android:layout_width="match_parent"
  android:layout_height="match_parent">
  
  <com.facebook.drawee.view.SimpleDraweeView
        android:id="@+id/media_image_A"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        android:visibility="gone"/>

    <com.facebook.drawee.view.SimpleDraweeView
        android:id="@+id/media_image_B"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"/>

    <com.facebook.drawee.view.SimpleDraweeView
        android:id="@+id/media_image_C"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        android:visibility="gone"/>
  
</FrameLayout>
```


### Change Log

##### Version 0.2
- Made some internal SwitchListener functions public for easier access and functionality.
- Updated from Fresco 0.7 to 0.8.
