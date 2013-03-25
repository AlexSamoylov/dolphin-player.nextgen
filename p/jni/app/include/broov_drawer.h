#ifndef __BROOV_DRAWER_H__
#define __BROOV_DRAWER_H__
#include "JniDrawer.h"

uint8_t* getNextDecodedFrame(int *size);

// Set Screen width and height
void set_screen_size(int width, int height);

//Set object for drawer
void set_drawer(JNIMediaPlayerDrawer  *drawer);

//Get drawer
 JNIMediaPlayerDrawer * get_drawer();
// render video 
void renderVideo(VideoPicture *is);

void refreshVideo();

int nativeRenderVideo(uint8_t *pixels,int width,int height,int bytes);

#endif
