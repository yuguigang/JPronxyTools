@echo off
set DIR="%~dp0"
set JAVA_EXEC="%DIR:"=%\java"



pushd %DIR% & %JAVA_EXEC% %CDS_JVM_OPTS% -XX:+UseZGC -XX:+ShowCodeDetailsInExceptionMessages -Dsun.java2d.opengl=true -Dhttps.protocols=TLSv1.1,TLSv1.2 -p "%~dp0/../app" -m jproxytools/com.ztoncloud.jproxytools.Launcher  %* & popd
