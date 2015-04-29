@echo off

if "%1"=="" (
	echo Usage: %0 root_path
	exit 1
) 

set log_file=%1/../../face_log.txt

%~d0%~p0\Face_thread.bat %1 > %log_file% 2>&1 
exit %ERRORLEVEL%
