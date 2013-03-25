#ifndef __JNIDRAWER_H__
#define __JNIDRAWER_H__
#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <GLES2/gl2.h>
#define GL_GLEXT_PROTOTYPES 
#include <GLES2/gl2ext.h>
#include <dlfcn.h>
enum 
{
	ATTRIBUTE_VERTEX,
	ATTRIBUTE_TEXCOORD,
};
//-----------------------------------------------------------------------------
// Drawer interface
//-----------------------------------------------------------------------------
class JNIMediaPlayerDrawer
{
private:
    int viewPortWidth;
    int viewPortHeight;
    float zoomCoeff;

    JNIMediaPlayerDrawer();
    jclass  mClass;     // Reference to MMediaPlayer class
    jobject mObject;    // Weak ref to MMediaPlayer Java object to call on

    int textureSizes[6];
    int bckgTextureWidth;
    int bckgTextureHeight;
    /**** new variables ***/
    GLuint  _yuvtexture[3];
    GLint   _uniformSamplers[3];
    GLuint  _program;
    GLint   _uniformMatrix;
    GLfloat _vertices[8];
    GLuint  _framebuffer;
    GLuint  _renderbuffer;
public:
    JNIMediaPlayerDrawer(JNIEnv* env, jobject thiz, jobject weakThiz);
    ~JNIMediaPlayerDrawer();
    void setViewportSize(int _width, int _height);
    void setZoomCoeff(float zoom);
    void stop(){};
    void draw(int _width, int _height);
    void resolveUniformSamplers(GLuint program);
    bool loadShaders();
    GLuint compileShader(GLenum type,const char *shaderString);
    bool validateProgram(GLuint prog);
    bool prepareRender();
    void updateVertices(int width,int height);
    void mat4f_LoadOrtho(float left, float right, float bottom, float top, float near, float far, float* mout);
    void ConvertYUV2RGB(int _width,int _height, uint8_t **data);
};
#endif
