#include "SystemInformation.h"

JNIEXPORT jlong JNICALL
Java_com_vladium_utils_SystemInformation_getProcessCPUTime (JNIEnv * env, jclass cls)
{
    FILETIME creationTime, exitTime, kernelTime, userTime;

    GetProcessTimes (s_currentProcess, & creationTime, & exitTime, & kernelTime, & userTime);
    return (jlong) ((fileTimeToInt64 (& kernelTime) + fileTimeToInt64 (& userTime)) /
        (s_numberOfProcessors * 10000));
}

static HANDLE s_currentProcess;
static int s_numberOfProcessors;
JNIEXPORT jint JNICALL
JNI_OnLoad (JavaVM * vm, void * reserved)
{
    SYSTEM_INFO systemInfo;

    s_currentProcess = GetCurrentProcess ();
    GetSystemInfo (& systemInfo);
    s_numberOfProcessors = systemInfo.dwNumberOfProcessors;
    return JNI_VERSION_1_2;
}