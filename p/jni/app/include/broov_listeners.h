#ifndef BROOV_LISTENERS_H
#define BROOV_LISTENERS_H

#ifdef __cplusplus
extern "C" {
#endif

void broov_on_completion_listener();
void broov_on_prepared_listener();
void broov_on_buffering_listener(int percentage);
void broov_on_buffering_completion();
void de_init_listeners();
void delete_global_references();
void set_width_height(int width, int height);
#ifdef __cplusplus
}
#endif



#endif



