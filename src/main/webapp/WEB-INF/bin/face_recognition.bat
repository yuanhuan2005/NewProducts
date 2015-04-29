@echo off

if "%1"=="" (
	echo Usage: %0 root_path postfix
	exit 1
) 

if "%2"=="" (
	echo Usage: %0 root_path postfix
	exit 1
) 

set postfix=%2
set face_log_file=%1/../../logs/face_log_%postfix%.txt

echo ################  begin to execute bat file  ################## >> %face_log_file%
echo %date% %time% >> %face_log_file%
%~d0%~p0\face_recognition_worker.bat %1 >> %face_log_file% 2>&1
exit %ERRORLEVEL%
