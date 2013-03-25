LOCAL_PATH:= $(call my-dir)

common_SRC_FILES:= \
	s2_meth.c \
	s2_srvr.c \
	s2_clnt.c \
	s2_lib.c \
	s2_enc.c \
	s2_pkt.c \
	s3_meth.c \
	s3_srvr.c \
	s3_clnt.c \
	s3_lib.c \
	s3_enc.c \
	s3_pkt.c \
	s3_both.c \
	s23_meth.c \
	s23_srvr.c \
	s23_clnt.c \
	s23_lib.c \
	s23_pkt.c \
	t1_meth.c \
	t1_srvr.c \
	t1_clnt.c \
	t1_lib.c \
	t1_enc.c \
	ssl_lib.c \
	ssl_err2.c \
	ssl_cert.c \
	ssl_sess.c \
	ssl_ciph.c \
	ssl_stat.c \
	ssl_rsa.c \
	ssl_asn1.c \
	ssl_txt.c \
	ssl_algs.c \
	bio_ssl.c \
	ssl_err.c \
	kssl.c

# static library
# =====================================================

#include $(CLEAR_VARS)
#LOCAL_SRC_FILES:= $(common_SRC_FILES)
#LOCAL_CFLAGS += -DOPENSSL_THREADS -D_REENTRANT -DDSO_DLFCN -DHAVE_DLFCN_H -DL_ENDIAN -DOPENSSL_NO_HW
#3LOCAL_CFLAGS += -DOPENSSL_NO_CAMELLIA -DOPENSSL_NO_CAST -DOPENSSL_NO_CMS -DOPENSSL_NO_GMP -DOPENSSL_NO_IDEA -DOPENSSL_NO_MDC2 -DOPENSSL_NO_RC5 -DOPENSSL_NO_RFC3779 -DOPENSSL_NO_SEED -DOPENSSL_NO_TLSEXT -DOPENSSL_NO_MD2 -DOPENSSL_NO_EC -DOPENSSL_NO_ECDH -DOPENSSL_NO_ECDSA -DOPENSSL_NO_OCSP
#LOCAL_C_INCLUDES:= $(common_C_INCLUDES)
#LOCAL_PRELINK_MODULE:= false
#LOCAL_STATIC_LIBRARIES := libcrypto-static
#LOCAL_SHARED_LIBRARIES := libcrypto
#LOCAL_MODULE:= libssl-static
#include $(BUILD_STATIC_LIBRARY)

# dynamic library
# =====================================================

include $(CLEAR_VARS)
LOCAL_MODULE:= ssl
LOCAL_SRC_FILES:= $(common_SRC_FILES)
LOCAL_CFLAGS += -DOPENSSL_THREADS -D_REENTRANT -DDSO_DLFCN -DHAVE_DLFCN_H -DL_ENDIAN -DOPENSSL_NO_HW
LOCAL_CFLAGS += -DOPENSSL_NO_CAMELLIA -DOPENSSL_NO_CAST -DOPENSSL_NO_CMS -DOPENSSL_NO_GMP -DOPENSSL_NO_IDEA -DOPENSSL_NO_MDC2 -DOPENSSL_NO_RC5 -DOPENSSL_NO_RFC3779 -DOPENSSL_NO_SEED -DOPENSSL_NO_TLSEXT -DOPENSSL_NO_MD2 -DOPENSSL_NO_EC -DOPENSSL_NO_ECDH -DOPENSSL_NO_ECDSA -DOPENSSL_NO_OCSP
LOCAL_EXPORT_C_INCLUDES:= openssl ../crypto
LOCAL_STATIC_LIBRARIES += crypto
include $(BUILD_SHARED_LIBRARY)
