@echo off

if "%1"=="" (
	echo Usage: %0 root_path
	exit 1
) 

echo Step 1 FaceDetection ... && "U:\TCL_project\FaceRecognition\RunningEnvironment\Face Recognition 2.4.9\Face Recognition\bin\Debug\Face Recognition.exe" FaceDetection %1 FaceDetectionOutputParas.txt FaceDetectionInputParas.txt && echo Step 2 ObtainKeyFrames ... && java research.algorithm.abstraction.ROIBasedAbstractionMultiple ObtainKeyFrames %1 ObtainKeyFramesOutputParas.txt FaceDetectionOutputParas.txt && echo Step 3 ConvertStaticImsToTraining && "U:\TCL_project\FaceRecognition\RunningEnvironment\Face Recognition 2.4.9\Face Recognition\bin\Debug\Face Recognition.exe" ConvertStaticImsToTraining %1 null ObtainKeyFramesOutputParas.txt && echo Step 3.5 Email Notification ... && java com.tcl.t6.tracking.EmailNotification EmailNotification %1 null FaceDetectionInputParas.txt && echo Step 5 ComposeFaceVideo ... && java com.tcl.t6.tracking.ComposeFaceVideo ComposeFaceVideo %1 ComposeFaceVideoOutputParas.txt && echo Sleeping 300s && ping 127.0.0.1 -n 300 && echo Step 6 FaceRecognition ... && "U:\TCL_project\FaceRecognition\RunningEnvironment\Face Recognition 2.4.9\Face Recognition\bin\Debug\Face Recognition.exe" FaceRecognition %1 FaceRecognitionOutputParas.txt ComposeFaceVideoOutputParas.txt && echo Step 7 Tracking ... && java com.tcl.t6.tracking.Tracking Tracking %1 TrackingOutputParas.txt ImsContainFacesInOrder FaceRecognitionOutputParas.txt && echo Step 8 GroupingAndJason ... && java com.tcl.t6.tracking.GroupingAndConvertToJson GroupingAndJason %1 finalResult.txt FaceDetectionOutputParas.txt TrackingOutputParas.txt

echo All DONE
exit
