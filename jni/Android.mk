LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)
LOCAL_SRC_FILES:= \
		src/cups_extension.c \

LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/include \
  
LOCAL_SHARED_LIBRARIES := cups
LOCAL_MODULE:= extension

include $(BUILD_SHARED_LIBRARY)
include $(LOCAL_PATH)/cups/Android.mk

