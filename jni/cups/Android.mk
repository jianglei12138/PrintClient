LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := libcups.so
LOCAL_MODULE:= cups
include $(PREBUILT_SHARED_LIBRARY)
