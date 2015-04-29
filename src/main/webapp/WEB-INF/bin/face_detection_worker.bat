@echo off

if "%1"=="" (
	echo Usage: %0 root_path
	exit 1
) 

echo Step 1 FaceDetection && "U:\TCL_project\FaceRecognition\RunningEnvironment\Face Recognition 2.4.9\Face Recognition\bin\Debug\Face Recognition.exe" FaceDetection %1 FaceDetectionOutputParas.txt FaceDetectionInputParas.txt && echo Step 2 ObtainKeyFrames && java research.algorithm.abstraction.ROIBasedAbstractionMultiple ObtainKeyFrames %1 ObtainKeyFramesOutputParas.txt FaceDetectionOutputParas.txt && echo Step 3 ConvertStaticImsToTraining && "U:\TCL_project\FaceRecognition\RunningEnvironment\Face Recognition 2.4.9\Face Recognition\bin\Debug\Face Recognition.exe" ConvertStaticImsToTraining %1 null ObtainKeyFramesOutputParas.txt

echo All DONE
exit
