LOCAL_PATH:= $(call my-dir)


include $(CLEAR_VARS)
LOCAL_SRC_FILES:= \
		src/cups_extension.c \
		src/cups_util.c \
		src/cups_check.c \
		src/cups_affix.c \
		
LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH)/src \

LOCAL_CFLAGS := -g -Wall
LOCAL_LDLIBS := -llog -lcups -lcupscgi
#LOCAL_SHARED_LIBRARIES := cups
LOCAL_MODULE:= extension
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_SRC_FILES:= \
		src/cups_util.c \
		src/cups_ppd.c
		
LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH)/src \

LOCAL_CFLAGS := -g -Wall
LOCAL_LDLIBS := -llog -lcups -lcupscgi
#LOCAL_SHARED_LIBRARIES := cups
LOCAL_MODULE:= persistent
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_SRC_FILES:= \
		src/cups_util.c \
		src/cups_printer.c

LOCAL_C_INCLUDES += \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH)/src \

LOCAL_CFLAGS := -g -Wall
LOCAL_LDLIBS := -llog -lcups -lcupscgi
LOCAL_MODULE:= printer
include $(BUILD_SHARED_LIBRARY)
