#ifndef BROOV_AUDIO_H
#define BROOV_AUDIO_H

#ifdef __cplusplus
extern "C" {
#endif

extern int audioBufferSize;
extern unsigned char *audioBuffer;

int BroovNDK_PauseAudio(void);
int BroovNDK_ResumeAudio(void);

int audio_open(int rate, int channels, int bits_per_sample, int bufSize);
void audio_close();
void audio_write();

void InitAudio();
void DeinitAudio();

#ifdef __cplusplus
}
#endif



#endif
