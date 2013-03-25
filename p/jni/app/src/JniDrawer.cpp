#include "JniDrawer.h"
#include <string>
#include <math.h>

#define TAG "JniDrawer.cpp"


static const GLfloat texCoords[] = { 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f };
static const GLfloat vertices[]= {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f };
extern JavaVM *jniVM;
static const char gVertexShader[] = 
"attribute vec4 position;\n"
"attribute vec2 texcoord;\n"
"uniform mat4 modelViewProjectionMatrix;\n"
"varying vec2 v_texcoord;\n"
" void main()\n"
"{\n"
"gl_Position = modelViewProjectionMatrix * position;\n"
"v_texcoord = texcoord.xy;\n"
"}\n";

static const char gFragmentShader[] = 
"varying highp vec2 v_texcoord;\n"
"uniform sampler2D s_texture_y;\n"
"uniform sampler2D s_texture_u;\n"
"uniform sampler2D s_texture_v;\n"
"void main() {\n"
	"highp float y = texture2D(s_texture_y, v_texcoord).r;\n"
	"highp float u = texture2D(s_texture_u, v_texcoord).r - 0.5;\n"
	"highp float v = texture2D(s_texture_v, v_texcoord).r - 0.5;\n"
	"highp float r = y + 1.402 * v;\n"
	"highp float g = y - 0.344 * u - 0.714 * v;\n"
	"highp float b = y + 1.772 * u;\n"
	"gl_FragColor = vec4(r,g,b,1.0);\n"
"}\n";


JNIMediaPlayerDrawer::JNIMediaPlayerDrawer(JNIEnv* env, jobject thiz, jobject weakThiz)
{
    // Hold onto the MMediaPlayer class for use in calling the static method
    // that posts events to the application thread.
    jclass clazz = env->GetObjectClass(thiz);

    if (clazz == NULL)
    {
        return;
    }

    mClass = (jclass)env->NewGlobalRef(clazz);

    // We use a weak reference so the MMediaPlayer object can be garbage collected.
    // The reference is only used as a proxy for callbacks.
    mObject  = env->NewGlobalRef(weakThiz);

    viewPortWidth = 0;
    viewPortHeight = 0;

    zoomCoeff = 1.0f;

    textureSizes[0] = 128;
    textureSizes[1] = 256;
    textureSizes[2] = 512;
    textureSizes[3] = 1024;
    textureSizes[4] = 2048;
    textureSizes[5] = 4096;

    bckgTextureWidth = 2048;
    bckgTextureHeight = 800;


    /*Initialize shaders for coverting yuv frame to rgb*/
#if 1
    loadShaders();
	
    _vertices[0] = -1.0f;  // x0
    _vertices[1] = -1.0f;  // y0
    _vertices[2] =  1.0f;  // ..
    _vertices[3] = -1.0f;
    _vertices[4] = -1.0f;
    _vertices[5] =  1.0f;
    _vertices[6] =  1.0f;  // x3
    _vertices[7] =  1.0f;  // y3
    glBindRenderbuffer(GL_RENDERBUFFER, _renderbuffer);
    //glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_WIDTH, &bckgTextureWidth);  
    //glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_HEIGHT, &bckgTextureHeight);
   //__android_log_print(ANDROID_LOG_INFO,TAG,"after texWidth %d,texHeight %d, width%d,height%d",bckgTextureWidth,bckgTextureHeight,_width,_height);
   GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
   if (status != GL_FRAMEBUFFER_COMPLETE) {
	__android_log_print(ANDROID_LOG_DEBUG,TAG,"failed to make complete framebuffer object %x", status);
   } else {
	__android_log_print(ANDROID_LOG_DEBUG,TAG,"OK setup GL framebuffer %d:%d", bckgTextureWidth, bckgTextureHeight);
   }
   glBindFramebuffer(GL_FRAMEBUFFER, _framebuffer);
   glUseProgram(_program);
#endif
}
//-----------------------------------------------------------------------------
JNIMediaPlayerDrawer::~JNIMediaPlayerDrawer()
{
    // remove global references
    JNIEnv *env;
    (jniVM)->AttachCurrentThread(&env, NULL);
    env->DeleteGlobalRef(mObject);
    env->DeleteGlobalRef(mClass);
    glDeleteProgram(_program);
    _program = 0;
    if (_yuvtexture[0])
    glDeleteTextures(3, _yuvtexture);
}

//-----------------------------------------------------------------------------

static void printGLString(const char *name, GLenum s) {
	    const char *v = (const char *) glGetString(s);
	        __android_log_print(ANDROID_LOG_INFO,TAG,"GL %s = %s\n", name, v);
}

//-----------------------------------------------------------------------------
void JNIMediaPlayerDrawer::setZoomCoeff(float zoom)
{
    zoomCoeff = zoom;
}

//-----------------------------------------------------------------------------
void JNIMediaPlayerDrawer::setViewportSize(int _width, int _height)
{
	printGLString("Version", GL_VERSION);
	printGLString("Vendor", GL_VENDOR);
	printGLString("Renderer", GL_RENDERER);
	printGLString("Extensions", GL_EXTENSIONS);

    viewPortWidth = _width;
    viewPortHeight = _height;
    
    glViewport(0, 0, _width, _height);
    for(int i=0;i<3;i++){
    	glDeleteTextures(1,&_yuvtexture[i]);
    }
    for(int i = 0; i < 6; i++)
    {
        if(viewPortWidth <= textureSizes[i])
        {
            bckgTextureWidth = textureSizes[i];
            break;
        }
    }

    for(int i = 0; i < 6; i++)
    {
        if(viewPortHeight <= textureSizes[i])
        {
            bckgTextureHeight = textureSizes[i];
            break;
        }
    }

    __android_log_print(ANDROID_LOG_INFO, TAG, "setViewportSize");
    __android_log_print(ANDROID_LOG_INFO, TAG, "bckgTextureWidth: %d", bckgTextureWidth);
    __android_log_print(ANDROID_LOG_INFO, TAG, "bckgTextureHeight: %d", bckgTextureHeight);
}
void JNIMediaPlayerDrawer::ConvertYUV2RGB(int _width,int _height, uint8_t **data)
{
	__android_log_print(ANDROID_LOG_DEBUG,TAG,"OK setup GL framebuffer %d:%d", bckgTextureWidth, bckgTextureHeight);
	//__android_log_print(ANDROID_LOG_INFO,TAG," b4 texWidth %d,texHeight %d, width%d,height%d",bckgTextureWidth,bckgTextureHeight,_width,_height);
#if 0
	glBindRenderbuffer(GL_RENDERBUFFER, _renderbuffer);
	//glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_WIDTH, &bckgTextureWidth);
	//glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_HEIGHT, &bckgTextureHeight);

	//__android_log_print(ANDROID_LOG_INFO,TAG,"after texWidth %d,texHeight %d, width%d,height%d",bckgTextureWidth,bckgTextureHeight,_width,_height);
	GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
	if (status != GL_FRAMEBUFFER_COMPLETE) {
		__android_log_print(ANDROID_LOG_DEBUG,TAG,"failed to make complete framebuffer object %x", status);
	} else {

		__android_log_print(ANDROID_LOG_DEBUG,TAG,"OK setup GL framebuffer %d:%d", &bckgTextureWidth, &bckgTextureHeight);
	}


	glBindFramebuffer(GL_FRAMEBUFFER, _framebuffer);
	glViewport(0, 0, viewPortWidth, viewPortHeight);
	glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	glClear(GL_COLOR_BUFFER_BIT);
	glUseProgram(_program);
#endif
	if (0 == _yuvtexture[0])
		glGenTextures(3, _yuvtexture);

	const int widths[3]  = { _width, _width / 2, _width / 2 };
	const int heights[3] = { _height, _height / 2, _height / 2 };

	for (int i = 0; i < 3; ++i) {

		glBindTexture(GL_TEXTURE_2D, _yuvtexture[i]);

		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,widths[i],heights[i],0,GL_LUMINANCE,GL_UNSIGNED_BYTE,data[i]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}     


}
//void JNIMediaPlayerDrawer::draw(int _width,int _height,uint8_t **data){

void JNIMediaPlayerDrawer::draw(int _width,int _height){
#if 1
		__android_log_print(ANDROID_LOG_DEBUG,TAG,"OK setup GL framebuffer %d:%d", bckgTextureWidth, bckgTextureHeight);
//__android_log_print(ANDROID_LOG_INFO,TAG," b4 texWidth %d,texHeight %d, width%d,height%d",bckgTextureWidth,bckgTextureHeight,_width,_height);
#if 0
	glBindRenderbuffer(GL_RENDERBUFFER, _renderbuffer);
	//glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_WIDTH, &bckgTextureWidth);
	//glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_HEIGHT, &bckgTextureHeight);

	//__android_log_print(ANDROID_LOG_INFO,TAG,"after texWidth %d,texHeight %d, width%d,height%d",bckgTextureWidth,bckgTextureHeight,_width,_height);
	GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
	if (status != GL_FRAMEBUFFER_COMPLETE) {
		__android_log_print(ANDROID_LOG_DEBUG,TAG,"failed to make complete framebuffer object %x", status);
	} else {

		__android_log_print(ANDROID_LOG_DEBUG,TAG,"OK setup GL framebuffer %d:%d", &bckgTextureWidth, &bckgTextureHeight);
	}


	glBindFramebuffer(GL_FRAMEBUFFER, _framebuffer);
	glViewport(0, 0, viewPortWidth, viewPortHeight);
	glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	glClear(GL_COLOR_BUFFER_BIT);
	glUseProgram(_program);
#endif
#if 0
	if (0 == _yuvtexture[0])
		glGenTextures(3, _yuvtexture);

	const int widths[3]  = { _width, _width / 2, _width / 2 };
	const int heights[3] = { _height, _height / 2, _height / 2 };

	for (int i = 0; i < 3; ++i) {

		glBindTexture(GL_TEXTURE_2D, _yuvtexture[i]);

		glTexImage2D(GL_TEXTURE_2D,0,GL_LUMINANCE,widths[i],heights[i],0,GL_LUMINANCE,GL_UNSIGNED_BYTE,data[i]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}     

__android_log_print(ANDROID_LOG_INFO,TAG,"b4 prepare Render");
	//updateVertices(_width,_height);
#endif
	if (prepareRender()) {
		__android_log_print(ANDROID_LOG_INFO,TAG," prepare Render returns true");
		GLfloat modelviewProj[16];
		mat4f_LoadOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, modelviewProj);
		glUniformMatrix4fv(_uniformMatrix, 1, GL_FALSE, modelviewProj);

		glVertexAttribPointer(ATTRIBUTE_VERTEX, 2, GL_FLOAT, 0, 0, vertices);
		glEnableVertexAttribArray(ATTRIBUTE_VERTEX);
		glVertexAttribPointer(ATTRIBUTE_TEXCOORD, 2, GL_FLOAT, 0, 0, texCoords);
		glEnableVertexAttribArray(ATTRIBUTE_TEXCOORD);

#if 0
		if (!validateProgram(_program))
		{
			NSLog(@"Failed to validate program");
			return;
		}
#endif

		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);        
	}

	glBindRenderbuffer(GL_RENDERBUFFER, _renderbuffer);
#if 0	
	for(int i=0; i<3; i++){
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0+i,GL_TEXTURE_2D, _yuvtexture[i], 0);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, viewPortWidth, viewPortHeight);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0+i,GL_RENDERBUFFER,GL_DEPTH_COMPONENT16);
	}
#endif
	__android_log_print(ANDROID_LOG_INFO,TAG,"view port width %d, height,%d",viewPortWidth,viewPortHeight);
        __android_log_print(ANDROID_LOG_INFO,TAG,"end of draw method");
#endif
}
#if 1
//-----------------------------------------------------------------------------------------
void JNIMediaPlayerDrawer::resolveUniformSamplers(GLuint program)
{
	_uniformSamplers[0] = glGetUniformLocation(program, "s_texture_y");
	_uniformSamplers[1] = glGetUniformLocation(program, "s_texture_u");
	_uniformSamplers[2] = glGetUniformLocation(program, "s_texture_v");
}
bool JNIMediaPlayerDrawer::loadShaders()
{
	bool result = false;
	GLuint vertShader = 0, fragShader = 0;
	_program = glCreateProgram();
	vertShader = compileShader(GL_VERTEX_SHADER, gVertexShader);
	if (!vertShader)
		goto exit;
	fragShader = compileShader(GL_FRAGMENT_SHADER, gFragmentShader);
	if (!fragShader)
		goto exit;
	glAttachShader(_program, vertShader);
	glAttachShader(_program, fragShader);
	glBindAttribLocation(_program, ATTRIBUTE_VERTEX, "position");
	glBindAttribLocation(_program, ATTRIBUTE_TEXCOORD, "texcoord");
	glLinkProgram(_program);
	GLint status;
	glGetProgramiv(_program, GL_LINK_STATUS, &status);
	if (status == GL_FALSE) {
		__android_log_print(ANDROID_LOG_DEBUG,TAG,"Failed to link program %d", _program);
		goto exit;
	}

	result = validateProgram(_program);

	_uniformMatrix = glGetUniformLocation(_program, "modelViewProjectionMatrix");
	resolveUniformSamplers(_program);

exit:

	if (vertShader)
		glDeleteShader(vertShader);
	if (fragShader)
		glDeleteShader(fragShader);

	if (result) {

		__android_log_print(ANDROID_LOG_DEBUG,TAG,"OK setup GL programm");

	} else {
		__android_log_print(ANDROID_LOG_ERROR,TAG,"Error in Compile Shader");
		glDeleteProgram(_program);
		_program = 0;
	}
	return result;

}

bool JNIMediaPlayerDrawer:: validateProgram(GLuint prog)
{
	GLint status;
	glValidateProgram(prog);

#ifdef DEBUG
	GLint logLength;
	glGetProgramiv(prog, GL_INFO_LOG_LENGTH, &logLength);
	if (logLength > 0)
	{
		GLchar *log = (GLchar *)malloc(logLength);
		glGetProgramInfoLog(prog, logLength, &logLength, log);
		__android_log_print(ANDROID_LOG_DEBUG,TAG,"Program validate log:\n%s", log);
		free(log);
	}
#endif

	glGetProgramiv(prog, GL_VALIDATE_STATUS, &status);
	if (status == GL_FALSE) {
		__android_log_print(ANDROID_LOG_DEBUG,TAG,"Failed to validate program %d", prog);
		return false;
	}

	return true;
}

GLuint JNIMediaPlayerDrawer::compileShader(GLenum type, const char *shaderString)
{
	GLint status;
	const GLchar *sources = (GLchar *)shaderString;
	__android_log_print(ANDROID_LOG_DEBUG,TAG,"shaderString = %s",sources);
	GLuint shader = glCreateShader(type);
	if (shader == 0 || shader == GL_INVALID_ENUM) {
		__android_log_print(ANDROID_LOG_DEBUG,TAG,"Failed to create shader %d", type);
		return 0;
	}

	glShaderSource(shader, 1, &sources, NULL);
	glCompileShader(shader);

#ifdef DEBUG
	GLint logLength;
	glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &logLength);
	if (logLength > 0)
	{
		GLchar *log = (GLchar *)malloc(logLength);
		glGetShaderInfoLog(shader, logLength, &logLength, log);
		android_log_print(ANDROID_LOG_DEBUG,TAG,"Shader compile log:\n%s", log);
		free(log);
	}
#endif

	glGetShaderiv(shader, GL_COMPILE_STATUS, &status);
	if (status == GL_FALSE) {
		glDeleteShader(shader);
		__android_log_print(ANDROID_LOG_DEBUG,TAG,"Failed to compile shader:\n");
		return 0;
	}

	return shader;
}
bool JNIMediaPlayerDrawer::prepareRender()
{
	if (_yuvtexture[0] == 0)
		return false;

	for (int i = 0; i < 3; ++i) {
		glActiveTexture(GL_TEXTURE0 + i);
		glBindTexture(GL_TEXTURE_2D, _yuvtexture[i]);
		glUniform1i(_uniformSamplers[i], i);
	}

	return true;
}
void JNIMediaPlayerDrawer::updateVertices(int frameWidth,int frameHeight)
{
	__android_log_print(ANDROID_LOG_INFO,TAG,"update vertices is called");
	const float width   = frameWidth;
	const float height  = frameHeight;
	const float dH      = (float)viewPortHeight / height;
	const float dW      = (float)viewPortWidth / width;
	const float dd      =  std::max(dH,dW);//std::min(dH, dW);
	__android_log_print(ANDROID_LOG_INFO,TAG,"dd value is %f",dd);
	const float h       = (height * dd / (float)viewPortHeight);
	const float w       = (width  * dd / (float)viewPortWidth );
	__android_log_print(ANDROID_LOG_INFO,TAG,"w = %f, h =%f",w,h);
	_vertices[0] = - w;
	_vertices[1] = - h;
	_vertices[2] =   w;
	_vertices[3] = - h;
	_vertices[4] = - w;
	_vertices[5] =   h;
	_vertices[6] =   w;
	_vertices[7] =   h;
#ifndef BROOV_NO_DEBUG_LOG
	__android_log_print(ANDROID_LOG_INFO,TAG,"end of update vertices");
	for(int i=0;i<8;i++){
		__android_log_print(ANDROID_LOG_INFO,TAG,"vertices[%d] = %f",i,_vertices[i]);
	}
#endif
}
void JNIMediaPlayerDrawer::mat4f_LoadOrtho(float left, float right, float bottom, float top, float near, float far, float* mout)
{
	float r_l = right - left;
	float t_b = top - bottom;
	float f_n = far - near;
	float tx = - (right + left) / (right - left);
	float ty = - (top + bottom) / (top - bottom);
	float tz = - (far + near) / (far - near);

	mout[0] = 2.0f / r_l;
	mout[1] = 0.0f;
	mout[2] = 0.0f;
	mout[3] = 0.0f;

	mout[4] = 0.0f;
	mout[5] = 2.0f / t_b;
	mout[6] = 0.0f;
	mout[7] = 0.0f;

	mout[8] = 0.0f;
	mout[9] = 0.0f;
	mout[10] = -2.0f / f_n;
	mout[11] = 0.0f;

	mout[12] = tx;
	mout[13] = ty;
	mout[14] = tz;
	mout[15] = 1.0f;
}
#endif
